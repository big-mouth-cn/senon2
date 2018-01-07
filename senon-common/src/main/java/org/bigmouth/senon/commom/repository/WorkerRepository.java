package org.bigmouth.senon.commom.repository;

import org.bigmouth.senon.commom.model.WorkerEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface WorkerRepository extends CrudRepository<WorkerEntity,Long> {
	public List<WorkerEntity> findByNameAndUrl(String name, String url);
}
