package org.bigmouth.senon.admin.controller;

import org.apache.commons.collections.MapUtils;
import org.bigmouth.senon.admin.convert.CaseInsensitiveConverter;
import org.bigmouth.senon.admin.service.JobService;
import org.bigmouth.senon.commom.model.*;
import org.bigmouth.senon.commom.registry.SchedulerRegistry;
import org.bigmouth.senon.commom.registry.Service;
import org.bigmouth.senon.commom.scheduler.Scheduler;
import org.bigmouth.senon.commom.selector.Selector;
import org.bigmouth.senon.commom.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/job")
public class JobController {
	private Logger logger = LoggerFactory.getLogger(JobController.class);
	@Autowired
	private JobService jobService;

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private SchedulerRegistry schedulerRegistry;
	@Autowired
	private Selector selector;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(AllocationType.class,
				new CaseInsensitiveConverter<AllocationType>(
						AllocationType.class));
		binder.registerCustomEditor(JobType.class,
				new CaseInsensitiveConverter<JobType>(JobType.class));
		binder.registerCustomEditor(ScheduleStatus.class,
				new CaseInsensitiveConverter<ScheduleStatus>(
						ScheduleStatus.class));
		binder.registerCustomEditor(ScheduleType.class,
				new CaseInsensitiveConverter<ScheduleType>(ScheduleType.class));
		binder.registerCustomEditor(FileType.class,
				new CaseInsensitiveConverter<FileType>(FileType.class));
	}

	@RequestMapping(value = "get_by_file_id")
	@ResponseBody
	public JobEntity get_by_file_id(Long fileId) {
		return jobService.getByFileId(fileId);
	}

	@RequestMapping(value = "get_history_list")
	@ResponseBody
	public List<JobHistoryEntity> get_history_list(Long jobId) {
		return jobService.getJobHistoryList(jobId);
	}

	@RequestMapping(value = "update_job")
	@ResponseBody
	public CommonResponse update_job(Long id, String name, JobType jobType,
			ScheduleType scheduleType, String cron, String dependencies,
			String script, AllocationType allocationType,
			String executionMachine) {
		try {
			JobEntity job = jobService.getByJobId(id);
			job.setName(name);
			job.setJobType(jobType);
			job.setScheduleType(scheduleType);
			job.setCron(cron);
			job.setDependencies(dependencies);
			job.setScript(script);
			job.setAllocationType(allocationType);
			job.setExecutionMachine(executionMachine);
			job.getFile().setName(job.getName());
			jobService.updateJob(job);
			return CommonResponse.SUCCESS();
		} catch (Exception e) {
			return CommonResponse.FAILED(e.getMessage());
		}
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public CommonResponse delete(Long jobId) {
		try {
			jobService.delete(jobId);
			return CommonResponse.SUCCESS();
		} catch (Exception e) {
			return CommonResponse.FAILED(e.getMessage());
		}
	}

	@RequestMapping(value = "trigger_job")
	@ResponseBody
	public CommonResponse trigger_job(@RequestParam(required = true) Long jobId) {
		try {
			MultiValueMap<String, Long> params = new LinkedMultiValueMap<String, Long>();
			params.add("jobId", jobId);

			Scheduler service = getScheduler();
			if (null == service) {
				return CommonResponse.FAILED("Could not found any scheduler service!");
			}
			Map<String, Object> rs = restTemplate.postForObject(service.getUrl(Scheduler.URI_TRIGGER_JOB), params, Map.class);

			if (MapUtils.isNotEmpty(rs) && (Boolean) rs.get("op_result") == true) {
				return CommonResponse.SUCCESS((String) rs.get("job_status"));
			} else {
				return CommonResponse.FAILED((String) rs.get("job_status"));
			}
		} catch (Exception e) {
			return CommonResponse.FAILED(e.getMessage());
		}
	}

	@RequestMapping(value = "manualrun")
	@ResponseBody
	public CommonResponse manualrun(@RequestParam(required = true) Long jobId) {
		try {
			MultiValueMap<String, Long> params = new LinkedMultiValueMap<String, Long>();
			params.add("jobId", jobId);

			Scheduler service = getScheduler();
			if (null == service) {
				return CommonResponse.FAILED("Could not found any scheduler service!");
			}
			return restTemplate.postForObject(service.getUrl(Scheduler.URI_MANUAL_RUN_JOB),
					params, CommonResponse.class);
		} catch (Exception e) {
			return CommonResponse.FAILED(e.getMessage());
		}
	}

	@RequestMapping(value = "resumerun")
	@ResponseBody
	public CommonResponse resumerun(@RequestParam(required = true) Long jobId) {
		try {
			MultiValueMap<String, Long> params = new LinkedMultiValueMap<String, Long>();
			params.add("jobId", jobId);
			Scheduler service = getScheduler();
			if (null == service) {
				return CommonResponse.FAILED("Could not found any scheduler service!");
			}
			return restTemplate.postForObject(service.getUrl(Scheduler.URI_RESUME_RUN_JOB),
					params, CommonResponse.class);
		} catch (Exception e) {
			return CommonResponse.FAILED(e.getMessage());
		}
	}

	@RequestMapping(value = "gethistorylog")
	public @ResponseBody LogStatus gethistorylog(Long historyId) {
		MultiValueMap<String, Long> params = new LinkedMultiValueMap<String, Long>();
		params.add("logId", historyId);
		Scheduler service = getScheduler();
		if (null == service) {
			return new LogStatus("ERROR", "Could not found any scheduler service!");
		}
		return restTemplate.postForObject(
				service.getUrl(Scheduler.URI_GET_LOG), params,
				LogStatus.class);
	}

	@RequestMapping(value = "dependency_info")
	public @ResponseBody Object[] dependency_info(Long id) {

		Set<DependencyJobInfoWebBean> jobList = new HashSet<DependencyJobInfoWebBean>();
		List<DependencyInfoWebBean> relations = new ArrayList<DependencyInfoWebBean>();

		JobEntity smallestJob = jobService.getByJobId(id);

		if (ScheduleType.DEPENDENCY == smallestJob.getScheduleType()) {
			getDependency(jobList, relations, smallestJob);
		} else {
			List<JobHistoryEntity> his = jobService
					.getJobHistoryList(smallestJob.getId());
			if (!his.isEmpty()) {
				jobList.add(new DependencyJobInfoWebBean(smallestJob.getFile()
						.getId().toString(), smallestJob.getName(), DateUtils
						.format(his.get(0).getStartTime().getTime(),
								"yyyy-MM-dd HH:mm:ss"),
						his.get(0).getResult() == JobRunResult.SUCCESS));
			} else {
				jobList.add(new DependencyJobInfoWebBean(smallestJob.getFile()
						.getId().toString(), smallestJob.getName(), "", false));
			}
		}
		return new Object[] { jobList, relations };
	}

	private void getDependency(Set<DependencyJobInfoWebBean> jobList,
			List<DependencyInfoWebBean> relations, JobEntity job) {
		List<JobHistoryEntity> his = jobService.getJobHistoryList(job.getId());
		if (!his.isEmpty()) {
			jobList.add(new DependencyJobInfoWebBean(job.getFile().getId()
					.toString(), job.getName(), DateUtils.format(his.get(0)
					.getStartTime().getTime(), "yyyy-MM-dd HH:mm:ss"), his.get(
					0).getResult() == JobRunResult.SUCCESS));
		} else {
			jobList.add(new DependencyJobInfoWebBean(job.getFile().getId()
					.toString(), job.getName(), "", false));
		}

		for (String dependency : job.getDependencies().split(",")) {
			JobEntity dep = jobService.getByFileId(Long.valueOf(dependency));
			relations.add(new DependencyInfoWebBean(job.getFile().getId()
					.toString(), dependency));

			if (ScheduleType.DEPENDENCY == dep.getScheduleType()) {
				getDependency(jobList, relations, dep);
			} else {
				List<JobHistoryEntity> dhis = jobService.getJobHistoryList(dep
						.getId());
				if (!dhis.isEmpty()) {
					jobList.add(new DependencyJobInfoWebBean(dep.getFile()
							.getId().toString(), dep.getName(), DateUtils
							.format(dhis.get(0).getStartTime().getTime(),
									"yyyy-MM-dd HH:mm:ss"), dhis.get(0)
							.getResult() == JobRunResult.SUCCESS));
				} else {
					jobList.add(new DependencyJobInfoWebBean(dep.getFile()
							.getId().toString(), dep.getName(), "", false));
				}
			}
		}
	}

	private Scheduler getScheduler() {
		List<Scheduler> services = schedulerRegistry.getServices();
		return selector.select(services);
	}

}
