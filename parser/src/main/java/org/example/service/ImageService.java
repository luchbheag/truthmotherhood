package org.example.service;

import org.example.entity.Image;

import java.util.List;

public interface ImageService {
    public void saveAll(List<Image> images);

    public List<Image> findAllByWallPostId(Long wallPostId);

    public List<Image> findAllByMultipleWallPostIds(List<Long> wallPostIds);

    public List<Image> findAllByMultipleInnerPostIds(List<Long> InnerPostIds);

    public List<Image> findAllByMultipleCommentIds(List<Long> InnerPostIds);

    public List<Image> findAllByMultipleTopicCommentIds(List<Long> InnerPostIds);
}
