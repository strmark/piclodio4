package nl.oradev.piclodio.controller;

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

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AlarmController {
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);
    private static final String ALARM = "Alarm";
    private static final String ALARM_JOBS= "Alarm-jobs";
    private static final String PICLODIO = "Piclodio_";

    private AlarmRepository alarmRepository;

    private WebradioRepository webradioRepository;

    private Scheduler scheduler;

    public AlarmController(AlarmRepository alarmRepository, WebradioRepository webradioRepository, Scheduler scheduler) {
        this.alarmRepository = alarmRepository;
        this.webradioRepository = webradioRepository;
        this.scheduler = scheduler;
    }

    @GetMapping(path = "/alarms")
    public List<Alarm> getAllAlarm() {
        return alarmRepository.findAll();
    }

    @PostMapping(path = "/alarms")
    public Alarm createAlarm(@Valid @RequestBody AlarmDTO alarmDTO) {
        Alarm alarm = new Alarm();
        alarm.setMinute(alarmDTO.getMinute());
        alarm.setHour(alarmDTO.getHour());
        alarm.setName(alarmDTO.getName());
        alarm.setMonday(alarmDTO.isMonday());
        alarm.setTuesday(alarmDTO.isTuesday());
        alarm.setWednesday(alarmDTO.isWednesday());
        alarm.setThursday(alarmDTO.isThursday());
        alarm.setFriday(alarmDTO.isFriday());
        alarm.setSaturday(alarmDTO.isSaturday());
        alarm.setSunday(alarmDTO.isSunday());
        alarm.setAutoStopMinutes(alarmDTO.getAutoStopMinutes());
        alarm.setActive(alarmDTO.isActive());
        alarm.setWebradio(alarmDTO.getWebradioId());
        return alarmRepository.save(alarm);
    }

    @GetMapping(path = "/alarms/{id}")
    public Alarm getAlarmById(@PathVariable(value = "id") Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));
    }

    @PutMapping(path = "/alarms/{id}")
    public Alarm updateAlarm(@PathVariable(value = "id") Long alarmId,
                             @Valid @RequestBody AlarmDTO alarmDetails) {
        //0 45 6 ? * MON,TUE,WED,THU,FRI *
        String cronSchedule;
        String cronDays = "";
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));

        alarm.setMinute(alarmDetails.getMinute());
        cronSchedule = "0 " + alarmDetails.getMinute() + " ";
        alarm.setHour(alarmDetails.getHour());
        cronSchedule = cronSchedule + alarmDetails.getHour() + " ? * ";
        alarm.setName(alarmDetails.getName());
        alarm.setMonday(alarmDetails.isMonday());
        if (alarmDetails.isMonday())
            cronDays = stringConcat(cronDays, "MON");
        alarm.setTuesday(alarmDetails.isTuesday());
        if (alarmDetails.isTuesday())
            cronDays = stringConcat(cronDays, "TUE");
        alarm.setWednesday(alarmDetails.isWednesday());
        if (alarmDetails.isWednesday())
            cronDays = stringConcat(cronDays, "WED");
        alarm.setThursday(alarmDetails.isThursday());
        if (alarmDetails.isThursday())
            cronDays = stringConcat(cronDays, "THU");
        alarm.setFriday(alarmDetails.isFriday());
        if (alarmDetails.isFriday())
            cronDays = stringConcat(cronDays, "FRI");
        alarm.setSaturday(alarmDetails.isSaturday());
        if (alarmDetails.isSaturday())
            cronDays = stringConcat(cronDays, "SAT");
        alarm.setSunday(alarmDetails.isSunday());
        if (alarmDetails.isSunday())
            cronDays = stringConcat(cronDays, "SUN");
        alarm.setAutoStopMinutes(alarmDetails.getAutoStopMinutes());
        cronDays = cronDays + " *";
        alarm.setActive(alarmDetails.isActive());
        alarm.setWebradio(alarmDetails.getWebradioId());
        Alarm updatedAlarm = alarmRepository.save(alarm);
        cronSchedule = cronSchedule + cronDays;

        try {
            Optional<Webradio> webradioOptional = webradioRepository.findById(alarmDetails.getWebradioId());
            Webradio webradio = null;
            if (webradioOptional.isPresent()) {
                webradio = webradioOptional.get();
            }

            JobKey jobkey = new JobKey(PICLODIO + alarmDetails.getWebradioId(), ALARM_JOBS);
            if (scheduler.checkExists(jobkey)) {
                logger.info("Already exists");
                scheduler.deleteJob(jobkey);
            }
            if (alarmDetails.isActive()) {
                //JobDetail jobDetail = buildJobDetail(alarmDetails.getName()+'_'+alarmDetails.getWebradio()
                JobDetail jobDetail = buildJobDetail(PICLODIO + alarmDetails.getWebradioId()
                        , alarmDetails.getWebradioId()
                        , (long) alarmDetails.getAutoStopMinutes()
                        , webradio!=null?webradio.getUrl():"dummy");

                Trigger trigger = buildJobTrigger(jobDetail, cronSchedule, ZonedDateTime.now());

                scheduler.scheduleJob(jobDetail, trigger);

                new ScheduleAlarmResponse(true,
                        jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Alarm Scheduled Successfully!");
            }
        } catch (SchedulerException ex) {
            logger.error("Error scheduling Alarm", ex);
            new ScheduleAlarmResponse(false,
                    "Error scheduling Alarm. Please try later!");
        }
        return updatedAlarm;
    }

    @DeleteMapping(path = "/alarms/{id}")
    public ResponseEntity<Long> deleteAlarm(@PathVariable(value = "id") Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException(ALARM, "id", alarmId));

        try {
            JobKey jobkey = new JobKey("PICLODIO" + alarm.getWebradio(), ALARM_JOBS);
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

    private String stringConcat(String cronDays, String day) {
        if (cronDays.isEmpty())
            return day;
        else
            return cronDays + "," + day;
    }

    private JobDetail buildJobDetail(String alarmName, Long webradio, Long autoStopMinutes, String url) {
        JobDataMap jobDataMap = new JobDataMap();

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
