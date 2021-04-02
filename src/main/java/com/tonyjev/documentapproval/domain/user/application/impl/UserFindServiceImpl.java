package com.tonyjev.documentapproval.domain.user.application.impl;

import com.tonyjev.documentapproval.domain.user.application.UserFindService;
import com.tonyjev.documentapproval.domain.user.dao.UserRepository;
import com.tonyjev.documentapproval.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFindServiceImpl implements UserFindService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUser(String userId) throws EntityNotFoundException{
        log.info("사용자 조회 : {}", userId);
        return userRepository.findByUserId(userId);

    }
}
