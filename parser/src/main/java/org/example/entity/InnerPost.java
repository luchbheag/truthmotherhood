package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class InnerPost {
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
