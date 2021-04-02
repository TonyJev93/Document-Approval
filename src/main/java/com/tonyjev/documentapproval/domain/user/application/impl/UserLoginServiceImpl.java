package com.tonyjev.documentapproval.domain.user.application.impl;

import com.tonyjev.documentapproval.domain.user.application.UserFindService;
import com.tonyjev.documentapproval.domain.user.application.UserLoginService;
import com.tonyjev.documentapproval.domain.user.application.exception.PasswordNotCorrectException;
import com.tonyjev.documentapproval.domain.user.application.exception.UserNotFoundException;
import com.tonyjev.documentapproval.domain.user.dao.UserRepository;
import com.tonyjev.documentapproval.domain.user.domain.User;
import com.tonyjev.documentapproval.global.security.jwt.JwtTokenProvider;
import com.tonyjev.documentapproval.global.security.utils.PasswordEncoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {

    private final UserFindService userFindService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(String userId, String password) {
        log.info("사용자 로그인 요청 : {}", userId);

        final User user = userFindService.findUser(userId).orElseThrow(UserNotFoundException::new);
        if (!PasswordEncoderUtil.isEquals(password, user.getPassword())) {
            throw new PasswordNotCorrectException();
        }

        return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());


    }
}
