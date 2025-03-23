package org.example.service;

import org.example.entity.WallPost;

import java.util.List;

public interface WallPostService {
    void save(WallPost wallPost);

    List<WallPost> findAll();
}
