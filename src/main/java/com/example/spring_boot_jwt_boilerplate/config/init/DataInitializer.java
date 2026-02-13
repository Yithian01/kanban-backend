package com.example.spring_boot_jwt_boilerplate.config.init;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.member.Role;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@test.com";

        if (memberRepository.existsByEmail(adminEmail)) {
            log.info("[DataInitializer] 관리자 계정이 이미 존재합니다.");
            return;
        }

        Member admin = Member.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("admin1234"))
                .nickname("슈퍼관리자")
                .role(Role.ADMIN)
                .build();

        memberRepository.save(admin);
        log.info("[DataInitializer] 초기 관리자 계정 생성이 완료되었습니다: {}", adminEmail);
    }
}