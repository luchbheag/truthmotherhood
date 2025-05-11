package org.example.dao;

import org.example.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentDao {
    private final static String ADD_COMMENT_SQL = "INSERT OR IGNORE INTO comments (comment_id, user_id, text, date, wall_post_id, thread_starter_id, comment_to_answer_id, user_to_answer_id, has_documents, has_links, has_videos, has_only_stickers) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    private final static String SELECT_ALL_COMMENTS_BY_WALL_POST_SQL = "SELECT comment_id, user_id, text, date, wall_post_id, thread_starter_id, comment_to_answer_id, user_to_answer_id, has_documents, has_links, has_videos, has_only_stickers FROM comments WHERE wall_post_id = ?";
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
                    comment.getCommentToAnswerId(),
                    comment.getUserToAnswerId(),
                    comment.getHasDocuments(),
                    comment.getHasLinks(),
                    comment.getHasVideo(),
                    comment.getHasOnlySticker()
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
                LocalDateTime.parse(rs.getString("date")),
                rs.getLong("wall_post_id"),
                rs.getLong("thread_starter_id"),
                rs.getLong("comment_to_answer_id"),
                rs.getLong("user_to_answer_id"),
                rs.getInt("has_documents") > 0,
                rs.getInt("has_links") > 0,
                rs.getInt("has_videos") > 0,
                rs.getInt("has_only_stickers") > 0
        );
    }
}
