package com.moonlightharmony.fishingmapback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SignUpRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 10, max = 50)
    private String userId;

    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 50)
    private String password;


    private String phoneNumber;

    private String language;

    private String useYn;

    private String note;

}
