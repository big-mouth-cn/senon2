package org.bigmouth.senon.admin.controller;

import org.bigmouth.senon.admin.service.JobService;
import org.bigmouth.senon.commom.model.WorkerEntity;
import org.bigmouth.senon.commom.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/workers")
public class WorkerController {
	@Autowired
	private JobService jobService;

	@RequestMapping(value = "list")
	@ResponseBody
	public List<Worker> list() {
		return jobService.listWorkers();
	}
}
