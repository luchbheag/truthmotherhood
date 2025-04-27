package org.example.service;

import org.example.entity.TopicComment;

import java.util.List;

public interface TopicCommentService {
    void saveAll(List<TopicComment> comments);

    List<TopicComment> findAllByTopicId(Long topicId);
}
