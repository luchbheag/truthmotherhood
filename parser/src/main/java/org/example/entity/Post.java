package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class Post {
    Integer id;
    String text;
    LocalDateTime date;
    List<Image> images;

    @Override
    public String toString() {
        return "WallPost [id=" + id
                + ", date = " + date
                + ", text = " + text + "]";
    }
}
