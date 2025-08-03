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
public class TopicComment {
    private Long topicCommentId;
    private String text;
    private LocalDateTime date;
    private Long userId;
    private String userName;
    private Long topicId;
    private Boolean hasImages;
    private Boolean hasDocuments;
    private Long topicCommentToAnswer;
    private Long userToAnswer;
    private List<Image> images;

    public TopicComment(
            Long topicCommentId,
            String text,
            LocalDateTime date,
            Long userId,
            String userName,
            Long topicId,
            boolean hasImages,
            boolean hasDocuments
    ) {
        this.topicCommentId = topicCommentId;
        this.text = text;
        this.date = date;
        this.userId = userId;
        this.userName = userName;
        this.topicId = topicId;
        this.hasImages = hasImages;
        this.hasDocuments = hasDocuments;
    }
}
