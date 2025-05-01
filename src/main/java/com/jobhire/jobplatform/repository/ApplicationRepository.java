package com.jobhire.jobplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobhire.jobplatform.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
