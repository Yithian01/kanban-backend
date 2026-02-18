package com.example.spring_boot_jwt_boilerplate.domain.kanban;

import com.example.spring_boot_jwt_boilerplate.domain.common.BaseTimeEntity;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kanban_boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KanbanBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public KanbanBoard(String title, Member member) {
        this.title = title;
        this.member = member;
    }

    public void renameTitle(String title) {
        this.title = title;
    }
}