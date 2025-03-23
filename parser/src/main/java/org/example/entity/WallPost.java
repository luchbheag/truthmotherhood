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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "wall_posts")
@Getter
@Setter
@Builder
@ToString()
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
    @OneToOne//(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_id")
    WallPost parentWallPost;
    @OneToOne(mappedBy = "parentWallPost", cascade = CascadeType.ALL)
    WallPost childWallPost;
    @OneToMany(mappedBy = "wallPost", cascade = CascadeType.PERSIST, orphanRemoval = true)
//    @BatchSize(size = 10)
    Set<Image> images = new LinkedHashSet<>();
//    @OneToMany(mappedBy = "wallPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    @Fetch(FetchMode.SUBSELECT)
//    @BatchSize(size = 10)
//    List<SimpleComment> comments = new ArrayList<>();
//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "inner_post_id",
//            referencedColumnName = "inner_post_id",
//            nullable = true)
//    InnerPost innerPost;

    public boolean isEmpty() {
        return wallPostId == null;
    }

//    public void setImages(List<Image> newImages) {
//        if (this.images != null) {
//            this.images.clear();
//        } else {
//            this.images = new ArrayList<>();
//        }
//        if (newImages != null) {
//            this.images.addAll(newImages);
//        }
//    }
//
//    public void setComments(List<SimpleComment> newSimpleComments) {
//        if (this.comments != null) {
//            this.comments.clear();
//        } else {
//            this.comments = new ArrayList<>();
//        }
//        if (newSimpleComments != null) {
//            this.comments.addAll(newSimpleComments);
//        }
//    }
}
