package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Comment;
import org.example.entity.Topic;
import org.example.entity.WallPost;
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

    @Autowired
    public ParserCommandLineRunner(WallPostService wallPostService) {
        this.wallPostService = wallPostService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTS EXECUTION");
        ParserTopics parserTopics = new ParserTopics();
        List<Topic> topics = parserTopics.parseTopics();
        System.out.println("Size:" + topics.size());
        int count = 0;
        for (Topic topic : topics) {
            System.out.println(topic);
            if (topic.getComments().size() != 0) {
                count++;
            }
        }
        System.out.println("Topics with comments:" + count);
//        Parser parser = new Parser();
//        while (!parser.isEmpty()) {
//            WallPost post = parser.parseWallPost();
//            if (!(post == null || post.isEmpty())) {
//                wallPostService.save(post);
//            }
//        }
//        List<WallPost> postsFromDb = wallPostService.findAll();
//        JsonWriter.writeToJsonFile(postsFromDb, "wall_posts.json");

        System.out.println("END EXECUTION");
        System.exit(0);
    }
}
