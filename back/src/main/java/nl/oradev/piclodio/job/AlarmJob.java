package nl.oradev.piclodio.job;

import nl.oradev.piclodio.controller.PlayerController;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Component
public class AlarmJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(AlarmJob.class);

    private PlayerController playerController;

    public AlarmJob(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long autoStopMinutes = jobDataMap.getLong("autoStopMinutes");
        String url = jobDataMap.getString("url");

        logger.info("Start player");

        playerController.startPlayer(url, autoStopMinutes);
    }

}
