package com.jobhire.jobplatform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobhire.jobplatform.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantId(Long applicantId);
    List<Application> findByRecruiterId(Long recruiterId);
    List<Application> findByJobId(Long jobId);
    List<Application> findByJobIdAndRecruiterId(Long jobId, Long recruiterId);
    List<Application> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    List<Application> findByApplicantIdAndRecruiterId(Long applicantId, Long recruiterId);
    List<Application> findByApplicantIdAndJobId(Long applicantId, Long jobId);
    List<Application> findByApplicantIdAndJobIdAndRecruiterId(Long applicantId, Long jobId, Long recruiterId);
    List<Application> findByApplicantIdAndJobIdAndRecruiterIdAndStatus(Long applicantId, Long jobId, Long recruiterId, String status);
    List<Application> findByApplicantIdAndJobIdAndStatus(Long applicantId, Long jobId, String status);
    List<Application> findByApplicantIdAndRecruiterIdAndStatus(Long applicantId, Long recruiterId, String status);
    List<Application> findByJobIdAndRecruiterIdAndStatus(Long jobId, Long recruiterId, String status);
    List<Application> findByJobIdAndApplicantIdAndStatus(Long jobId, Long applicantId, String status);
    List<Application> findByRecruiterIdAndStatus(Long recruiterId, String status);
    List<Application> findByApplicantIdAndStatus(Long applicantId, String status);
    List<Application> findByJobIdAndStatus(Long jobId, String status);
    List<Application> findByStatus(String status);
    boolean existsByApplicantIdAndJobId(Long applicantId, Long jobId);
    List<Application> findByJobPostedById(Long recruiterId);
        
    
    

}

// Compare this snippet from src/main/java/com/jobhire/jobplatform/service/ApplicationService.java:
// package com.jobhire.jobplatform.service;