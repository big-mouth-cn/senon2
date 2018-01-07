package org.bigmouth.senon.commom.repository;

import org.bigmouth.senon.commom.model.FileEntity;
import org.bigmouth.senon.commom.model.JobEntity;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<JobEntity,Long> {
    JobEntity findOneByFile(FileEntity file);
}
