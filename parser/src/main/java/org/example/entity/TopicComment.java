package org.example.entity;

import lombok.*;

import java.time.LocalDateTime;

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
}
