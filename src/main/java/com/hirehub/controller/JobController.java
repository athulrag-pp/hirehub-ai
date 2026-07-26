package com.hirehub.controller;

import com.hirehub.dto.JobPostingDto;
import com.hirehub.entity.JobPosting;
import com.hirehub.entity.User;
import com.hirehub.service.ApplicationService;
import com.hirehub.service.JobPostingService;
import com.hirehub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobPostingService jobPostingService;
    private final UserService userService;
    private final ApplicationService applicationService;

    // ── Public job listing ────────────────────────────────────────────────────

    @GetMapping("/jobs")
    public String listJobs(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        model.addAttribute("jobs", jobPostingService.searchJobs(keyword, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        return "jobs/list";
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        JobPosting job = jobPostingService.findById(id);
        model.addAttribute("job", job);
        if (userDetails != null) {
            model.addAttribute("hasApplied",
                    applicationService.hasApplied(
                            userService.findByEmail(userDetails.getUsername()), id));
        }
        return "jobs/detail";
    }

    // ── Employer job management ───────────────────────────────────────────────

    @GetMapping("/employer/jobs/new")
    public String newJobForm(Model model) {
        model.addAttribute("jobDto", new JobPostingDto());
        return "jobs/form";
    }

    @PostMapping("/employer/jobs")
    public String createJob(@Valid @ModelAttribute("jobDto") JobPostingDto dto,
                            BindingResult result,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (result.hasErrors()) return "jobs/form";
        User employer = userService.findByEmail(userDetails.getUsername());
        jobPostingService.create(dto, employer);
        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/employer/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User employer = userService.findByEmail(userDetails.getUsername());
        JobPosting job = jobPostingService.findById(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            return "redirect:/dashboard";
        }
        JobPostingDto dto = new JobPostingDto();
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setLocation(job.getLocation());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setDeadline(job.getDeadline());
        model.addAttribute("jobDto", dto);
        model.addAttribute("jobId", id);
        return "jobs/form";
    }

    @PostMapping("/employer/jobs/{id}")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute("jobDto") JobPostingDto dto,
                            BindingResult result,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "jobs/form";
        User employer = userService.findByEmail(userDetails.getUsername());
        jobPostingService.update(id, dto, employer);
        redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/employer/jobs/{id}/toggle")
    public String toggleJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User employer = userService.findByEmail(userDetails.getUsername());
        jobPostingService.toggleActive(id, employer);
        redirectAttributes.addFlashAttribute("success", "Job status updated.");
        return "redirect:/dashboard";
    }

    @PostMapping("/employer/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        User employer = userService.findByEmail(userDetails.getUsername());
        jobPostingService.delete(id, employer);
        redirectAttributes.addFlashAttribute("success", "Job deleted.");
        return "redirect:/dashboard";
    }

    @GetMapping("/employer/jobs/{id}/applications")
    public String viewApplications(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        User employer = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("job", jobPostingService.findById(id));
        model.addAttribute("applications", applicationService.findByJobPosting(id, employer));
        return "applications/employer-list";
    }
}
