package org.bigmouth.senon.scheduler.job;

import org.bigmouth.senon.commom.job.JobInfoRequest;
import org.bigmouth.senon.commom.job.JobInfoResponse;
import org.bigmouth.senon.commom.model.*;
import org.bigmouth.senon.commom.registry.WorkerRegistry;
import org.bigmouth.senon.commom.selector.Selector;
import org.bigmouth.senon.commom.worker.Worker;
import org.bigmouth.senon.scheduler.AppContextBeans;
import org.bigmouth.senon.scheduler.service.JobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class DistributedJob implements Job {

	private final RestTemplate restTemplate = new RestTemplate();

	private final WorkerRegistry workerRegistry;
	private final Selector selector;
	private final JobService jobService;
	private final JobManager jobManager;

	public DistributedJob() {
		this.workerRegistry = AppContextBeans.getBean(WorkerRegistry.class);
		this.selector = AppContextBeans.getBean(Selector.class);
		this.jobService = AppContextBeans.getBean(JobService.class);
		this.jobManager = AppContextBeans.getBean(JobManager.class);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		List<Worker> workers = workerRegistry.getServices();

		Long jobId = (Long) context.getJobDetail().getJobDataMap().get("JOB_ID");
		
		JobEntity jobEntity = jobService.getByJobId(jobId);
		String script = jobEntity.getScript();
		JobType jobType = jobEntity.getJobType();
		
		String cmdPrefix = "";
		String filePostfix = "";
		switch (jobType) {
		case HIVE:
			cmdPrefix = "hive -f";
			filePostfix = ".hive";
			break;
		case SHELL:
			cmdPrefix = "";
			if (System.getProperties().getProperty("os.name")
					.contains("Windows")) {
				filePostfix = ".cmd";
			} else {
				filePostfix = ".sh";
			}
			break;
		case PYTHON:
			cmdPrefix = "python";
			filePostfix = ".py";
			break;
		}
		TriggerType triggerType = (TriggerType) context.getJobDetail().getJobDataMap().get("TRIGGER_TYPE");

		JobHistoryEntity historyEntity = new JobHistoryEntity();
		historyEntity.setStartTime(new Timestamp(new Date().getTime()));
		historyEntity.setTriggerType(triggerType);
		historyEntity.setResult(JobRunResult.RUNNING);
		StringBuilder sb = new StringBuilder("任务开始");

		historyEntity.setJob(jobService.getByJobId(jobId));
		jobService.saveLog(historyEntity);

		sb.append("正在分配Worker\n");
		Worker worker = selector.select(workers);

		boolean jobSendSucceed = false;
		try {
			while (worker != null && !workers.isEmpty()) {
				// 给Worker发送任务信息
				JobInfoRequest request = new JobInfoRequest(jobId, historyEntity.getId(), cmdPrefix, filePostfix, script, triggerType);
				String url = worker.getUrl(Worker.URI_JOB_RECEIVER);
				JobInfoResponse rs = restTemplate.postForObject(url, request, JobInfoResponse.class);
				if (rs == null || !rs.isSucceed()) {
					sb.append(worker.getHostNameForHttpProtocol());
					sb.append("无效,正在重试\n");
					workers.remove(worker);
					worker = selector.select(workers);
				} else {
					sb.append(worker.getHostNameForHttpProtocol());
					sb.append("有效\n");
					historyEntity.setExecutionMachine(worker.getHostNameForHttpProtocol());
					jobSendSucceed = true;
					break;
				}
			}
		} catch (Exception e) {
			sb.append("任务失败,原因是:\n");
			sb.append(e.getMessage()).append("\n");
			historyEntity.setContent(sb.toString());
			setFailed(historyEntity);
		}
		if (!jobSendSucceed) {
			sb.append("任务失败,原因是:\n");
			sb.append("无可用Worker,任务失败!\n");
			historyEntity.setContent(sb.toString());
			setFailed(historyEntity);
		} else {
			sb.append("任务已经发送,等待Worker执行!\n");
			historyEntity.setContent(sb.toString());
			jobService.saveLog(historyEntity);
			addToRunningList(historyEntity);
		}
	}

	private void setFailed(JobHistoryEntity history) {
		jobManager.setJobFailed(history);
	}

	private void addToRunningList(JobHistoryEntity history) {
		jobManager.addToRunningList(history);
	}

}
