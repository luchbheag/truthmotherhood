package org.example.service;

import org.example.entity.Topic;

import java.util.List;

public interface TopicService {
    void saveAll(List<Topic> topics);

    List<Topic> findAll();

    Topic findByIdWithComments(Long topicId);
}
