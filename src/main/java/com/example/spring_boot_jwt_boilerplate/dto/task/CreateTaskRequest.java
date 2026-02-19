package com.example.spring_boot_jwt_boilerplate.dto.task;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaskRequest {
    private String title;
    private String content;
}
