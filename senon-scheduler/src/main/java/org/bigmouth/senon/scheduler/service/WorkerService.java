package org.bigmouth.senon.scheduler.service;

import org.bigmouth.senon.commom.model.WorkerEntity;
import org.bigmouth.senon.commom.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component("workerService")
@Transactional
public class WorkerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerService.class);

    @Autowired
    private WorkerRepository workerRepository;

    public List<WorkerEntity> findAll() {
        Iterator<WorkerEntity> ite = workerRepository.findAll().iterator();
        List<WorkerEntity> workerList = new ArrayList<WorkerEntity>();
        while (ite.hasNext()) {
            workerList.add(ite.next());
        }
        return workerList;
    }

    public WorkerEntity get(Long id) {
        return workerRepository.findOne(id);
    }
}
