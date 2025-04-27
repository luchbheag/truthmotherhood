package org.example.dao;

import org.example.entity.Image;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class ImageDao {
    private final static String ADD_IMAGE_SQL = "INSERT OR IGNORE INTO images (image_id, height, width, url, wall_post_id, inner_post_id, comment_id, topic_comment_id) VALUES(?,?,?,?,?,?,?,?)";
    private final static String SELECT_ALL_IMAGES_BY_WALL_POST_SQL = "SELECT image_id, height, width, url, wall_post_id, inner_post_id, topic_comment_id FROM images WHERE wall_post_id = ?";
    private final static String SELECT_ALL_IMAGES_BY_MULTIPLE_WALL_POSTS_SQL = "SELECT image_id, height, width, url, wall_post_id, inner_post_id, comment_id, topic_comment_id FROM images WHERE wall_post_id IN (%s)";
    private final static String SELECT_ALL_IMAGES_BY_MULTIPLE_INNER_POSTS_SQL = "SELECT image_id, height, width, url, wall_post_id, inner_post_id, comment_id, topic_comment_id FROM images WHERE inner_post_id IN (%s)";
    private final static String SELECT_ALL_IMAGES_BY_MULTIPLE_COMMENTS_SQL = "SELECT image_id, height, width, url, wall_post_id, inner_post_id, comment_id, topic_comment_id FROM images WHERE comment_id IN (%s)";
    private final static String SELECT_ALL_IMAGES_BY_MULTIPLE_TOPIC_COMMENTS_SQL = "SELECT image_id, height, width, url, wall_post_id, inner_post_id, comment_id, topic_comment_id FROM images WHERE topic_comment_id IN (%s)";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Image image) {
        jdbcTemplate.update(
                ADD_IMAGE_SQL,
                image.getId(),
                image.getHeight(),
                image.getWidth(),
                image.getUrl(),
                image.getWallPostId(),
                image.getInnerPostId(),
                image.getCommentId()
        );
    }

    public List<Image> findAllByPostId(Long wallPostId) {
        return jdbcTemplate.query(
                SELECT_ALL_IMAGES_BY_WALL_POST_SQL,
                (resultSet, rowNum) -> mapImage(resultSet),
                List.of(wallPostId).toArray()
        );
    }

    public List<Image> findAllByMultipleWallPostIds(List<Long> wallPostIds) {
        String inSql = String.join(",", Collections.nCopies(wallPostIds.size(), "?"));
        String selectQueryStr = String.format(SELECT_ALL_IMAGES_BY_MULTIPLE_WALL_POSTS_SQL, inSql);

        return jdbcTemplate.query(
                selectQueryStr,
                (resultSet, rowNum) -> mapImage(resultSet),
                wallPostIds.toArray()
        );
    }

    public List<Image> findAllByMultipleInnerPostIds(List<Long> innerPostIds) {
        String inSql = String.join(",", Collections.nCopies(innerPostIds.size(), "?"));
        String selectQueryStr = String.format(SELECT_ALL_IMAGES_BY_MULTIPLE_INNER_POSTS_SQL, inSql);

        return jdbcTemplate.query(
                selectQueryStr,
                (resultSet, rowNum) -> mapImage(resultSet),
                innerPostIds.toArray()
        );
    }

    public List<Image> findAllByMultipleCommentIds(List<Long> commentIds) {
        String inSql = String.join(",", Collections.nCopies(commentIds.size(), "?"));
        String selectQueryStr = String.format(SELECT_ALL_IMAGES_BY_MULTIPLE_COMMENTS_SQL, inSql);

        return jdbcTemplate.query(
                selectQueryStr,
                (resultSet, rowNum) -> mapImage(resultSet),
                commentIds.toArray()
        );
    }

    public List<Image> findAllByMultipleTopicCommentIds(List<Long> topicCommentIds) {
        String inSql = String.join(",", Collections.nCopies(topicCommentIds.size(), "?"));
        String selectQueryStr = String.format(SELECT_ALL_IMAGES_BY_MULTIPLE_TOPIC_COMMENTS_SQL, inSql);

        return jdbcTemplate.query(
                selectQueryStr,
                (resultSet, rowNum) -> mapImage(resultSet),
                topicCommentIds.toArray()
        );
    }

    private Image mapImage(ResultSet rs) throws SQLException {
        return new Image(
                rs.getLong("image_id"),
                rs.getInt("height"),
                rs.getInt("width"),
                rs.getString("url"),
                rs.getLong("wall_post_id"),
                rs.getLong("inner_post_id"),
                rs.getLong("comment_id"),
                rs.getLong("topic_comments_id")
        );
    }
}
