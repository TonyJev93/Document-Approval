package com.tonyjev.documentapproval.domain.user.application;

import com.tonyjev.documentapproval.domain.user.domain.User;

public interface UserCreateService {
    User join(User user);
}
