package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class SimpleComment extends Comment {
    // Think about: we can actually parse and store
    // to what user comment was sent in thread
    List<ThreadComment> threadComments;

    @Override
    public String toString() {
        return "Comment [id = " + id
                + ", userId = " + userId
                + ", postId = " + postId
                + ", date = " + date
                + ", text = " + text;
    }

    public boolean isEmpty() {
        return id == null
                && userId == null
                && postId == null
                && text == null;
    }
}
