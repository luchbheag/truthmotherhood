package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class Comment {
    Long id;
    Long userId; // many-to-one (many comments - to - one user)
    LocalDateTime date;
    Long postId; // one-to-one
    String text;
    List<Image> images;
}
