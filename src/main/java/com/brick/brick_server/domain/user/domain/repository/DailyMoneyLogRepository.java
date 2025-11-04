package com.brick.brick_server.domain.user.domain.repository;

import com.brick.brick_server.domain.user.domain.DailyMoneyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyMoneyLogRepository extends JpaRepository<DailyMoneyLog, Long> {
    Optional<DailyMoneyLog> findByUserIdAndDate(Long userId, LocalDate date);
}