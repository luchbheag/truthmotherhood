package org.example;

import org.example.entity.Comment;
import org.example.entity.WallPost;
import org.example.service.WallPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        Parser parser = new Parser();
//        List<WallPost> posts = new ArrayList<>();
        while (!parser.isEmpty()) {
            WallPost post = parser.parseWallPost();
            if (!(post == null || post.isEmpty())) {
//                posts.add(post);
//                System.out.println("WallPost id from parser: " + post.getWallPostId());
//                if (!post.getComments().isEmpty()) {
//                    System.out.println("Comments: " + post.getComments().size());
//                    post.getComments().forEach(comment -> {
//                        String prefix = comment.getThreadStarterId() == null ? "" : "\t\t";
//                        System.out.println(prefix + comment.getCommentId());
//                    });
//                }
                wallPostService.save(post);
            }
        }
//        System.out.println("POSTS FROM PARSER: " + posts.size());
//
//        List<WallPost> postsFromDb = wallPostService.findAll();

//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("Posts from DB: " + postsFromDb.size());
//        for (WallPost post : postsFromDb) {
//            System.out.println("WallPost id: " + post.getWallPostId());
//            if (!post.getComments().isEmpty()) {
//                System.out.println("Comments size: " + post.getComments().size());
//                post.getComments().forEach(comment -> {
//                    String prefix = comment.getThreadStarterId() == null || comment.getThreadStarterId() == 0 ? "" : "\t\t";
//                    System.out.println(prefix + comment.getCommentId());
//                });
//            }
//        }

        System.out.println("END EXECUTION");
        System.exit(0);
    }
}
