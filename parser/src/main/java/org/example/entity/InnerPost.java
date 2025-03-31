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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnerPost {
    private Long innerPostId;
    private String text;
    private LocalDateTime date;
    List<Image> images = new ArrayList<>();

    @Override
    public String toString() {
        return "InnerPost{" +
                "innerPostId=" + innerPostId +
                '}';
    }
}
