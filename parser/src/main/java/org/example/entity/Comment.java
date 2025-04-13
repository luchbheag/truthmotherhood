package org.example.entity;

import lombok.*;

import java.time.LocalDateTime;
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
    Long userToAnswerId;
    List<Image> images;

    public Comment(Long commentId,
                   Long userId,
                   String text,
                   LocalDateTime date,
                   Long wallPostId,
                   Long threadStarterId,
                   Long commentToAnswerId,
                   Long userToAnswerId) {
        this.commentId = commentId;
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.wallPostId = wallPostId;
        this.threadStarterId = threadStarterId;
        this.commentToAnswerId = commentToAnswerId;
        this.userToAnswerId = userToAnswerId;
    }

    public boolean isEmpty() {
        return commentId == null;
    }

    public void printIdAndAmountOfImages() {
        System.out.println("CommentId: " + commentId);
        if (!images.isEmpty()) {
            System.out.println("Images size: " + images.size());
            images.forEach(image -> System.out.println(image.getId()));
        }
    }
}
