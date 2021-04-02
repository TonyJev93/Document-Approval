package com.tonyjev.documentapproval.domain.user.application;

import com.tonyjev.documentapproval.domain.user.domain.User;

import java.util.Optional;

public interface UserFindService {
    Optional<User> findUser(String userId);
}
