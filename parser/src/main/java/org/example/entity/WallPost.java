package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column
    Long id;
    @Column
    String text;
    @Column
    LocalDateTime date;
//    @Column
//    List<Image> images;
//    @Column
//    List<SimpleComment> comments;
//    @Column
//    InnerPost innerPost;

//    public void addComment(SimpleComment comment) {
//        if (comments == null) {
//            comments = new ArrayList<>();
//        }
//        comments.add(comment);
//    }
}
