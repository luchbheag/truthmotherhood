package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Comment;
import org.example.entity.Topic;
import org.example.entity.WallPost;
import org.example.service.TopicService;
import org.example.service.WallPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParserCommandLineRunner implements CommandLineRunner {

    private final WallPostService wallPostService;
    private final TopicService topicService;

    @Autowired
    public ParserCommandLineRunner(WallPostService wallPostService, TopicService topicService) {
        this.wallPostService = wallPostService;
        this.topicService = topicService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTS EXECUTION");
        ParserTopics parserTopics = new ParserTopics();
        List<Topic> topics = parserTopics.parseTopics();
        System.out.println("Size:" + topics.size());
        topicService.saveAll(topics);
        int count = 0;
        for (Topic topic : topics) {
            count += topic.getComments().size();
            System.out.println(topic.getTopicId());
            System.out.println(topic.getComments().size());
            topic.getComments().forEach(comment -> {
                System.out.println("\t\t" + comment.getTopicCommentId() + " " + comment.getImages().size() + " " + comment.getNumberOfDocuments() + " " + comment.getTopicId());
            });
        }
        System.out.println("Topics with comments:" + topics.size());
        System.out.println("Comments:" + count);

        List<Topic> topicsWithoutComments = topicService.findAll();
        List<Topic> topicsWithComments = new ArrayList<>();
        for (Topic topic : topicsWithoutComments) {
            Topic topicWithComment = topicService.findByIdWithComments(topic.getTopicId());
            topicsWithComments.add(topicWithComment);
        }
        JsonWriter.writeTopicsToJsonFile(topicsWithComments, "topics.json");

        System.out.println("END EXECUTION");
        System.exit(0);
    }
}
