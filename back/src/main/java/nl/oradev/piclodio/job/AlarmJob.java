package nl.oradev.piclodio.job;

import nl.oradev.piclodio.controller.PlayerController;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AlarmJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(AlarmJob.class);

    @Autowired
    private PlayerController playerController;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long autoStopMinutes = jobDataMap.getLong("autoStopMinutes");
        String url = jobDataMap.getString("url");

        System.out.println("Start player");

        playerController.startPlayer(url, autoStopMinutes);
    }

}
