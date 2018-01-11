package org.bigmouth.senon.scheduler.service;

import org.bigmouth.senon.commom.model.FileEntity;
import org.bigmouth.senon.commom.model.JobEntity;
import org.bigmouth.senon.commom.model.JobHistoryEntity;
import org.bigmouth.senon.commom.repository.FileRepository;
import org.bigmouth.senon.commom.repository.JobHistoryRepository;
import org.bigmouth.senon.commom.repository.JobRepository;
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

    public JobEntity getByJobId(Long jobId) {
        return jobRepository.findOne(jobId);
    }

    public JobEntity getByFileId(Long fileId) {
        return jobRepository.findOneByFile(new FileEntity(fileId));
    }
    
    public List<JobEntity> getAllJobs() {
    	Iterator<JobEntity> ite = jobRepository.findAll().iterator();
    	List<JobEntity> jobList = new ArrayList<JobEntity>();
    	while(ite.hasNext()){
    		jobList.add(ite.next());
    	}

        return jobList;
    }

    public List<JobHistoryEntity> getJobHistoryList(Long jobId) {
        return jobHistoryRepository.findByJob(new JobEntity(jobId));
    }

    public JobHistoryEntity getJobHistory(Long historyId) {
        return jobHistoryRepository.findOne(historyId);
    }

    public void saveLog(JobHistoryEntity log){
    	jobHistoryRepository.save(log);
    }
    
    public void updateJob(JobEntity modifiedJob) {
        jobRepository.save(modifiedJob);
    }

    public void delete(Long jobId) {
        JobEntity job = jobRepository.findOne(jobId);
        jobRepository.delete(job);
    }
}
