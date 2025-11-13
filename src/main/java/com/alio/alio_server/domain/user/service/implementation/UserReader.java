package com.alio.alio_server.domain.user.service.implementation;

import com.alio.alio_server.domain.user.domain.Users;
import com.alio.alio_server.domain.user.domain.repository.UserRepository;
import com.alio.alio_server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;

    public Optional<Users> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Users findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }
}
