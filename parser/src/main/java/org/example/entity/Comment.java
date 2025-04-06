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
    String text;
    LocalDateTime date;
    Long wallPostId;
    Long threadStarterId;
    Long commentToAnswerId;
    List<Image> images;

    public Comment(Long commentId, Long userId, String text, Long wallPostId, Long threadStarterId, Long commentToAnswerId) {
        this.commentId = commentId;
        this.userId = userId;
        this.text = text;
        this.wallPostId = wallPostId;
        this.threadStarterId = threadStarterId;
        this.commentToAnswerId = commentToAnswerId;
    }

    public boolean isEmpty() {
        return commentId == null;
    }

    public void printIdAndAmountOfImages() {
        //System.out.println("\tComment id: " + commentId + ", is thread: " + (threadStarterId != null) + ", images: " + images.size());
        if (!images.isEmpty()) {
            System.out.println(images);
        }
    }
}
