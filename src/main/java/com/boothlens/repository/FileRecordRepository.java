package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.FileRecord;

import java.util.List;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Integer> {
    List<FileRecord> findByReferenceCodeOrderByCreatedAtDesc(String referenceCode);
}
