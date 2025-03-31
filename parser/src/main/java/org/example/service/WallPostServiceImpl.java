package org.example.service;

import org.example.dao.WallPostDao;
import org.example.entity.Image;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class WallPostServiceImpl implements WallPostService {

    private final WallPostDao wallPostDao;
    private final ImageService imageService;

    @Autowired
    public WallPostServiceImpl(WallPostDao wallPostDao,
                               ImageService imageService) {
        this.wallPostDao = wallPostDao;
        this.imageService = imageService;
    }

    @Override
    public void save(WallPost wallPost) {
        wallPostDao.save(wallPost);
        if (!wallPost.getImages().isEmpty()) {
            System.out.println("In wallpost service is not empty");
            imageService.saveAllByWallPostId(wallPost.getWallPostId(), wallPost.getImages());
        } else {
            System.out.println("Empty one");
        }
    }

    @Override
    public List<WallPost> findAll() {
        List<WallPost> posts = wallPostDao.findAll();
        if (posts.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> wallPostIds = posts.stream().map(WallPost::getWallPostId).toList();

        Map<Long, List<Image>> wallPostImagesMap = getImagesForPosts(wallPostIds);

        for (WallPost post : posts) {
            post.setImages(wallPostImagesMap.getOrDefault(post.getWallPostId(), new ArrayList<>()));
        }
        return posts;
    }

    private Map<Long, List<Image>> getImagesForPosts(List<Long> wallPostIds) {

        return imageService.findAllByMultipleWallPostIds(wallPostIds)
                .stream()
                .collect(Collectors.groupingBy(Image::getWallPostId));
    }
}
