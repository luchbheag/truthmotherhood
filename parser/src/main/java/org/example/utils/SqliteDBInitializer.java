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
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS wall_posts (
                    wall_post_id INTEGER PRIMARY KEY,
                    text TEXT NOT NULL,
                    date TEXT NOT NULL
                )
                """;
        jdbcTemplate.execute(sql);
        System.out.println("INFO: wall_posts table created");
    }
}
