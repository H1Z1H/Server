package com.alio.alio_server.domain.community.domain.repository;

import com.alio.alio_server.domain.community.domain.CommunityCaseType;
import com.alio.alio_server.domain.community.domain.CommunityPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    @EntityGraph(attributePaths = {"tags", "author"})
    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"tags", "author"})
    List<CommunityPost> findByTags_NameIgnoreCaseOrderByCreatedAtDesc(String tagName);

    @EntityGraph(attributePaths = {"tags", "author"})
    List<CommunityPost> findByCaseTypeOrderByCreatedAtDesc(CommunityCaseType caseType);

    @EntityGraph(attributePaths = {"tags", "author"})
    Optional<CommunityPost> findWithTagsById(Long id);
}

