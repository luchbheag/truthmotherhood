package org.example.dao;

import org.example.entity.Topic;
import org.example.entity.TopicComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TopicCommentDao {
    private final static String ADD_TOPIC_COMMENT_SQL="INSERT OR IGNORE INTO topic_comments (topic_comment_id, text, date, user_id, topic_id, has_documents) VALUES(?,?,?,?,?,?)";
    private final static String SELECT_ALL_TOPIC_COMMENTS_BY_ID_SQL="SELECT topic_comment_id, text, date, user_id, topic_id, has_documents FROM topic_comments WHERE topic_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TopicCommentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(TopicComment comment) {
        jdbcTemplate.update(
                ADD_TOPIC_COMMENT_SQL,
                comment.getTopicCommentId(),
                comment.getText(),
                comment.getDate(),
                comment.getUserId(),
                comment.getTopicId(),
                comment.getHasDocuments() ? 1 : 0
        );
    }

    public List<TopicComment> findAllByTopicId(Long topicId) {
        return jdbcTemplate.query(
                SELECT_ALL_TOPIC_COMMENTS_BY_ID_SQL,
                (resultSet, rowNum) -> mapTopicComment(resultSet),
                topicId
        );
    }

    private TopicComment mapTopicComment(ResultSet rs) throws SQLException {
        return new TopicComment(
                rs.getLong("topic_comment_id"),
                rs.getString("text"),
                LocalDateTime.parse(rs.getString("date")),
                rs.getLong("user_id"),
                rs.getLong("topic_id"),
                rs.getInt("has_documents") > 0
        );
    }
}
