package org.example;

import org.example.entity.WallPost;
import org.example.repository.WallPostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ParserToDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParserToDBApplication.class, args);
    }

    @Bean
    CommandLineRunner init(WallPostRepository repository) {
        return args -> {
            // Добавляем пользователей

            // Выводим всех пользователей
//            List<User> users = userRepository.findAll();
//            users.forEach(System.out::println);
            Parser parser = new Parser();
            List<WallPost> posts = parser.parse();
            posts.forEach(System.out::println);
            repository.saveAll(posts);
            repository.findAll().forEach(System.out::println);
            System.out.println("HERE IS FINE");
        };
    }
}
