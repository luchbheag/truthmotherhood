package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CollectionId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="wall_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WallPost {
    @Id
    Long id;
    @Column
    String text;
    @Column
    LocalDateTime date;
//    @Column
//    List<Image> images;
//    @Column
//    List<SimpleComment> comments;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inner_post",
            referencedColumnName = "id",
            nullable = true)
    InnerPost innerPost;

//    public void addComment(SimpleComment comment) {
//        if (comments == null) {
//            comments = new ArrayList<>();
//        }
//        comments.add(comment);
//    }
}
