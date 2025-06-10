package org.example.dao;

import org.example.entity.Topic;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TopicDao {
    private final static String ADD_TOPIC_SQL="INSERT OR IGNORE INTO topics (topic_id, title, date, number_of_comments) VALUES(?,?,?,?)";
    private final static String SELECT_ALL_TOPICS_SQL="SELECT topic_id, title, date, number_of_comments FROM topics ORDER BY topic_id, date";
    private final static String SELECT_TOPIC_BY_ID_SQL="SELECT * FROM topics WHERE topic_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TopicDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Topic topic) {
        jdbcTemplate.update(
                ADD_TOPIC_SQL,
                topic.getTopicId(),
                topic.getTitle(),
                topic.getDate(),
                topic.getNumberOfComments()
        );
    }

    public List<Topic> findAll() {
        return jdbcTemplate.query(
                SELECT_ALL_TOPICS_SQL,
                (resultSet, rowNum) -> mapTopic(resultSet)
        );
    }

    public Topic findById(Long topicId) {
        return jdbcTemplate.queryForObject(
                SELECT_TOPIC_BY_ID_SQL,
                (resultSet, rowNum) -> mapTopic(resultSet),
                topicId
        );
    }

    private Topic mapTopic(ResultSet rs) throws SQLException {
        return new Topic(
                rs.getLong("topic_id"),
                rs.getString("title"),
                LocalDateTime.parse(rs.getString("date")),
                rs.getInt("number_of_comments")
        );
    }

}
