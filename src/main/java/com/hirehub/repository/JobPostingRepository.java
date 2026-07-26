package com.hirehub.repository;

import com.hirehub.entity.JobPosting;
import com.hirehub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Page<JobPosting> findByActiveTrueOrderByPostedAtDesc(Pageable pageable);

    List<JobPosting> findByEmployerOrderByPostedAtDesc(User employer);

    @Query("SELECT j FROM JobPosting j WHERE j.active = true AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<JobPosting> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT j FROM JobPosting j WHERE j.active = true AND " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<JobPosting> findByLocationContaining(@Param("location") String location, Pageable pageable);
}
