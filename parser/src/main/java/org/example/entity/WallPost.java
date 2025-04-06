package org.example.entity;

import jakarta.persistence.*;
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

    public WallPost(Long wallPostId, String text) {
        this.wallPostId = wallPostId;
        this.text = text;
    }

    public boolean isEmpty() {
        return wallPostId == null;
    }
}
