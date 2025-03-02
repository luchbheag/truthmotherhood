package org.example.repository;

import org.example.entity.WallPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WallPostRepository extends JpaRepository<WallPost, Long> {
//    @EntityGraph(attributePaths = "images")
//    @Query("SELECT DISTINCT w FROM WallPost w LEFT JOIN FETCH w.images")
//    List<WallPost> findAllWithImages();
//    @Query("SELECT DISTINCT wp FROM WallPost wp " +
//            "LEFT JOIN FETCH wp.innerPost ip " +
//            "LEFT JOIN FETCH ip.images " +
//            "LEFT JOIN FETCH wp.images")
    @EntityGraph(attributePaths = {"innerPost", "innerPost.images"})
    @Query("SELECT wp FROM WallPost wp")
    List<WallPost> findAllWithDetails();
}
