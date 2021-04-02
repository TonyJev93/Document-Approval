package com.tonyjev.documentapproval.domain.user.application.impl;

import com.tonyjev.documentapproval.domain.user.application.UserCreateService;
import com.tonyjev.documentapproval.domain.user.application.UserFindService;
import com.tonyjev.documentapproval.domain.user.application.exception.AlreadyExistUserIdException;
import com.tonyjev.documentapproval.domain.user.dao.UserRepository;
import com.tonyjev.documentapproval.domain.user.domain.User;
import com.tonyjev.documentapproval.global.security.utils.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreateServiceImpl implements UserCreateService {

    private final UserRepository userRepository;
    private final UserFindService userFindService;

    @Override
    public User join(User user) {
        log.info("사용자 등록");

        try {
            userFindService.findUser(user.getUserId()).orElseThrow(EntityNotFoundException::new);
            log.info("이미 사용자 존재");
            throw new AlreadyExistUserIdException();
        } catch (EntityNotFoundException ex) {
            // Encoding Password
            String encodedPassword = PasswordEncoderUtil.encode(user.getPassword());

            // Set Encoded Password to User
            user.setEncodedPassword(encodedPassword);

            log.info("사용자 최초 등록");
            userRepository.save(user);
            return user;
        }
    }
}
