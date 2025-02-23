package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class Post {
    Long id;
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
