package com.alio.alio_server.domain.news.domain.repository;

import com.alio.alio_server.domain.news.domain.NewsComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {
    
    @Query("SELECT c FROM NewsComment c ORDER BY c.createdAt DESC")
    List<NewsComment> findTopNOrderByCreatedAtDesc(Pageable pageable);
}

