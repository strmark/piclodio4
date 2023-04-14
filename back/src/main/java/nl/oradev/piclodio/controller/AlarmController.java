package nl.oradev.piclodio.controller;

import jakarta.validation.Valid;
import nl.oradev.piclodio.dto.AlarmDTO;
import nl.oradev.piclodio.exception.ResourceNotFoundException;
import nl.oradev.piclodio.job.AlarmJob;
import nl.oradev.piclodio.model.Alarm;
import nl.oradev.piclodio.model.Webradio;
import nl.oradev.piclodio.payload.ScheduleAlarmResponse;
import nl.oradev.piclodio.repository.AlarmRepository;
import nl.oradev.piclodio.repository.WebradioRepository;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
public class AlarmController {
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);
    private static final String ALARM = "Alarm";
    private static final String ALARM_JOBS = "Alarm-jobs";
    private static final String PICLODIO = "Piclodio_";

    private final AlarmRepository alarmRepository;
    private final WebradioRepository webradioRepository;
    private final Scheduler scheduler;

    public AlarmController(AlarmRepository alarmRepository, WebradioRepository webradioRepository, Scheduler scheduler) {
        this.alarmRepository = alarmRepository;
        this.webradioRepository = webradioRepository;
        this.scheduler = scheduler;
    }

    @GetMapping(path = "/alarms/")
    public List<Alarm> getAllAlarm() {
        return alarmRepository.findAll();
    }

    @PostMapping(path = "/alarms/")
    public Alarm createAlarm(@Valid @RequestBody AlarmDTO alarmDTO) {
        return saveAlarm(alarmDTO, null);
    }

    @GetMapping(path = "/alarms/{id}")
    public Alarm getAlarmById(@PathVariable(value = "id") Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));
    }

    @PutMapping(path = "/alarms/{id}")
    public Alarm updateAlarm(@PathVariable(value = "id") Long alarmId,
                             @Valid @RequestBody AlarmDTO alarmDetails) {
        scheduleAlarm(alarmDetails.webradio()
                , alarmDetails.active()
                , (long) alarmDetails.autoStopMinutes()
                , getCronSchedule(alarmDetails));

        return saveAlarm(alarmDetails, alarmId);
    }

    @DeleteMapping(path = "/alarms/{id}")
    public ResponseEntity<Long> deleteAlarm(@PathVariable(value = "id") Long alarmId) {
        var alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));

        try {
            var jobkey = new JobKey("PICLODIO" + alarm.getWebradio(), ALARM_JOBS);
            if (scheduler.checkExists(jobkey)) {
                logger.info("Delete schedule");
                scheduler.deleteJob(jobkey);
            }
        } catch (SchedulerException ex) {
            logger.error("Error scheduling Alarm", ex);
        }
        alarmRepository.delete(alarm);
        return ResponseEntity.ok().build();
    }

    private void scheduleAlarm(Long webradioId, boolean isActive, Long autoStopMinutes, String cronSchedule) {
        try {
            Optional<Webradio> webradioOptional = webradioRepository.findById(webradioId);
            Webradio webradio = null;
            if (webradioOptional.isPresent()) {
                webradio = webradioOptional.get();
            }

            var jobkey = new JobKey(PICLODIO + webradioId, ALARM_JOBS);
            if (scheduler.checkExists(jobkey)) {
                logger.info("Already exists");
                scheduler.deleteJob(jobkey);
            }
            if (isActive) {
                //JobDetail jobDetail = buildJobDetail(alarmDetails.getName()+'_'+alarmDetails.getWebradio()
                var jobDetail = buildJobDetail(PICLODIO + webradioId
                        , webradioId
                        , autoStopMinutes
                        , webradio != null ? webradio.getUrl() : "dummy");

                var trigger = buildJobTrigger(jobDetail, cronSchedule, ZonedDateTime.now());

                scheduler.scheduleJob(jobDetail, trigger);

                new ScheduleAlarmResponse(true,
                        jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Alarm Scheduled Successfully!");
            }
        } catch (SchedulerException ex) {
            logger.error("Error scheduling Alarm", ex);
            new ScheduleAlarmResponse(false,
                    "Error scheduling Alarm. Please try later!");
        }
    }

    private String getCronSchedule(AlarmDTO alarmDetails) {
        //0 45 6 ? * MON,TUE,WED,THU,FRI *
        String cronSchedule = "0 "
                + alarmDetails.minute() + " "
                + alarmDetails.hour()
                + " ? * ";
        var cronDays = "";
        cronDays = stringAppend(cronDays, alarmDetails.monday(), "MON");
        cronDays = stringAppend(cronDays, alarmDetails.tuesday(), "TUE");
        cronDays = stringAppend(cronDays, alarmDetails.wednesday(), "WED");
        cronDays = stringAppend(cronDays, alarmDetails.thursday(), "THU");
        cronDays = stringAppend(cronDays, alarmDetails.friday(), "FRI");
        cronDays = stringAppend(cronDays, alarmDetails.saturday(), "SAT");
        cronDays = stringAppend(cronDays, alarmDetails.sunday(), "SUN");
        return cronSchedule + cronDays + " *";
    }

    private String stringAppend(String cronDays, boolean isDay, String day) {
        if (isDay) {
            return (cronDays.isEmpty() ? day : cronDays + "," + day);
        }
        return "";
    }

    private Alarm saveAlarm(AlarmDTO alarmDTO, Long alarmId) {
        var alarm = new Alarm();
        if (alarmId != null) {
            alarm = alarmRepository.findById(alarmId)
                    .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));
        }
        alarm.setMinute(alarmDTO.minute());
        alarm.setHour(alarmDTO.hour());
        alarm.setName(alarmDTO.name());
        alarm.setMonday(alarmDTO.monday());
        alarm.setTuesday(alarmDTO.tuesday());
        alarm.setWednesday(alarmDTO.wednesday());
        alarm.setThursday(alarmDTO.thursday());
        alarm.setFriday(alarmDTO.friday());
        alarm.setSaturday(alarmDTO.saturday());
        alarm.setSunday(alarmDTO.sunday());
        alarm.setAutoStopMinutes(alarmDTO.autoStopMinutes());
        alarm.setActive(alarmDTO.active());
        alarm.setWebradio(alarmDTO.webradio());
        return alarmRepository.save(alarm);
    }

    private JobDetail buildJobDetail(String alarmName, Long webradio, Long autoStopMinutes, String url) {
        var jobDataMap = new JobDataMap();
        jobDataMap.put("webradio", webradio);
        jobDataMap.put("autoStopMinutes", autoStopMinutes);
        jobDataMap.put("url", url);

        return JobBuilder.newJob(AlarmJob.class)
                .withIdentity(alarmName, ALARM_JOBS)
                .withDescription("Alarm Job")
                .usingJobData(jobDataMap)
                .storeDurably(true)
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, String cronSchedule, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "Alarm-triggers")
                .withDescription("Alarm Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
                .build();
    }
}
