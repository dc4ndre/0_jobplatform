package com.jobhire.jobplatform.dto;

import java.time.LocalDateTime;

public class ApplicationResponseDTO {
    public Long id;
    public String status;
    public LocalDateTime appliedAt;

    public String jobTitle;

    public String applicantName;
    public String applicantEmail;
    public String applicantBio;

    public ApplicationResponseDTO(Long id, String status, LocalDateTime appliedAt,
                                  String jobTitle, String applicantName,
                                  String applicantEmail, String applicantBio) {
        this.id = id;
        this.status = status;
        this.appliedAt = appliedAt;
        this.jobTitle = jobTitle;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantBio = applicantBio;
    }
}
