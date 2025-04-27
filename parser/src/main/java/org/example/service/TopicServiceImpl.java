package org.example.service;

import org.example.dao.TopicDao;
import org.example.entity.Image;
import org.example.entity.InnerPost;
import org.example.entity.Topic;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicDao topicDao;
    private final TopicCommentService topicCommentService;

    @Autowired
    public TopicServiceImpl(TopicDao topicDao, TopicCommentService topicCommentService) {
        this.topicDao = topicDao;
        this.topicCommentService = topicCommentService;
    }

    @Override
    public void saveAll(List<Topic> topics) {
        for (Topic topic : topics) {
            topicDao.save(topic);
            topicCommentService.saveAll(topic.getComments());
        }
    }

    @Override
    public List<Topic> findAll() {
        List<Topic> topics = topicDao.findAll();
        if (topics.isEmpty()) {
            return Collections.emptyList();
        }
        return topics;
    }

    @Override
    public Topic findByIdWithComments(Long topicId) {
        Topic topic = topicDao.findById(topicId);
        topic.setComments(topicCommentService.findAllByTopicId(topic.getTopicId()));
        return topic;
    }
}
