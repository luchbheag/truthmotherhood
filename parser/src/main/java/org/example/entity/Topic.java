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
    private Integer numberOfComments;
    private List<TopicComment> comments;

    public Topic(Long topicId, String title, LocalDateTime date, Integer numberOfComments) {
        this.topicId = topicId;
        this.title = title;
        this.date = date;
        this.numberOfComments = numberOfComments;
    }
}
