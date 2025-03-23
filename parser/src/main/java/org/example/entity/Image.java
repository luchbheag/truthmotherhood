package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name="images")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    private Long id;
    // TODO: should it be date here?
    private Integer height;
    private Integer width;
    private String url;
    @ManyToOne
    @JoinColumn(name = "wall_post_id", nullable = true)
    private WallPost wallPost;
//    @ManyToOne
//    @JoinColumn(name = "inner_post_id", nullable = true)
//    private InnerPost innerPost;
    @ManyToOne
    @JoinColumn(name = "simple_comment_id", nullable = true)
    private SimpleComment simpleComment;

    @Override
    public String toString() {
        return "Image [id=" + id
//                + ", post_id=" + postId
                + ", height=" + height
                + ", width=" + width
                + ", url=" + url
                + ", wallPost=" + (wallPost == null ? "-" : wallPost.getWallPostId()) + "]";
    }
}
