package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "wall_posts")
@Getter
@Setter
@Builder
@ToString(exclude = {"innerPost"})
@NoArgsConstructor
@AllArgsConstructor
public class WallPost {
    @Id
    @Column(name = "wall_post_id")
    Long wallPostId;
    @Column
    String text;
    @Column
    LocalDateTime date;
    @OneToMany(mappedBy = "wallPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    List<Image> images = new ArrayList<>();;
    @OneToMany(mappedBy = "wallPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    List<SimpleComment> comments = new ArrayList<>();;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inner_post_id",
            referencedColumnName = "inner_post_id",
            nullable = true)
    InnerPost innerPost;

//    public void addComment(SimpleComment comment) {
//        if (comments == null) {
//            comments = new ArrayList<>();
//        }
//        comments.add(comment);
//    }

    public boolean isEmpty() {
        return wallPostId == null;
    }
}
