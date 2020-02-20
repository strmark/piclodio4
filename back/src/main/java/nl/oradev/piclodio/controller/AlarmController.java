package nl.oradev.piclodio.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/alarms")
public class AlarmController {
    private static final Logger logger = LoggerFactory.getLogger(AlarmController.class);

    private AlarmRepository alarmRepository;

    private WebradioRepository webradioRepository;

    private Scheduler scheduler;

    public AlarmController(AlarmRepository alarmRepository, WebradioRepository webradioRepository, Scheduler scheduler) {
        this.alarmRepository = alarmRepository;
        this.webradioRepository = webradioRepository;
        this.scheduler = scheduler;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Alarm> getAllAlarm() {
        return alarmRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Alarm createAlarm(@Valid @RequestBody Alarm alarm) {
        return alarmRepository.save(alarm);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Alarm getAlarmById(@PathVariable(value = "id") Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm", "id", alarmId));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Alarm updateAlarm(@PathVariable(value = "id") Long alarmId,
                                           @Valid @RequestBody Alarm alarmDetails) {

        //0 45 6 ? * MON,TUE,WED,THU,FRI *
        String cronSchedule = "10 * * * * ?";
        String cronDays = "";
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm", "id", alarmId));

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
            cronDays = stringConcat( cronDays ,"TUE");
        alarm.setWednesday(alarmDetails.isWednesday());
        if (alarmDetails.isWednesday())
            cronDays = stringConcat( cronDays ,"WED");
        alarm.setThursday(alarmDetails.isThursday());
        if (alarmDetails.isThursday())
            cronDays = stringConcat( cronDays ,"THU");
        alarm.setFriday(alarmDetails.isFriday());
        if (alarmDetails.isFriday())
            cronDays = stringConcat( cronDays ,"FRI");
        alarm.setSaturday(alarmDetails.isSaturday());
        if (alarmDetails.isSaturday())
            cronDays = stringConcat( cronDays ,"SAT");
        alarm.setSunday(alarmDetails.isSunday());
        if (alarmDetails.isSunday())
            cronDays = stringConcat( cronDays ,"SUN");
        alarm.setAuto_stop_minutes(alarmDetails.getAuto_stop_minutes());
        cronDays = cronDays + " *";
        alarm.setIs_active(alarmDetails.isIs_active());
        alarm.setWebradio(alarmDetails.getWebradio());
        Alarm updatedAlarm = alarmRepository.save(alarm);
        cronSchedule = cronSchedule + cronDays;

        try {
            Optional <Webradio> webradioOptional = webradioRepository.findById(alarmDetails.getWebradio());
            Webradio webradio  = webradioOptional.get();
            JobKey jobkey = new JobKey("Piclodio_"+alarmDetails.getWebradio(), "Alarm-jobs");
            if (scheduler.checkExists(jobkey)){
                logger.info("Already exists");
                scheduler.deleteJob(jobkey);
            }
            if (alarmDetails.isIs_active()) {
                //JobDetail jobDetail = buildJobDetail(alarmDetails.getName()+'_'+alarmDetails.getWebradio()
                JobDetail jobDetail = buildJobDetail("Piclodio_" + alarmDetails.getWebradio()
                        , alarmDetails.getWebradio()
                        , (long) alarmDetails.getAuto_stop_minutes()
                        , webradio.getUrl());

                Trigger trigger = buildJobTrigger(jobDetail, cronSchedule, ZonedDateTime.now());

                scheduler.scheduleJob(jobDetail, trigger);

                ScheduleAlarmResponse scheduleAlarmResponse = new ScheduleAlarmResponse(true,
                        jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Alarm Scheduled Successfully!");
            }
        } catch (SchedulerException ex) {
            logger.error("Error scheduling Alarm", ex);

            ScheduleAlarmResponse scheduleAlarmResponse = new ScheduleAlarmResponse(false,
                    "Error scheduling Alarm. Please try later!");
        }
        return updatedAlarm;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAlarm(@PathVariable(value = "id") Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new ResourceNotFoundException("Alarm", "id", alarmId));

        try {
            JobKey jobkey = new JobKey("Piclodio_"+alarm.getWebradio(), "Alarm-jobs");
            if (scheduler.checkExists(jobkey)){
                logger.info("Delete schedule");
                scheduler.deleteJob(jobkey);
            }
        } catch (SchedulerException ex) {
            logger.error("Error scheduling Alarm", ex);
        }
        alarmRepository.delete(alarm);

        return ResponseEntity.ok().build();
    }

    private String stringConcat(String cronDays, String day){
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
                .withIdentity(alarmName, "Alarm-jobs")
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
