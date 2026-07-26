package com.hirehub.controller;

import com.hirehub.dto.ApplicationDto;
import com.hirehub.entity.ApplicationStatus;
import com.hirehub.entity.User;
import com.hirehub.service.ApplicationService;
import com.hirehub.service.JobPostingService;
import com.hirehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobPostingService jobPostingService;
    private final UserService userService;

    @GetMapping("/applications/apply/{jobId}")
    public String applyForm(@PathVariable Long jobId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User applicant = userService.findByEmail(userDetails.getUsername());
        if (applicationService.hasApplied(applicant, jobId)) {
            return "redirect:/jobs/" + jobId + "?alreadyApplied=true";
        }
        model.addAttribute("job", jobPostingService.findById(jobId));
        model.addAttribute("appDto", new ApplicationDto());
        model.addAttribute("user", applicant);
        return "applications/apply";
    }

    @PostMapping("/applications/apply/{jobId}")
    public String submitApplication(@PathVariable Long jobId,
                                    @ModelAttribute("appDto") ApplicationDto dto,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        User applicant = userService.findByEmail(userDetails.getUsername());
        try {
            applicationService.apply(applicant, jobId, dto);
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        User applicant = userService.findByEmail(userDetails.getUsername());
        applicationService.withdraw(id, applicant);
        redirectAttributes.addFlashAttribute("success", "Application withdrawn.");
        return "redirect:/dashboard";
    }

    @PostMapping("/employer/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ApplicationStatus status,
                               @RequestParam Long jobId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        User employer = userService.findByEmail(userDetails.getUsername());
        applicationService.updateStatus(id, status, employer);
        redirectAttributes.addFlashAttribute("success", "Status updated to " + status.name());
        return "redirect:/employer/jobs/" + jobId + "/applications";
    }
}
