package org.example.repository;

import org.example.entity.WallPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WallPostRepository extends JpaRepository<WallPost, Long> {

    @Query("SELECT wp FROM WallPost wp LEFT JOIN FETCH wp.innerPost LEFT JOIN FETCH wp.innerPost.images")
    List<WallPost> findAllWithInnerPost();

    @Query("SELECT wp FROM WallPost wp LEFT JOIN FETCH wp.comments")
    List<WallPost> findAllWithComments();
}

