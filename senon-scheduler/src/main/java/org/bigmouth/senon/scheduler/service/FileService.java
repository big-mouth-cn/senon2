package org.bigmouth.senon.scheduler.service;

import org.bigmouth.senon.commom.model.FileEntity;
import org.bigmouth.senon.commom.model.FileType;
import org.bigmouth.senon.commom.model.JobEntity;
import org.bigmouth.senon.commom.repository.FileRepository;
import org.bigmouth.senon.commom.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component("fileService")
@Transactional
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private JobRepository jobRepository;

    public Long save(FileEntity fileEntity) {
        Long fileId = fileRepository.save(fileEntity).getId();
        if (fileEntity.getFileType() == FileType.FILE) {
            jobRepository.save(JobEntity.getDefault(fileEntity));
        }
        return fileId;
    }

    public FileEntity findOne(Long id) {
        return fileRepository.findOne(id);
    }

    public List<FileEntity> listFilesByParent(Long parent) {
        if (parent == null) {
            return fileRepository.findByParent(null);
        } else {
            return fileRepository.findByParent(new FileEntity(parent));
        }
    }

    public List<FileEntity> listFilesByParent(Long parent, String[]... sortProperties) {

        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        for (String[] sp : sortProperties) {
            orders.add(new Sort.Order(Sort.Direction.fromString(sp[1]), sp[0]));
        }
        Sort sort = new Sort(orders);

        if (parent == null) {
            return fileRepository.findByParent(null, sort);
        } else {
            return fileRepository.findByParent(new FileEntity(parent), sort);
        }
    }

    public void delete(Long id) {
        fileRepository.delete(id);
    }
}
