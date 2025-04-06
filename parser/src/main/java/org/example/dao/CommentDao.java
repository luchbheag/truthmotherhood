package org.example.dao;

import org.example.entity.Comment;
import org.example.entity.InnerPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class CommentDao {
    private final static String ADD_COMMENT_SQL = "INSERT OR IGNORE INTO comments (comment_id, user_id, text, date, wall_post_id, thread_starter_id, comment_to_answer_id) VALUES(?,?,?,?,?,?,?)";
    private final static String SELECT_ALL_COMMENTS_BY_WALL_POST_SQL = "SELECT comment_id, user_id, text, date, wall_post_id, thread_starter_id, comment_to_answer_id FROM comments WHERE wall_post_id = ?";
    private final static String COUNT_ALL_SQL = "SELECT COUNT(*) as count FROM comments";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void saveAll(List<Comment> comments) {
        for (Comment comment : comments) {
            jdbcTemplate.update(
                    ADD_COMMENT_SQL,
                    comment.getCommentId(),
                    comment.getUserId(),
                    comment.getText(),
                    comment.getDate(),
                    comment.getWallPostId(),
                    comment.getThreadStarterId(),
                    comment.getCommentToAnswerId()
            );
        }
    }

    public List<Comment> findAllByWallPostId(Long wallPostId) {
        return jdbcTemplate.query(
                SELECT_ALL_COMMENTS_BY_WALL_POST_SQL,
                (resultSet, rowNum) -> mapComment(resultSet),
                List.of(wallPostId).toArray()
        );
    }

    public int countAllCommentsInTable() {
        return jdbcTemplate.query(
               COUNT_ALL_SQL, (resultSet, rowNum) -> {
                   return resultSet.getInt("count");
               }
        ).get(0);
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getLong("comment_id"),
                rs.getLong("user_id"),
                rs.getString("text"),
                rs.getLong("wall_post_id"),
                rs.getLong("thread_starter_id"),
                rs.getLong("comment_to_answer_id")
        );
    }
}
