package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "simple_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleComment {
    @Id
    @Column(name = "simple_comment_id")
    Long simpleCommentId;
    Long userId;
    LocalDateTime date;
//    Long postId; // one-to-one
    String text;
    @OneToMany(mappedBy = "simpleComment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    List<Image> images = new ArrayList<>();;
    @ManyToOne
    @JoinColumn(name = "wall_post_id", nullable = false)
    WallPost wallPost;
    // Think about: we can actually parse and store
    // to what user comment was sent in thread
//    List<ThreadComment> threadComments;

    @Override
    public String toString() {
        return "Comment [id = " + simpleCommentId
                + ", userId = " + userId
                + ", date = " + date
                + ", text = " + text
                + ", images = " + images;
    }

    public boolean isEmpty() {
        return simpleCommentId == null;
    }

    public void setImages(List<Image> newImages) {
        if (this.images != null) {
            this.images.clear();
        } else {
            this.images = new ArrayList<>();
        }
        if (newImages != null) {
            this.images.addAll(newImages); // Добавляем новые элементы
        }
    }
}
