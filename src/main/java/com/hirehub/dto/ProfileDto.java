package com.hirehub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    private String companyName;

    private String resumeUrl;

    private String bio;
}
