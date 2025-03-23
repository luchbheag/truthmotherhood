package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Data
//@Entity
//@Table(name="inner_posts")
@Getter
@Setter
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class InnerPost {
//    @Id
//    @Column(name = "inner_post_id")
//    private Long innerPostId;
//    @Column
//    private String text;
//    @Column
//    private LocalDateTime date;
//    @OneToOne(mappedBy = "innerPost", cascade = CascadeType.ALL, orphanRemoval = true)
//    private WallPost wallPost;
//    @OneToMany(mappedBy = "innerPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
////    @Fetch(FetchMode.SUBSELECT)
//    @BatchSize(size = 10)
//    List<Image> images = new ArrayList<>();
//
//    @Override
//    public String toString() {
//        return "InnerPost{" + innerPostId
//                + ", date = " + date
//                + ", text = " + text + "]";
//    }
//
//    public boolean isNull() {
//        return innerPostId == null;
//    }
//
//    public void setImages(List<Image> newImages) {
//        if (this.images != null) {
//            this.images.clear();
//        } else {
//            this.images = new ArrayList<>();
//        }
//        if (newImages != null) {
//            this.images.addAll(newImages); // Добавляем новые элементы
//        }
//    }
}
