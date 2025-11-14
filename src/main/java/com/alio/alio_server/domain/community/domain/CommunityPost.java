package com.alio.alio_server.domain.community.domain;

import com.alio.alio_server.domain.user.domain.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "community_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Users author;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Lob
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CommunityCaseType caseType;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnonymous = false;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(length = 50)
    private String country;

    @ManyToMany
    @JoinTable(
            name = "community_post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<CommunityTag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    public void addTag(CommunityTag tag) {
        tags.add(tag);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}

