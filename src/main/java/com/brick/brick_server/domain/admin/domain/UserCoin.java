package com.brick.brick_server.domain.admin.domain;

import com.brick.brick_server.domain.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String coinName;

    private String type; //매수,매도(buy,sell)

    private String price;

    private Long amount;

    private String date;

    private Long nowAmount;
}