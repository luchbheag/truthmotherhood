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
    private Long commentId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDateTime date;
    private Long wallPostId;
    private Long threadStarterId;
    private Long commentToAnswerId;
    private Long userToAnswerId;
    private Boolean hasImages;
    private Boolean hasDocuments;
    private Boolean hasLinks;
    private Boolean hasVideo;
    private Boolean hasOnlySticker;
    private List<Image> images;

    public Comment(Long commentId,
                   Long userId,
                   String text,
                   String userName,
                   LocalDateTime date,
                   Long wallPostId,
                   Long threadStarterId,
                   Long commentToAnswerId,
                   Long userToAnswerId,
                   Boolean hasImages,
                   Boolean hasDocuments,
                   Boolean hasLinks,
                   Boolean hasVideo,
                   Boolean hasOnlySticker) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.date = date;
        this.wallPostId = wallPostId;
        this.threadStarterId = threadStarterId;
        this.commentToAnswerId = commentToAnswerId;
        this.userToAnswerId = userToAnswerId;
        this.hasImages = hasImages;
        this.hasDocuments = hasDocuments;
        this.hasLinks = hasLinks;
        this.hasVideo = hasVideo;
        this.hasOnlySticker = hasOnlySticker;
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
