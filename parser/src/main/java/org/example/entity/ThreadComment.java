package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name="thread_comments")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadComment {
    @Id
    @Column(name="thread_comment_id")
    Long threadCommentId;
    @Column(name="user_id")
    Long userId; // many-to-one (many comments - to - one user)
    @Column
    LocalDateTime date;
//    @Column
//    Long postId; // one-to-one
    @Column
    String text;
//    List<Image> images;
//    @Column
//    private Long threadStarterId;
    @Column(name="user_of_reply") // TODO: find comment by iser_id
    private Long userOfReply;
    @Column(name="comment_of_reply")
    private Long commentOfReply; // TODO: make it entity? threadComment
    @ManyToOne
    @JoinColumn(name = "simple_comment_id", nullable = true)
    SimpleComment simpleComment;

    @Override
    public String toString() {
        return "ThreadComment [id = " + threadCommentId
                + ", userId = " + userId
//                + ", postId = " + postId
                + ", date = " + date
                + ", text = " + text
//                + ", threadStarterId = " + threadStarterId
                + ", userOfReply = " + userOfReply
                + ", commentOfReply = " + commentOfReply + "]";
    }
}
