package com.arun.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.arun.user_service.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
