package com.tonyjev.documentapproval.domain.user.dao;

import com.tonyjev.documentapproval.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId) throws EntityNotFoundException;

    void deleteByUserId(String userId);

    List<User> findAllByUserIdIn(List<String> userIdList);
}
