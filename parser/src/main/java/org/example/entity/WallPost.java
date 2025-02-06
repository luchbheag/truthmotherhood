package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class WallPost extends Post {
    List<SimpleComment> comments;
    InnerPost innerPost;

    @Override
    public String toString() {
        return "WallPost [id=" + id
                + ", text=" + super.text + "]";
    }

    public void addComment(SimpleComment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}
