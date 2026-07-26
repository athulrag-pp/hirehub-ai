package com.hirehub.repository;

import com.hirehub.entity.Application;
import com.hirehub.entity.JobPosting;
import com.hirehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicantOrderByAppliedAtDesc(User applicant);

    List<Application> findByJobPostingOrderByAppliedAtDesc(JobPosting jobPosting);

    Optional<Application> findByApplicantAndJobPosting(User applicant, JobPosting jobPosting);

    boolean existsByApplicantAndJobPosting(User applicant, JobPosting jobPosting);

    long countByJobPosting(JobPosting jobPosting);
}
