package com.hirehub.config;

import com.hirehub.dto.ApplicationDto;
import com.hirehub.dto.JobPostingDto;
import com.hirehub.dto.RegisterDto;
import com.hirehub.entity.JobPosting;
import com.hirehub.entity.Role;
import com.hirehub.entity.User;
import com.hirehub.repository.UserRepository;
import com.hirehub.service.ApplicationService;
import com.hirehub.service.JobPostingService;
import com.hirehub.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JobPostingService jobPostingService;
    private final ApplicationService applicationService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with data.");
            return;
        }

        log.info("Seeding initial demo data for HireHub AI...");

        // 1. Create Employer User
        RegisterDto employerDto = new RegisterDto();
        employerDto.setFullName("Sarah Jenkins");
        employerDto.setEmail("employer@hirehub.com");
        employerDto.setPassword("password123");
        employerDto.setConfirmPassword("password123");
        employerDto.setRole(Role.ROLE_EMPLOYER);
        employerDto.setCompanyName("TechVision AI");
        employerDto.setPhone("+1 (555) 019-2834");
        User employer = userService.register(employerDto);

        // 2. Create Job Seeker User
        RegisterDto seekerDto = new RegisterDto();
        seekerDto.setFullName("Alex Rivera");
        seekerDto.setEmail("jobseeker@hirehub.com");
        seekerDto.setPassword("password123");
        seekerDto.setConfirmPassword("password123");
        seekerDto.setRole(Role.ROLE_JOBSEEKER);
        seekerDto.setPhone("+1 (555) 847-1920");
        User seeker = userService.register(seekerDto);

        // 3. Create Sample Job Postings
        JobPostingDto job1 = new JobPostingDto();
        job1.setTitle("Senior Full Stack Java Engineer");
        job1.setCompany("TechVision AI");
        job1.setLocation("Remote");
        job1.setSalary("$130,000 - $160,000");
        job1.setJobType("Full-time");
        job1.setExperienceLevel("Senior");
        job1.setDescription("We are seeking an experienced Java & Spring Boot engineer to lead the development of our enterprise AI dashboard platform. You will build high-performance REST APIs, integrate microservices, and collaborate closely with product managers.");
        job1.setRequirements("• 5+ years Java & Spring Boot experience\n• Proficiency with HTML5, CSS3, JavaScript, and Thymeleaf/React\n• PostgreSQL / MySQL database optimization\n• Microservices & Docker knowledge");
        job1.setDeadline(LocalDateTime.now().plusDays(30));
        JobPosting createdJob1 = jobPostingService.create(job1, employer);

        JobPostingDto job2 = new JobPostingDto();
        job2.setTitle("AI / Machine Learning Engineer");
        job2.setCompany("TechVision AI");
        job2.setLocation("San Francisco, CA");
        job2.setSalary("$150,000 - $190,000");
        job2.setJobType("Full-time");
        job2.setExperienceLevel("Senior");
        job2.setDescription("Join our core AI team to design, train, and deploy next-generation LLM application workflows. Work with vector databases, agentic pipelines, and real-time prompt optimization.");
        job2.setRequirements("• Strong Python and Java background\n• Experience with PyTorch, LangChain, or LLM fine-tuning\n• Knowledge of RESTful APIs & Cloud deployment (AWS / Render / GCP)");
        job2.setDeadline(LocalDateTime.now().plusDays(45));
        jobPostingService.create(job2, employer);

        JobPostingDto job3 = new JobPostingDto();
        job3.setTitle("Frontend UI/UX Specialist");
        job3.setCompany("Designify Labs");
        job3.setLocation("New York, NY (Hybrid)");
        job3.setSalary("$95,000 - $120,000");
        job3.setJobType("Full-time");
        job3.setExperienceLevel("Mid");
        job3.setDescription("Craft sleek, highly accessible, and visually stunning web interfaces for modern SaaS applications. Focus on user experience, smooth animations, and responsive web design.");
        job3.setRequirements("• 3+ years experience with modern JavaScript, CSS, and Bootstrap\n• Portfolio demonstrating clean visual design aesthetics\n• Familiarity with Thymeleaf or modern frontend tools");
        job3.setDeadline(LocalDateTime.now().plusDays(20));
        jobPostingService.create(job3, employer);

        // 4. Create Sample Application
        ApplicationDto appDto = new ApplicationDto();
        appDto.setCoverLetter("Hi Sarah,\n\nI am thrilled to apply for the Senior Full Stack Java Engineer role. With over 6 years of experience scaling Spring Boot applications and designing responsive interfaces, I am confident I can make an immediate impact at TechVision AI.\n\nBest regards,\nAlex Rivera");
        appDto.setResumeUrl("https://github.com/alexrivera-dev/resume.pdf");
        applicationService.apply(seeker, createdJob1.getId(), appDto);

        log.info("Demo data initialized successfully!");
        log.info("Default Accounts:\n  Employer: employer@hirehub.com / password123\n  JobSeeker: jobseeker@hirehub.com / password123");
    }
}
