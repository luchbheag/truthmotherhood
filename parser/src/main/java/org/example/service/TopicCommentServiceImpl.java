package org.example.service;

import org.example.dao.TopicCommentDao;
import org.example.entity.Comment;
import org.example.entity.Image;
import org.example.entity.Topic;
import org.example.entity.TopicComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopicCommentServiceImpl implements TopicCommentService {

    private final TopicCommentDao topicCommentDao;
    private final ImageService imageService;

    @Autowired
    public TopicCommentServiceImpl(TopicCommentDao topicCommentDao, ImageService imageService) {
        this.topicCommentDao = topicCommentDao;
        this.imageService = imageService;
    }

    @Override
    public void saveAll(List<TopicComment> comments) {
        for (TopicComment comment : comments) {
            topicCommentDao.save(comment);
            if (!(comment.getImages() == null || comment.getImages().isEmpty())) {
                imageService.saveAll(comment.getImages());
            }
        }
    }

    @Override
    public List<TopicComment> findAllByTopicId(Long topicId) {
        List<TopicComment> topicComments = topicCommentDao.findAllByTopicId(topicId);
        List<Long> commentIds = topicComments.stream().map(TopicComment::getTopicCommentId).toList();
        Map<Long, List<Image>> commentImagesMap = getImagesForTopicComments(commentIds);

        for (TopicComment topicComment : topicComments) {
            topicComment.setImages(commentImagesMap.getOrDefault(topicComment.getTopicCommentId(), new ArrayList<>()));
        }

        return topicComments;
    }

    private Map<Long, List<Image>> getImagesForTopicComments(List<Long> topicCommentsIds) {
        System.out.println(imageService.findAllByMultipleTopicCommentIds(topicCommentsIds));

        return imageService.findAllByMultipleCommentIds(topicCommentsIds)
                .stream()
                .collect(Collectors.groupingBy(Image::getTopicCommentId));
    }
}
