package org.example.service;

import org.example.dao.WallPostDao;
import org.example.entity.Image;
import org.example.entity.InnerPost;
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
    private final InnerPostService innerPostService;
    private final CommentService commentService;

    @Autowired
    public WallPostServiceImpl(WallPostDao wallPostDao,
                               ImageService imageService,
                               InnerPostService innerPostService,
                               CommentService commentService) {
        this.wallPostDao = wallPostDao;
        this.imageService = imageService;
        this.innerPostService = innerPostService;
        this.commentService = commentService;
    }

    @Override
    public void save(WallPost wallPost) {
        wallPostDao.save(wallPost);
        if (!wallPost.getImages().isEmpty()) {
            imageService.saveAll(wallPost.getImages());
        }
        if (!wallPost.getInnerPosts().isEmpty()) {
            innerPostService.saveAll(wallPost.getInnerPosts());
        }
        if (!wallPost.getComments().isEmpty()) {
            commentService.saveAll(wallPost.getComments());
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
        Map<Long, List<InnerPost>> wallPostInnerPostMap = getInnerPostsForPosts(wallPostIds);

        for (WallPost post : posts) {
            post.setImages(wallPostImagesMap.getOrDefault(post.getWallPostId(), new ArrayList<>()));
            post.setInnerPosts(wallPostInnerPostMap.getOrDefault(post.getWallPostId(), new ArrayList<>()));
            post.setComments(commentService.findAllByPostId(post.getWallPostId()));
        }
        return posts;
    }

    private Map<Long, List<Image>> getImagesForPosts(List<Long> wallPostIds) {

        return imageService.findAllByMultipleWallPostIds(wallPostIds)
                .stream()
                .collect(Collectors.groupingBy(Image::getWallPostId));
    }

    private Map<Long, List<InnerPost>> getInnerPostsForPosts(List<Long> wallPostIds) {
        return innerPostService.findAllByMultipleWallPostIds(wallPostIds)
                .stream()
                .collect(Collectors.groupingBy(InnerPost::getWallPostId));
    }
}
