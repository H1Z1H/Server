package com.alio.alio_server.domain.community.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    public static CommunityTag of(String name) {
        return CommunityTag.builder()
                .name(name.toLowerCase())
                .build();
    }
}

