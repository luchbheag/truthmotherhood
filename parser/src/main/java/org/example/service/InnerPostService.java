package org.example.service;

import org.example.entity.InnerPost;

import java.util.List;

public interface InnerPostService {
    public void saveAll(List<InnerPost> innerPosts);
    public List<InnerPost> findAllByMultipleWallPostIds(List<Long> wallPostIds);
}
