package org.example.dao;

import org.example.entity.Image;
import org.example.entity.InnerPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class InnerPostDao {

    private final static String ADD_INNER_POST_SQL = "INSERT OR IGNORE INTO inner_posts (inner_post_id, text, date, wall_post_id) VALUES(?,?,?,?)";
    private final static String SELECT_ALL_INNER_POSTS_BY_MULTIPLE_WALL_POSTS_SQL = "SELECT inner_post_id, text, date, wall_post_id FROM inner_posts WHERE wall_post_id IN (%s)";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InnerPostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<InnerPost> innerPosts) {
        // TODO: make it bulk
        for (InnerPost innerPost : innerPosts) {
            jdbcTemplate.update(
                    ADD_INNER_POST_SQL,
                    innerPost.getInnerPostId(),
                    innerPost.getText(),
                    innerPost.getDate(),
                    innerPost.getWallPostId()
            );
        }
    }

    public List<InnerPost> findAllByMultipleWallPostIds(List<Long> wallPostIds) {
        String inSql = String.join(",", Collections.nCopies(wallPostIds.size(), "?"));
        String selectQueryStr = String.format(SELECT_ALL_INNER_POSTS_BY_MULTIPLE_WALL_POSTS_SQL, inSql);

        return jdbcTemplate.query(
                selectQueryStr,
                (resultSet, rowNum) -> mapInnerPost(resultSet),
                wallPostIds.toArray()
        );
    }

    private InnerPost mapInnerPost(ResultSet rs) throws SQLException {
        return new InnerPost(
                rs.getLong("inner_post_id"),
                rs.getString("text"),
                rs.getLong("wall_post_id")
        );
    }
}
