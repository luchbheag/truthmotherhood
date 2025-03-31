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
    public void saveAllByWallPostId(long wallPostId, List<Image> images) {
        System.out.println("TRY TO SAVE ALL IMAGES WITH DAO");
        for (Image image : images) {
            imageDao.saveByWallPostId(wallPostId, image);
        }
    }

    @Override
    public List<Image> findAllByWallPostId(Long wallPostId) {
        return imageDao.findAllByPostId(wallPostId);
    }

    @Override
    public List<Image> findAllByMultipleWallPostIds(List<Long> wallPostIds) {
        return imageDao.findAllByMultiplePostIds(wallPostIds);
    }
}
