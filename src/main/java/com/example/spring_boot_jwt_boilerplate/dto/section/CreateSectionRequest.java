package com.example.spring_boot_jwt_boilerplate.dto.section;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSectionRequest {
    private String name;
}