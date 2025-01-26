package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class WallPost {private Integer id;
    private String text;
    // structure for copy_history (it's recursive, but how much?)
    List<SimpleComment> comments;
    List<Image> images;

    @Override
    public String toString() {
        return "WallPost [id=" + id
                + ", text=" + text + "]";
    }

    public void addComment(SimpleComment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}
