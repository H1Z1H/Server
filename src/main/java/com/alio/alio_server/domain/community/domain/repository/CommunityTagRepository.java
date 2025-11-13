package com.alio.alio_server.domain.community.domain.repository;

import com.alio.alio_server.domain.community.domain.CommunityTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityTagRepository extends JpaRepository<CommunityTag, Long> {
    Optional<CommunityTag> findByNameIgnoreCase(String name);
}

