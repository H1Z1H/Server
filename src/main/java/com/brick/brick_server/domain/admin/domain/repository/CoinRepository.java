package com.brick.brick_server.domain.admin.domain.repository;

import com.brick.brick_server.domain.admin.domain.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin, Long> {

    List<Coin> findByNameOrderByDateAsc(String name);
    List<Coin> findByNameOrderByDateDesc(String name);

    Optional<Coin> findTopByNameOrderByDateDesc(String name);

    List<Coin> findTop2ByNameOrderByDateDesc(String name);

    @Query("""
    SELECT c.price FROM Coin c
    WHERE c.name = :coinName
    AND c.date < (
        SELECT MAX(c2.date) FROM Coin c2 WHERE c2.name = :coinName
    )
    ORDER BY c.date DESC
    LIMIT 1
""")
    Optional<String> findPreviousPriceByCoinName(@Param("coinName") String coinName);

    @Query("""
    SELECT c FROM Coin c
    WHERE c.date = (
        SELECT MAX(c2.date) FROM Coin c2 WHERE c2.name = c.name
    )
    OR c.date = (
        SELECT MAX(c3.date) FROM Coin c3
        WHERE c3.name = c.name AND c3.date < (
            SELECT MAX(c4.date) FROM Coin c4 WHERE c4.name = c.name
        )
    )
""")
    List<Coin> findLatestAndPreviousForAllCoins();
}
