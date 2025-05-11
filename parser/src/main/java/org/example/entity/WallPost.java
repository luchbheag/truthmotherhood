package org.example.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WallPost {
    private Long wallPostId;
    private String text;
    private LocalDateTime date;
    private Boolean hasDocuments;
    private Boolean hasPoll;
    private Boolean hasLinks;
    private Boolean hasVideo;
    private List<Image> images = new ArrayList<>();
    private List<InnerPost> innerPosts = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public WallPost(
            Long wallPostId,
            String text,
            LocalDateTime date,
            Boolean hasDocuments,
            Boolean hasPoll,
            Boolean hasLinks,
            Boolean hasVideo
    ) {
        this.wallPostId = wallPostId;
        this.text = text;
        this.date = date;
        this.hasDocuments = hasDocuments;
        this.hasPoll = hasPoll;
        this.hasLinks = hasLinks;
        this.hasVideo = hasVideo;
    }

    public boolean isEmpty() {
        return wallPostId == null;
    }
}
