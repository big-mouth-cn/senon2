package org.bigmouth.senon.worker.controller;

import org.bigmouth.senon.commom.job.JobInfoRequest;
import org.bigmouth.senon.commom.job.JobInfoResponse;
import org.bigmouth.senon.worker.WorkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WorkerController {

    @Autowired
    private WorkerManager workerManager;

	@RequestMapping("/job_receiver")
	public JobInfoResponse job_receiver(@RequestBody JobInfoRequest jobInfo) {
        workerManager.addJob(jobInfo);
        return new JobInfoResponse(true,"SUCCESS");
	}

    @RequestMapping("/get_log")
    public String get_log(@RequestBody Long logId) {
        return workerManager.getRunningJobLog(logId);
    }
}
