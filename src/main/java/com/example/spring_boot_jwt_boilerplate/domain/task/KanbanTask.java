package com.example.spring_boot_jwt_boilerplate.domain.task;

import com.example.spring_boot_jwt_boilerplate.domain.common.BaseTimeEntity;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kanban_tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KanbanTask extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Double position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private KanbanSection kanbanSection;

    @Column(name = "kanban_id", nullable = false)
    private Long kanbanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public KanbanTask(String title, String content, Double position, KanbanSection kanbanSection, Long kanbanId, Member member) {
        this.title = title;
        this.content = content;
        this.position = position;
        this.kanbanSection = kanbanSection;
        this.kanbanId = kanbanId;
        this.member = member;
    }
}