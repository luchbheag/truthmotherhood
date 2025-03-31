package org.example.service;

import org.example.entity.Comment;

import java.util.List;

public interface CommentService {
    public void saveAll(List<Comment> comments);
    public List<Comment> findAllByPostId(Long wallPostId);
}
