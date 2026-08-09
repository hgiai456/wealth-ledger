package com.giaidev.identity.configuration;

import java.util.HashSet;

import com.giaidev.identity.entity.User;
import com.giaidev.identity.enums.Role;
import com.giaidev.identity.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor // Sẽ tạo 1 constructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "app.bootstrap-admin",
            name = "enabled",
            havingValue = "true") // Dieu kien co khoi tao Bean khong vi duoi h2 khong giong mysql dang bi
    // loi findByUsername
    // Neu Start test thi sẽ chạy vào h2 bỏ qua Bean applicationRunner Otherwise Start Application thì chạy vào mysql sẽ
    // cho phép Bean Init applicationRunner

    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Init application.....");
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name()); // Có nghĩa là thêm Role.ADMIN vào Set(1 list chứa những item unique)
                User user = User.builder()
                        .username("admin")
                        .email("admin@wealth-ledger.local")
                        .password(passwordEncoder.encode("admin"))
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}