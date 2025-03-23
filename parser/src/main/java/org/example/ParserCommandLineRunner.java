package org.example;

import org.example.entity.WallPost;
import org.example.service.WallPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParserCommandLineRunner implements CommandLineRunner {

    private WallPostService wallPostService;

    @Autowired
    public ParserCommandLineRunner(WallPostService wallPostService) {
        this.wallPostService = wallPostService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTS EXECUTION");
        Parser parser = new Parser();
        List<WallPost> posts = new ArrayList<>();
        while (!parser.isEmpty()) {
            WallPost post = parser.parseWallPost();
            if (!(post == null || post.isEmpty())) {
                posts.add(post);
                wallPostService.save(post);
            }
        }
        System.out.println("POSTS FROM PARSER: " + posts.size());

        List<WallPost> postsFromDb = wallPostService.findAll();

        System.out.println("Posts from DB: " + postsFromDb.size());
        for (WallPost post : postsFromDb) {
            System.out.println(post.getWallPostId());
        }

        System.out.println("END EXECUTION");
    }
}
