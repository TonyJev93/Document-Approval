package com.tonyjev.documentapproval.global.security.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordEncoderUtil {

    private static PasswordEncoder passwordEncoder;
    private final PasswordEncoder passwordEncoderBean;

    public static String encode(String password) {
        log.info("Encoding Password : {}", password);

        return passwordEncoder.encode(password);
    }

    public static boolean isEquals(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    @PostConstruct
    public void initialize() {
        passwordEncoder = passwordEncoderBean;
    }

}
