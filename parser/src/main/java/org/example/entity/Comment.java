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
    Integer id;
    Integer userId; // many-to-one (many comments - to - one user)
    LocalDateTime date;
    Integer postId; // one-to-one
    String text;
    List<Image> images;
}
