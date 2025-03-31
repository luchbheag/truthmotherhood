package org.example.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    Long commentId;
    Long userId;
    LocalDateTime date;
    String text;
    List<Image> images;
    Long wallPostId;
    Long threadStarterId;
    Long commentToAnswerId;

    public boolean isEmpty() {
        return commentId == null;
    }
}
