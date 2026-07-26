package com.hirehub.service;

import com.hirehub.dto.JobPostingDto;
import com.hirehub.entity.JobPosting;
import com.hirehub.entity.User;
import com.hirehub.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    public Page<JobPosting> getActiveJobs(int page, int size) {
        return jobPostingRepository.findByActiveTrueOrderByPostedAtDesc(
                PageRequest.of(page, size));
    }

    public Page<JobPosting> searchJobs(String keyword, int page, int size) {
        if (keyword == null || keyword.isBlank()) {
            return getActiveJobs(page, size);
        }
        return jobPostingRepository.searchByKeyword(keyword.trim(),
                PageRequest.of(page, size));
    }

    public JobPosting findById(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
    }

    public List<JobPosting> findByEmployer(User employer) {
        return jobPostingRepository.findByEmployerOrderByPostedAtDesc(employer);
    }

    @Transactional
    public JobPosting create(JobPostingDto dto, User employer) {
        JobPosting job = JobPosting.builder()
                .title(dto.getTitle())
                .company(dto.getCompany())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .requirements(dto.getRequirements())
                .salary(dto.getSalary())
                .jobType(dto.getJobType())
                .experienceLevel(dto.getExperienceLevel())
                .deadline(dto.getDeadline())
                .employer(employer)
                .active(true)
                .build();
        return jobPostingRepository.save(job);
    }

    @Transactional
    public JobPosting update(Long id, JobPostingDto dto, User employer) {
        JobPosting job = findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new SecurityException("Not authorized to edit this job");
        }
        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setLocation(dto.getLocation());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setSalary(dto.getSalary());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setDeadline(dto.getDeadline());
        return jobPostingRepository.save(job);
    }

    @Transactional
    public void toggleActive(Long id, User employer) {
        JobPosting job = findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new SecurityException("Not authorized");
        }
        job.setActive(!job.isActive());
        jobPostingRepository.save(job);
    }

    @Transactional
    public void delete(Long id, User employer) {
        JobPosting job = findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new SecurityException("Not authorized to delete this job");
        }
        jobPostingRepository.delete(job);
    }
}
