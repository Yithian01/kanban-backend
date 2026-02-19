package com.example.spring_boot_jwt_boilerplate.dto.task;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MoveTaskRequest {
    private Long targetSectionId;
    private Long targetIndex;
}
