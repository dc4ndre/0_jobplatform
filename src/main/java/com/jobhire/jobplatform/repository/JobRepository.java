package com.jobhire.jobplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobhire.jobplatform.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByPostedById(Long recruiterId);
}

