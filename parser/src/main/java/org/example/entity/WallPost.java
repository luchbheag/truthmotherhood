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
    Long wallPostId;
    String text;
    LocalDateTime date;
    List<Image> images = new ArrayList<>();
    List<InnerPost> innerPosts = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();

    public WallPost(Long wallPostId, String text, LocalDateTime date) {
        this.wallPostId = wallPostId;
        this.text = text;
        this.date = date;
    }

    public boolean isEmpty() {
        return wallPostId == null;
    }
}
