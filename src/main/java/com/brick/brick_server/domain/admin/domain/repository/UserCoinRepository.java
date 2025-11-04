package com.brick.brick_server.domain.admin.domain.repository;

import com.brick.brick_server.domain.admin.domain.UserCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCoinRepository extends JpaRepository<UserCoin, Long> {
    Optional<UserCoin> findTopByUserIdAndCoinNameOrderByIdDesc(Long userId, String coinName);

    @Query("""
    SELECT SUM(uc.nowAmount) 
    FROM UserCoin uc 
    WHERE uc.coinName = :coinName 
      AND uc.id IN (
          SELECT MAX(uc2.id) 
          FROM UserCoin uc2 
          WHERE uc2.coinName = :coinName 
          GROUP BY uc2.user.id
      )
""")
    Long findLatestHoldingsByCoinName(@Param("coinName") String coinName);

    @Query("""
    SELECT uc.nowAmount FROM UserCoin uc
    WHERE uc.coinName = :coinName
    ORDER BY uc.id DESC
""")
    List<Long> findNowAmountByCoinNameOrderByIdDesc(@Param("coinName") String coinName);
}