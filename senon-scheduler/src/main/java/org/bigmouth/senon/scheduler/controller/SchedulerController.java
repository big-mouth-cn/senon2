package org.bigmouth.senon.scheduler.controller;

import org.bigmouth.senon.commom.model.CommonResponse;
import org.bigmouth.senon.commom.model.JobHistoryEntity;
import org.bigmouth.senon.commom.model.JobRunResult;
import org.bigmouth.senon.commom.model.LogStatus;
import org.bigmouth.senon.commom.job.JobInfoResponse;
import org.bigmouth.senon.scheduler.job.JobManager;
import org.bigmouth.senon.scheduler.service.JobService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class SchedulerController {
    @Autowired
    private JobService jobService;
    @Autowired
    private JobManager jobManager;

    @RequestMapping("/job_response")
    public boolean job_response(@RequestBody JobInfoResponse jobResponse) {
    	JobHistoryEntity his = jobService.getJobHistory(jobResponse.getHistoryId());
        if (his == null) {
            return false;
        }
        String end = jobResponse.isSucceed() ? "\nJob Run Success." : "\nJob Run Failed.";
        his.setResult(jobResponse.isSucceed() ? JobRunResult.SUCCESS : JobRunResult.FAILED);
        his.setContent(his.getContent() + "\n" + jobResponse.getMessage() + end);
        
    	if(jobResponse.isSucceed()){
    		jobManager.setJobSucceed(his,jobResponse.isScheduledJob());
    	}else{
    		jobManager.setJobFailed(his);
    	}
        return true;
    }

    @RequestMapping("/trigger_job")
    public Map<String,Object> trigger_job(@RequestParam(required=true) Long jobId) {
        Map<String,Object> rs = new HashMap<String, Object>();
        rs.put("job_status","OFF");
        rs.put("op_result",Boolean.FALSE);
        if(jobManager.isScheduled(jobId)){
            try {
                jobManager.removeScheduledJob(jobId);
                rs.put("job_status","OFF");
            } catch (SchedulerException e) {
                return rs;
            }
        }else{
            try {
                jobManager.scheduleJob(jobId);
                rs.put("job_status","ON");
            } catch (SchedulerException e) {
                return rs;
            }
        }
        rs.put("op_result",Boolean.TRUE);
        return rs;
    }
    
    @RequestMapping("/manual_run_job")
    public CommonResponse manual_run_job(@RequestParam(required=true) Long jobId) {
        try {
			jobManager.manualJob(jobId);
			return CommonResponse.SUCCESS();
		} catch (SchedulerException e1) {
			return CommonResponse.FAILED(e1.getMessage());
		}
    }
    
    @RequestMapping("/resume_run_job")
    public CommonResponse resume_run_job(@RequestParam(required=true) Long jobId) {
        try {
			jobManager.resumeJob(jobId);
			return CommonResponse.SUCCESS();
		} catch (SchedulerException e1) {
			return CommonResponse.FAILED(e1.getMessage());
		}
    }
    
    @RequestMapping("/get_log")
    public @ResponseBody LogStatus get_log(@RequestParam(required=true) Long logId) {
    	return jobManager.getRunningJobLog(logId);
    }
    
    @RequestMapping("/send_log")
    public boolean send_log(@RequestParam(required=true) Long logId,String log) {
    	jobManager.refreshLog(logId,log);
    	return true;
    }
    
    
}
