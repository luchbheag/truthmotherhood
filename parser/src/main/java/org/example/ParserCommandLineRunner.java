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
        List<WallPost> posts = new ArrayList<>();
        while (!parser.isEmpty()) {
            WallPost post = parser.parseWallPost();
            if (!(post == null || post.isEmpty())) {
                posts.add(post);
//                if (!post.getImages().isEmpty()) {
//                    System.out.println("!!" + post.getImages());
//                }
//                if (!post.getInnerPosts().isEmpty()) {
//                    System.out.println("\uD83D\uDD25 " + post.getInnerPosts());
//                }
                if (!post.getComments().isEmpty()) {
//                    System.out.println("\uD83D\uDC1D");
                    post.getComments().forEach(Comment::printIdAndAmountOfImages);
                }
                wallPostService.save(post);
            }
        }
        System.out.println("POSTS FROM PARSER: " + posts.size());

        List<WallPost> postsFromDb = wallPostService.findAll();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Posts from DB: " + postsFromDb.size());
        for (WallPost post : postsFromDb) {
            System.out.println(post.getWallPostId());
//            if (!post.getImages().isEmpty()) {
//                System.out.println("!!" + post.getImages());
//            }
            if (!post.getInnerPosts().isEmpty()) {
                System.out.println("✅" + post.getInnerPosts().size());
            }
            if (!post.getComments().isEmpty()) {
                post.getComments().forEach(Comment::printIdAndAmountOfImages);
            }
        }

        System.out.println("END EXECUTION");
    }
}
