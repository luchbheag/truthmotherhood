package org.example.entity;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Long id;
    private Integer height;
    private Integer width;
    private String url;
    private Long wallPostId;
    private Long innerPostId;
    private Long commentId;
    private Long topicCommentId;
}
