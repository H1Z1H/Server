package com.brick.brick_server.domain.admin.domain;

    import com.brick.brick_server.domain.user.domain.vo.Role;
    import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}


