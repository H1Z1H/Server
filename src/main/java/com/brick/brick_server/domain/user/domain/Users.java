package com.brick.brick_server.domain.user.domain;

import com.brick.brick_server.domain.admin.domain.UserCoin;
import com.brick.brick_server.domain.user.domain.vo.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @Column(nullable = false)
    private String socialAccountUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String gender;

    private Long money;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCloth> userClothes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCoin> userCoins;

    public void updateDetail(String nickname,String gender) {
        this.nickname = nickname;
        this.gender = gender;
    }

    public void setMoney(Long money) {
        this.money = money;
    }
}
