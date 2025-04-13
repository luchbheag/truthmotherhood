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
public class InnerPost {
    private Long innerPostId;
    private String text;
    private LocalDateTime date;
    private Long wallPostId;
    List<Image> images = new ArrayList<>();

    // TODO: date; delete annotations about constructors
    public InnerPost(Long innerPostId, String text, Long wallPostId, LocalDateTime date) {
        this.innerPostId = innerPostId;
        this.text = text;
        this.wallPostId = wallPostId;
        this.date = date;
    }
}
