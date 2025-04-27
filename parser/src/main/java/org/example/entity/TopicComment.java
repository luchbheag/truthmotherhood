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
    private Long topicId;
    private Integer numberOfDocuments;
    private List<Image> images;

    public TopicComment(Long topicCommentId, String text, LocalDateTime date, Long userId, Long topicId, Integer numberOfDocuments) {
        this.topicCommentId = topicCommentId;
        this.text = text;
        this.date = date;
        this.userId = userId;
        this.topicId = topicId;
        this.numberOfDocuments = numberOfDocuments;
    }
}
