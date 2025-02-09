package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder(toBuilder = true)
public class ThreadComment extends SimpleComment {
    private Integer threadStarterId;
    private Integer userOfReply;
    private Integer commentOfReply;

    @Override
    public String toString() {
        return "ThreadComment [id = " + id
                + ", userId = " + userId
                + ", postId = " + postId
                + ", date = " + date
                + ", text = " + text
                + ", threadStarterId = " + threadStarterId
                + ", userOfReply = " + userOfReply
                + ", commentOfReply = " + commentOfReply + "]";
    }
}
