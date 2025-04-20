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
public class Topic {
    private Long topicId;
    private String title;
    private LocalDateTime date;
    private List<TopicComment> comments;
}
