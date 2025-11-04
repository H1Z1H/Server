package com.brick.brick_server.domain.user.domain.repository;

import com.brick.brick_server.domain.user.domain.UserCloth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserClothRepository extends JpaRepository<UserCloth, Long> {
    // 같은 타입 중 착용중인 옷 모두 조회
    List<UserCloth> findByUserIdAndTypeAndWearTrue(Long userId, String type);

    // 해당 이름의 옷 조회
    Optional<UserCloth> findByUserIdAndTypeAndName(Long userId, String type, String name);
}
