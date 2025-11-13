package com.alio.alio_server.domain.community.domain;

import java.util.Arrays;

public enum CommunityCaseType {
    SUCCESS("성공 사례"),
    RISK("위험/피해 사례");

    private final String description;

    CommunityCaseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static CommunityCaseType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported case type: " + value));
    }
}

