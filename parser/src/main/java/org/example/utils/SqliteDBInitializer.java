package org.example.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqliteDBInitializer {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SqliteDBInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void createTables() {
        String sqlWallPosts = """
                CREATE TABLE IF NOT EXISTS wall_posts (
                    wall_post_id INTEGER PRIMARY KEY,
                    text TEXT NULL,
                    date TEXT NULL
                )
                """;
        /*
        id,
        text,
        data
        List<Image>
        List<InnerPost>
        List<Comment>
         */
        String sqlImages = """
                CREATE TABLE IF NOT EXISTS images (
                    image_id INTEGER PRIMARY KEY,
                    height INTEGER NULL,
                    width INTEGER NULL,
                    url TEXT NULL,
                    wall_post_id INTEGER NULL,
                    inner_post_id INTEGER NULL,
                    comment_id INTEGER NULL
                )
                """;
        String sqlInnerPosts = """
                CREATE TABLE IF NOT EXISTS inner_posts (
                    inner_post_id INTEGER PRIMARY KEY,
                    text TEXT NULL,
                    date TEXT NULL,
                    wall_post_id INTEGER NOT NULL
                )
                """;
        String sqlComments = """
                CREATE TABLE IF NOT EXISTS comments (
                    comment_id INTEGER PRIMARY KEY,
                    user_id INTEGER NULL,
                    text TEXT NULL,
                    date TEXT NULL,
                    wall_post_id INTEGER NULL,
                    thread_starter_id INTEGER NULL,
                    comment_to_answer_id INTEGER NULL,
                    user_to_answer_id INTEGER NULL
                )
                """;
        jdbcTemplate.execute(sqlWallPosts);
        System.out.println("INFO: wall_posts table was created");
        jdbcTemplate.execute(sqlImages);
        System.out.println("INFO: images table was created");
        jdbcTemplate.execute(sqlInnerPosts);
        System.out.println("INFO: inner_posts table was created");
        jdbcTemplate.execute(sqlComments);
        System.out.println("INFO: comments table was created");
    }
}
