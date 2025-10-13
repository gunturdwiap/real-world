package com.example.realworld.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 2, max = 255)
    private String username;

    @Size(min = 8, max = 255)
    private String password;

    private String image;
    
    private String bio;

}

