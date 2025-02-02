package org.example.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Image {
    private int id;
    //private int album_id;

    // should be int or List<Integer> (Many-to-Many)?
//    private int postId;
    private int height;
    private int width;
    private String url;

    @Override
    public String toString() {
        return "Image [id=" + id
//                + ", post_id=" + postId
                + ", height=" + height
                + ", width=" + width
                + ", url=" + url + "]";
    }
}
