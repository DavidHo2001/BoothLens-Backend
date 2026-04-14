package com.boothlens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boothlens.entity.FileRecord;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Integer> {
}
