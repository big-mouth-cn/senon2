package org.bigmouth.senon.admin.service;

import org.bigmouth.senon.commom.model.FileEntity;
import org.bigmouth.senon.commom.model.JobEntity;
import org.bigmouth.senon.commom.model.JobHistoryEntity;
import org.bigmouth.senon.commom.registry.WorkerRegistry;
import org.bigmouth.senon.commom.repository.FileRepository;
import org.bigmouth.senon.commom.repository.JobHistoryRepository;
import org.bigmouth.senon.commom.repository.JobRepository;
import org.bigmouth.senon.commom.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component("jobService")
@Transactional
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private JobHistoryRepository jobHistoryRepository;
    @Autowired
    private WorkerRegistry workerRegistry;

    public JobEntity getByJobId(Long jobId) {
        return jobRepository.findOne(jobId);
    }

    public JobEntity getByFileId(Long fileId) {
        return jobRepository.findOneByFile(new FileEntity(fileId));
    }

    public List<JobHistoryEntity> getJobHistoryList(Long jobId) {
        return jobHistoryRepository
                .findTop10ByJobOrderByStartTimeDesc(new JobEntity(jobId));
    }

    public void updateJob(JobEntity modifiedJob) {
        jobRepository.save(modifiedJob);
    }

    public void delete(Long jobId) {
        JobEntity job = jobRepository.findOne(jobId);
        jobRepository.delete(job);
    }

    public List<Worker> listWorkers() {
        Iterable<Worker> ite = workerRegistry.getServices();
        Iterator<Worker> it = ite.iterator();
        List<Worker> rs = new ArrayList<Worker>();
        while (it.hasNext()) {
            rs.add(it.next());
        }
        return rs;
    }

}
