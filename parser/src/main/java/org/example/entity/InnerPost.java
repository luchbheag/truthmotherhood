package org.example.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name="inner_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnerPost {
    @Id
    Long id;
    @Column
    String text;
    @Column
    LocalDateTime date;
//    List<Image> images;

    @Override
    public String toString() {
        return "InnerPost [id=" + id
                + ", date = " + date
                + ", text = " + text + "]";
    }

    public boolean isNull() {
        return id == null;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(id, text, date);
//    }
}
