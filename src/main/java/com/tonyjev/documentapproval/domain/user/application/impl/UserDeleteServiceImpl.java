package com.tonyjev.documentapproval.domain.user.application.impl;

import com.tonyjev.documentapproval.domain.user.application.UserDeleteService;
import com.tonyjev.documentapproval.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDeleteServiceImpl implements UserDeleteService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void delete(String userId) {
        userRepository.deleteByUserId(userId);
    }
}
