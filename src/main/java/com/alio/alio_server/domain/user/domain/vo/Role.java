package com.alio.alio_server.domain.user.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role
{
    USER("ROLE_USER"),

    ADMIN("ROLE_ADMIN");

    private final String role;

    public static Role from(String role) {
        return Arrays.stream(Role.values())
                .filter(r -> r.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
    }
}
