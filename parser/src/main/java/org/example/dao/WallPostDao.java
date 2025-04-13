package org.example.dao;

import org.example.entity.InnerPost;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WallPostDao {
    private final static int LIMIT = 10;
    private final static String ADD_WALL_POST_SQL="INSERT OR IGNORE INTO wall_posts (wall_post_id, text, date) VALUES(?,?,?)";
    private final static String SELECT_ALL_WALL_POST_SQL="SELECT wall_post_id, text, date FROM wall_posts ORDER BY wall_post_id, date LIMIT ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WallPostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(WallPost wallPost) {
        jdbcTemplate.update(
            ADD_WALL_POST_SQL,
            wallPost.getWallPostId(),
            wallPost.getText(),
            wallPost.getDate()
        );
    }

    public List<WallPost> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_WALL_POST_SQL,
                (resultSet, rowNum) -> mapWallPost(resultSet),
            List.of(LIMIT).toArray()
        );
    }

    private WallPost mapWallPost(ResultSet rs) throws SQLException {
        return new WallPost(
                rs.getLong("wall_post_id"),
                rs.getString("text"),
                LocalDateTime.parse(rs.getString("date"))
        );
    }
}
