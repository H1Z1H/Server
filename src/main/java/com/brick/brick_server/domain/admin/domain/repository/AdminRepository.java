package com.brick.brick_server.domain.admin.domain.repository;



import com.brick.brick_server.domain.admin.domain.Admin;
import com.brick.brick_server.domain.auth.domain.Token;
import com.brick.brick_server.domain.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
