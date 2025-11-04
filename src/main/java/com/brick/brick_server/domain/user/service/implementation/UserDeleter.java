package com.brick.brick_server.domain.user.service.implementation;


import com.brick.brick_server.domain.user.domain.Users;
import com.brick.brick_server.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeleter {

    private final UserRepository usersRepository;

    @Transactional
    public void delete(Users user) {
        usersRepository.delete(user);
    }
}