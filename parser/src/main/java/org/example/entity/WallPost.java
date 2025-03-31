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

    public boolean isEmpty() {
        return wallPostId == null;
    }
}
