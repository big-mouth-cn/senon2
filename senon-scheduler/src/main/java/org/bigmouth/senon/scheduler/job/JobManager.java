package org.bigmouth.senon.scheduler.job;

import com.google.common.collect.Lists;
import org.bigmouth.senon.commom.model.*;
import org.bigmouth.senon.scheduler.service.JobService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Component
public class JobManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    private static final String TRIGGER_OF_JOB = "trigger_of_job_";

    private Scheduler scheduler;
    private Lock runningJobsLock = new ReentrantLock();
    private List<JobHistoryEntity> runningJobs = new LinkedList<JobHistoryEntity>();
    private Lock scheduledJobIdsLock = new ReentrantLock();
    private List<Long> scheduledJobIds = new LinkedList<Long>();
    private Map<Long, String> logHolder = new HashMap<Long, String>();

    private Map<Long, List<Long>> dependencyInfoMap = new HashMap<Long, List<Long>>();
    private Lock workingDepMapLock = new ReentrantLock();
    private Map<Long, List<Long>> workingDepMap = new HashMap<Long, List<Long>>();

    @Autowired
    private JobService jobService;

    @PostConstruct
    void init() throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        scheduler = sf.getScheduler();
        scheduler.start();

        initJobs();
        initMonitorOfJobTimeout();
    }

    private void initMonitorOfJobTimeout() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {

                        List<JobHistoryEntity> jobHistoryEntities = Lists.newArrayList();
                        runningJobsLock.lock();
                        try {
                            jobHistoryEntities.addAll(runningJobs);
                        } finally {
                            runningJobsLock.unlock();
                        }

                        // 遍历CPY寻找过期的，然后去数据库比对
                        for (JobHistoryEntity log : jobHistoryEntities) {
                            long start = log.getStartTime().getTime();
                            long cost = System.currentTimeMillis() - start;
                            if (cost > 7200000) {
                                // 超过2小时
                                JobHistoryEntity dbLog = jobService
                                        .getJobHistory(log.getId());
                                if (dbLog.getEndTime() == null) {
                                    killJob(dbLog);
                                    notifyFailedJob(dbLog);
                                }
                            }
                        }

                        Thread.sleep(60 * 1000);
                    } catch (Exception ignore) {
                    }
                }
            }
        }).start();
    }

    private void initJobs() throws SchedulerException {
        for (JobEntity job : jobService.getAllJobs()) {
            if (ScheduleStatus.ON == job.getScheduleStatus()) {
                scheduleJob(job);
            }
        }
    }

    public boolean isScheduled(Long jobId) {
        scheduledJobIdsLock.lock();
        try {
            return this.scheduledJobIds.contains(jobId);
        } finally {
            scheduledJobIdsLock.unlock();
        }
    }

    public void scheduleJob(Long jobId) throws SchedulerException {
        JobEntity jobEntity = jobService.getByJobId(jobId);
        scheduleJob(jobEntity);
    }

    private void scheduleJob(JobEntity jobEntity) throws SchedulerException {
        JobDetail jobDetail = initJobDetail(jobEntity);

        if (ScheduleType.CRON == jobEntity.getScheduleType()) {
            CronTrigger trigger = newTrigger()
                    .withIdentity(TRIGGER_OF_JOB + jobEntity.getId())
                    .withSchedule(cronSchedule(jobEntity.getCron())).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // dependency
            String[] dpIds = jobEntity.getDependencies().split(",");
            dependencyInfoMap.put(jobEntity.getId(), new ArrayList<Long>(dpIds.length));
            for (String id : dpIds) {
                dependencyInfoMap.get(jobEntity.getId()).add(jobService.getByFileId(Long.valueOf(id)).getId());
            }
            Collections.sort(dependencyInfoMap.get(jobEntity.getId()));
        }

        scheduledJobIdsLock.lock();
        try {
            scheduledJobIds.add(jobEntity.getId());
        } finally {
            scheduledJobIdsLock.unlock();
        }

        jobEntity.setScheduleStatus(ScheduleStatus.ON);
        jobService.updateJob(jobEntity);
    }

    private JobDetail initJobDetail(JobEntity jobEntity) {
        JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(String.valueOf(jobEntity.getId())).build();
        jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
        jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.AUTO);
        return jobDetail;
    }

    public void removeScheduledJob(Long jobId) throws SchedulerException {
        scheduler.deleteJob(new JobKey(jobId.toString()));
        dependencyInfoMap.remove(jobId);
        workingDepMap.remove(jobId);
        scheduledJobIdsLock.lock();
        try {
            scheduledJobIds.remove(jobId);
        } finally {
            scheduledJobIdsLock.unlock();
        }

        JobEntity job = jobService.getByJobId(jobId);
        job.setScheduleStatus(ScheduleStatus.OFF);
        jobService.updateJob(job);
    }

    /**
     * 恢复一个Job, 这个job和手工启动的job的不同之处是,它会触发依赖与他的job的执行
     *
     * @param jobId 这个job必须是已经存在于quartz中
     * @throws SchedulerException
     */
    public void resumeJob(Long jobId) throws SchedulerException {
        JobEntity jobEntity = jobService.getByJobId(jobId);
        JobDetail jobDetail = initJobDetail(jobEntity);
        scheduler.addJob(jobDetail, true, true);
        scheduler.triggerJob(jobDetail.getKey());
    }

    public void manualJob(Long jobId) throws SchedulerException {
        JobEntity jobEntity = jobService.getByJobId(jobId);

        JobDetail jobDetail = newJob(DistributedJob.class).withIdentity(
                "manual_job_" + jobId).build();
        jobDetail.getJobDataMap().put("JOB_ID", jobEntity.getId());
        jobDetail.getJobDataMap().put("TRIGGER_TYPE", TriggerType.MANUAL);

        Trigger runNowTrigger = newTrigger()
                .withIdentity("manual_trigger_run_now_job_" + jobId).startNow()
                .withSchedule(simpleSchedule()).build();

        scheduler.scheduleJob(jobDetail, runNowTrigger);
    }

    public void setJobFailed(JobHistoryEntity historyEntity) {
        historyEntity.setEndTime(new Timestamp(new Date().getTime()));
        historyEntity.setResult(JobRunResult.FAILED);
        jobService.saveLog(historyEntity);

        logHolder.remove(historyEntity.getId());

        removeFromRunningList(historyEntity);
        notifyFailedJob(historyEntity);
    }

    public void setJobSucceed(JobHistoryEntity historyEntity,
                              boolean isScheduledJob) {
        historyEntity.setEndTime(new Timestamp(new Date().getTime()));
        historyEntity.setResult(JobRunResult.SUCCESS);
        jobService.saveLog(historyEntity);

        logHolder.remove(historyEntity.getId());

        removeFromRunningList(historyEntity);

        // 检查任务依赖并触发
        if (isScheduledJob) {
            Long jobId = historyEntity.getJob().getId();

            for (Long id : dependencyInfoMap.keySet()) {
                if (dependencyInfoMap.get(id).contains(jobId)) {
                    workingDepMapLock.lock();
                    try {
                        if (workingDepMap.containsKey(id)) {
                            workingDepMap.get(id).add(jobId);
                            Collections.sort(workingDepMap.get(id));
                        } else {
                            workingDepMap.put(id, new ArrayList<Long>());
                            workingDepMap.get(id).add(jobId);
                        }

                        if (workingDepMap.get(id).equals(
                                dependencyInfoMap.get(id))) {
                            // 触发一个任务
                            try {
                                resumeJob(id);
                            } catch (SchedulerException e) {

                            }
                            workingDepMap.remove(id);
                        }
                    } finally {
                        workingDepMapLock.unlock();
                    }
                }
            }
        }
    }

    public LogStatus getRunningJobLog(Long id) {
        LogStatus log = new LogStatus();
        boolean running = false;
        runningJobsLock.lock();
        try {
            if (runningJobs.contains(new JobHistoryEntity(id))) {
                running = true;
            }
        } finally {
            runningJobsLock.unlock();
        }

        if (!running) {
            JobHistoryEntity his = jobService.getJobHistory(id);
            log.setStatus(his.getResult() == JobRunResult.SUCCESS ? "SUCCESS"
                    : "FAILED");
            log.setLog(his.getContent());
        } else {
            log.setStatus("RUNNING");
            log.setLog(logHolder.get(id));
        }
        return log;
    }

    void addToRunningList(JobHistoryEntity log) {
        runningJobsLock.lock();
        try {
            runningJobs.add(log);
        } finally {
            runningJobsLock.unlock();
        }
    }

    private void removeFromRunningList(JobHistoryEntity log) {
        runningJobsLock.lock();
        try {
            runningJobs.remove(log);
        } finally {
            runningJobsLock.unlock();
        }
        log.setEndTime(new Timestamp(new Date().getTime()));
        jobService.saveLog(log);
    }

    private void notifyFailedJob(JobHistoryEntity historyEntity) {
        JobEntity job = jobService.getByJobId(historyEntity.getJob().getId());
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error("任务运行失败! \n{}", job.getId() + "\n" + job.getName() + "\n" + historyEntity.getContent());
        }
    }

    private void killJob(JobHistoryEntity log) {
        log.setContent(log.getContent() + "\n任务超时,Killed!!");
        log.setEndTime(new Timestamp(new Date().getTime()));
        log.setResult(JobRunResult.FAILED);
        jobService.saveLog(log);
    }

    public void refreshLog(Long logId, String log) {
        this.logHolder.put(logId, log);
    }

}
