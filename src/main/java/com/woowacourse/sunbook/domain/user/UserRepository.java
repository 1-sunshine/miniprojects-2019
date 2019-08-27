package com.woowacourse.sunbook.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmailAndUserPassword(UserEmail email, UserPassword password);

    boolean existsByUserEmail(UserEmail userEmail);

    @Query("select u from User u where u.userName.name like %?1%")
    List<User> findAllByUserNameLike(String userName);
}