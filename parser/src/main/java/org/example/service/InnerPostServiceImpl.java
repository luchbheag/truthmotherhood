package org.example.service;

import org.example.dao.InnerPostDao;
import org.example.entity.Image;
import org.example.entity.InnerPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InnerPostServiceImpl implements InnerPostService {

    private final InnerPostDao innerPostDao;
    private final ImageService imageService;

    @Autowired
    public InnerPostServiceImpl(InnerPostDao innerPostDao,
                                ImageService imageService) {
        this.innerPostDao = innerPostDao;
        this.imageService = imageService;
    }

    @Override
    public void saveAll(List<InnerPost> innerPosts) {
        innerPostDao.saveAll(innerPosts);
        for (InnerPost innerPost : innerPosts) {
            imageService.saveAll(innerPost.getImages());
        }
    }

    @Override
    public List<InnerPost> findAllByMultipleWallPostIds(List<Long> wallPostIds) {
        List<InnerPost> innerPosts = innerPostDao.findAllByMultipleWallPostIds(wallPostIds);
        List<Long> innerPostIds = innerPosts.stream().map(InnerPost::getInnerPostId).toList();
        Map<Long, List<Image>> wallPostImagesMap = getImagesForInnerPosts(innerPostIds);

        for (InnerPost innerPost : innerPosts) {
            innerPost.setImages(wallPostImagesMap.getOrDefault(innerPost.getInnerPostId(), new ArrayList<>()));
        }
        return innerPosts;
    }

    private Map<Long, List<Image>> getImagesForInnerPosts(List<Long> innerPostIds) {

        return imageService.findAllByMultipleInnerPostIds(innerPostIds)
                .stream()
                .collect(Collectors.groupingBy(Image::getInnerPostId));
    }
}
