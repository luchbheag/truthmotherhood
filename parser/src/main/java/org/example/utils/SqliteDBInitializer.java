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
                    text TEXT NOT NULL,
                    date TEXT NOT NULL
                )
                """;
        String sqlImages = """
                CREATE TABLE IF NOT EXISTS images (
                    image_id INTEGER PRIMARY KEY,
                    height INTEGER NOT NULL,
                    width INTEGER NOT NULL,
                    url TEXT NOT NULL,
                    wall_post_id INTEGER NULL
                )
                """;
        jdbcTemplate.execute(sqlWallPosts);
        System.out.println("INFO: wall_posts table was created");
        jdbcTemplate.execute(sqlImages);
        System.out.println("INFO: images table was created");
    }
}
