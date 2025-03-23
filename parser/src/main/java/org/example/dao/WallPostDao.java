package org.example.dao;

import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WallPostDao {
    private final static String ADD_WALLPOST_SQL="INSERT OR IGNORE INTO wall_posts (wall_post_id, text, date) VALUES(?,?,?)";
    private final static String SELECT_ALL_WALLPOST_SQL="SELECT * FROM wall_posts";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public WallPostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(WallPost wallPost) {
        jdbcTemplate.update(
            ADD_WALLPOST_SQL,
            wallPost.getWallPostId(),
            wallPost.getText(),
            wallPost.getDate()
        );
    }

    public List<WallPost> findAll() {
//        List<WallPost> posts = new ArrayList<>();
//        jdbcTemplate.query(SELECT_ALL_WALLPOST_SQL, new RowCallbackHandler() {
//            public void processRow(ResultSet resultSet) throws SQLException {
//
//                while (resultSet.next()) {
//                    System.out.println("HERE IT IS");
//                    WallPost wallPost = new WallPost();
//                    wallPost.setWallPostId(resultSet.getLong("wall_post_id"));
//                    wallPost.setText(resultSet.getString("text"));
//                    posts.add(wallPost);
////                    wallPost.setDate(resultSet.getDate("date"));
//                }
//            }
//        });
//        return posts;
        return jdbcTemplate.query(SELECT_ALL_WALLPOST_SQL, (resultSet, rowNum) -> {
            WallPost wallPost = new WallPost();
            wallPost.setWallPostId(resultSet.getLong("wall_post_id"));
            wallPost.setText(resultSet.getString("text"));
            return wallPost;
        });
    }
}
