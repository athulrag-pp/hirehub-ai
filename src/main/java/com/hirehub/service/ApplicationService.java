package com.hirehub.service;

import com.hirehub.dto.ApplicationDto;
import com.hirehub.entity.*;
import com.hirehub.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingService jobPostingService;

    public List<Application> findByApplicant(User applicant) {
        return applicationRepository.findByApplicantOrderByAppliedAtDesc(applicant);
    }

    public List<Application> findByJobPosting(Long jobId, User employer) {
        JobPosting job = jobPostingService.findById(jobId);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new SecurityException("Not authorized to view these applications");
        }
        return applicationRepository.findByJobPostingOrderByAppliedAtDesc(job);
    }

    public boolean hasApplied(User applicant, Long jobId) {
        JobPosting job = jobPostingService.findById(jobId);
        return applicationRepository.existsByApplicantAndJobPosting(applicant, job);
    }

    @Transactional
    public Application apply(User applicant, Long jobId, ApplicationDto dto) {
        JobPosting job = jobPostingService.findById(jobId);
        if (applicationRepository.existsByApplicantAndJobPosting(applicant, job)) {
            throw new IllegalStateException("You have already applied to this job");
        }
        Application application = Application.builder()
                .applicant(applicant)
                .jobPosting(job)
                .coverLetter(dto.getCoverLetter())
                .resumeUrl(dto.getResumeUrl())
                .status(ApplicationStatus.PENDING)
                .build();
        return applicationRepository.save(application);
    }

    @Transactional
    public Application updateStatus(Long applicationId, ApplicationStatus status, User employer) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (!application.getJobPosting().getEmployer().getId().equals(employer.getId())) {
            throw new SecurityException("Not authorized");
        }
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    @Transactional
    public void withdraw(Long applicationId, User applicant) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (!application.getApplicant().getId().equals(applicant.getId())) {
            throw new SecurityException("Not authorized");
        }
        applicationRepository.delete(application);
    }
}
