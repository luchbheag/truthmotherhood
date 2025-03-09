package org.example;

import jakarta.transaction.Transactional;
import org.example.entity.Image;
import org.example.entity.SimpleComment;
import org.example.entity.WallPost;
import org.example.repository.WallPostRepository;
import org.hibernate.Hibernate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            System.out.println("POSTS FROM PARSER: " + posts.size());
//            saveAllWithRelations(posts, repository);
//            repository.flush();
//            repository.findAllWithImages().forEach(System.out::println);
            List<WallPost> postsFromDb = findAllWithDetails(repository);
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
                System.out.println(post.getComments().size());
                if (post.getComments() != null) {
                    post.getComments().forEach(System.out::println);
                }
            });
            System.out.println("HERE IS FINE");
        };
    }

    private void saveAllWithRelations(List<WallPost> wallPosts, WallPostRepository repository) {
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

    @Transactional
    void saveWallPostWithRelations(WallPost post, WallPostRepository repository) {
        for (Image image : post.getImages()) {
            image.setWallPost(post);
        }
        if (post.getInnerPost() != null) {
            post.getInnerPost().setWallPost(post);

            for (Image image : post.getInnerPost().getImages()) {
                image.setInnerPost(post.getInnerPost());
            }
        }
        for (SimpleComment comment : post.getComments()) {
            comment.setWallPost(post);
            if (!comment.isEmpty()) {
                for (Image image : comment.getImages()) {
                    image.setSimpleComment(comment);
                }
            }
        }
        repository.save(post);
    }

    @Transactional
    List<WallPost> findAllWithDetails(WallPostRepository repository) {
//        List<WallPost> posts = repository.findAllWithInnerPostAndComments();
//
//        for (WallPost post : posts) {
//            Hibernate.initialize(post.getImages());
//
//            if (post.getInnerPost() != null) {
//                Hibernate.initialize(post.getInnerPost().getImages());
//            }
//            for (SimpleComment comment : post.getComments()) {
//                Hibernate.initialize(comment.getImages());
//                Hibernate.initialize(comment.getThreadComments());
//
//                for (ThreadComment threadComment : comment.getThreadComments()) {
//                    Hibernate.initialize(threadComment.getImages());
//                }
//            }
//        }
        List<WallPost> postsWithInnerPost = repository.findAllWithInnerPost();
        List<WallPost> postsWithComments = repository.findAllWithComments();

        // Создаем мапу, чтобы быстро находить посты по id
        Map<Long, WallPost> postMap = postsWithInnerPost.stream()
                .collect(Collectors.toMap(WallPost::getWallPostId, post -> post));

        // Объединяем комментарии в существующие объекты WallPost
        for (WallPost postWithComments : postsWithComments) {
            WallPost existingPost = postMap.get(postWithComments.getWallPostId());
            if (existingPost != null) {
                existingPost.setComments(postWithComments.getComments()); // Добавляем комментарии
            }
        }

        return new ArrayList<>(postMap.values());
    }
}
