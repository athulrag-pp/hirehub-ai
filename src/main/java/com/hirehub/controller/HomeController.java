package com.hirehub.controller;

import com.hirehub.dto.ProfileDto;
import com.hirehub.entity.Role;
import com.hirehub.entity.User;
import com.hirehub.service.ApplicationService;
import com.hirehub.service.JobPostingService;
import com.hirehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobPostingService jobPostingService;
    private final UserService userService;
    private final ApplicationService applicationService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredJobs", jobPostingService.getActiveJobs(0, 6));
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);

        if (user.getRole() == Role.ROLE_EMPLOYER) {
            var jobs = jobPostingService.findByEmployer(user);
            model.addAttribute("myJobs", jobs);
            model.addAttribute("activeJobCount",  jobs.stream().filter(com.hirehub.entity.JobPosting::isActive).count());
            model.addAttribute("inactiveJobCount", jobs.stream().filter(j -> !j.isActive()).count());
            return "dashboard/employer";
        } else {
            var apps = applicationService.findByApplicant(user);
            model.addAttribute("myApplications", apps);
            model.addAttribute("underReviewCount",   apps.stream().filter(a -> a.getStatus().name().equals("PENDING") || a.getStatus().name().equals("REVIEWED")).count());
            model.addAttribute("shortlistedCount",   apps.stream().filter(a -> a.getStatus().name().equals("SHORTLISTED") || a.getStatus().name().equals("HIRED")).count());
            return "dashboard/jobseeker";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        if (!model.containsAttribute("profileDto")) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setFullName(user.getFullName());
            profileDto.setPhone(user.getPhone());
            profileDto.setCompanyName(user.getCompanyName());
            profileDto.setResumeUrl(user.getResumeUrl());
            profileDto.setBio(user.getBio());
            model.addAttribute("profileDto", profileDto);
        }
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute ProfileDto dto,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        userService.updateProfile(userDetails.getUsername(), dto);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }
}
