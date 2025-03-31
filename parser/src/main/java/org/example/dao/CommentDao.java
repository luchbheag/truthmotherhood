package org.example.dao;

import org.example.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDao {

    public void saveAll(List<Comment> comments) {

    }

    public List<Comment> findAllByWallPostId(Long wallPostId) {
        return List.of();
    }
}
