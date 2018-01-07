package org.bigmouth.senon.commom.repository;

import org.bigmouth.senon.commom.model.FileEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository extends CrudRepository<FileEntity,Long> {
    List<FileEntity> findByParent(FileEntity parent);
    List<FileEntity> findByParent(FileEntity parent, Sort sort);
}
