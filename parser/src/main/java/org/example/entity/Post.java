package org.example.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class Post {
    Integer id;
    String text;
//    private Date date;
    List<Image> images;
}
