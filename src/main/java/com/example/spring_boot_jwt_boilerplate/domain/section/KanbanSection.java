package com.example.spring_boot_jwt_boilerplate.domain.section;

import com.example.spring_boot_jwt_boilerplate.domain.common.BaseTimeEntity;
import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kanban_sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KanbanSection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kanban_id", nullable = false)
    private KanbanBoard kanbanBoard;

    @Builder
    public KanbanSection(String name, Double position, KanbanBoard kanbanBoard) {
        this.name = name;
        this.position = position;
        this.kanbanBoard = kanbanBoard;
    }

    public void updateName(String name) {
        this.name = name;
    }
}