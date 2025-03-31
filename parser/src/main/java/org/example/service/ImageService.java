package org.example.service;

import org.example.entity.Image;

import java.util.List;

public interface ImageService {
    public void saveAllByWallPostId(long wallPostId, List<Image> images);

    public List<Image> findAllByWallPostId(Long wallPostId);

    public List<Image> findAllByMultipleWallPostIds(List<Long> wallPostIds);
}
