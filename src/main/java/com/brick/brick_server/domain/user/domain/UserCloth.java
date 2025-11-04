package com.brick.brick_server.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCloth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String type; // clothes 또는 accessories

    private String name;

    private boolean wear;

    public void wear() {
        this.wear = true;
    }

    public void takeOff() {
        this.wear = false;
    }
}
