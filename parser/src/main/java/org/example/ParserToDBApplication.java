package org.example;

import jakarta.transaction.Transactional;
import org.example.entity.Image;
import org.example.entity.WallPost;
import org.example.repository.WallPostRepository;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ParserToDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParserToDBApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner init(WallPostRepository repository) {
        return args -> {

            Parser parser = new Parser();
            List<WallPost> posts = new ArrayList<>();
            while (!parser.isEmpty()) {
                WallPost post = parser.parseWallPost();
                if (!(post == null || post.isEmpty())) {
                    posts.add(post);
                    saveWallPostWithRelations(post, repository);
                }
            }
            System.out.println(posts.size());

            System.out.println("Post count: " + posts.size());
//            saveAllWithRelations(posts, repository);
//            repository.flush();
//            repository.findAllWithImages().forEach(System.out::println);
            List<WallPost> postsFromDb = repository.findAllWithDetails();
//            for (WallPost post : postsFromDb) {
//                if (post.getInnerPost() != null) {
//                    Hibernate.initialize(post.getInnerPost().getImages());
//                }
//            }
            System.out.println("Posts from DB: " + postsFromDb.size());
            postsFromDb.forEach(post -> {
                System.out.println("POST");
                System.out.println(post.getWallPostId());
                if (post.getInnerPost() != null) {
                    System.out.println(post.getInnerPost().getImages());
                }
            });
            System.out.println("HERE IS FINE");
        };
    }

    public void saveAllWithRelations(List<WallPost> wallPosts, WallPostRepository repository) {
        for (WallPost post : wallPosts) {
            if (post.getInnerPost() != null) {
                post.getInnerPost().setWallPost(post); // Устанавливаем связь WallPost → InnerPost

                for (Image image : post.getInnerPost().getImages()) {
                    image.setInnerPost(post.getInnerPost()); // Устанавливаем связь Image → InnerPost
                }
            }
        }
        repository.saveAll(wallPosts); // Теперь Hibernate сохранит всё правильно!
    }

    public void saveWallPostWithRelations(WallPost post, WallPostRepository repository) {
        for (Image image : post.getImages()) {
            image.setWallPost(post);
        }
        if (post.getInnerPost() != null) {
            post.getInnerPost().setWallPost(post);

            for (Image image : post.getInnerPost().getImages()) {
                image.setInnerPost(post.getInnerPost());
            }
        }
        repository.save(post);
    }
}
