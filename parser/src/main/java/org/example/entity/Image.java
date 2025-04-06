package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    private Long id;
    private Integer height;
    private Integer width;
    private String url;
    private Long wallPostId;
    private Long innerPostId;
    private Long commentId;

//    @Override
//    public String toString() {
//        return "Image [id=" + id
////                + ", post_id=" + postId
//                + ", height=" + height
//                + ", width=" + width
//                + ", url=" + url + "]";
//    }
}
