package org.bigmouth.senon.commom.repository;

import org.bigmouth.senon.commom.model.JobEntity;
import org.bigmouth.senon.commom.model.JobHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobHistoryRepository extends CrudRepository<JobHistoryEntity,Long> {
    List<JobHistoryEntity> findByJob(JobEntity file);

    List<JobHistoryEntity> findTop10ByJobOrderByStartTimeDesc(JobEntity job);
}
