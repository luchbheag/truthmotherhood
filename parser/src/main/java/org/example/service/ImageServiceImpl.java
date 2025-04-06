package org.example.service;

import org.example.dao.ImageDao;
import org.example.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    public void saveAll(List<Image> images) {
        for (Image image : images) {
            imageDao.save(image);
        }
    }

    @Override
    public List<Image> findAllByWallPostId(Long wallPostId) {
        return imageDao.findAllByPostId(wallPostId);
    }

    @Override
    public List<Image> findAllByMultipleWallPostIds(List<Long> wallPostIds) {
        return imageDao.findAllByMultipleWallPostIds(wallPostIds);
    }

    @Override
    public List<Image> findAllByMultipleInnerPostIds(List<Long> innerPostIds) {
        return imageDao.findAllByMultipleInnerPostIds(innerPostIds);
    }

    @Override
    public List<Image> findAllByMultipleCommentIds(List<Long> commentIds) {
        return imageDao.findAllByMultipleCommentIds(commentIds);
    }
}
