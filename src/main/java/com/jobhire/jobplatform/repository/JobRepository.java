package com.jobhire.jobplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobhire.jobplatform.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
