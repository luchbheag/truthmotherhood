package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Topic;
import org.example.entity.TopicComment;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParserTopics {

    private final JsonParser jParser;

    public ParserTopics() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("topics.json");

        jParser = objectMapper.getFactory().createParser(is);
        jParser.nextToken();
    }

    public List<Topic> parseTopics() throws IOException {
        List<Topic> topics = new ArrayList<>();

        try {
            while (!(jParser.currentToken() == JsonToken.START_ARRAY && "items".equals(jParser.currentName()))) {
                jParser.nextToken();
            }
            while (!(jParser.currentToken() == JsonToken.END_ARRAY && "items".equals(jParser.currentName()))) {
                Topic topic = parseTopic();
                if (topic != null) {
                    System.out.println("\uD83D\uDD25" + topic.getTopicId());
                    topics.add(topic);
                }
                jParser.nextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return topics;
    }

    private Topic parseTopic() {
        Topic topic = null;
        List<TopicComment> comments = new ArrayList<>();
        try {
            Topic.TopicBuilder topicBuilder = Topic.builder();
            while (jParser.currentToken() != null && jParser.currentToken() != JsonToken.END_OBJECT) {
                if (jParser.currentName() != null) {
                    System.out.println("!!" + jParser.currentToken() + " " + jParser.currentName());
                    switch (jParser.currentName()) {
                        case "id":
                            topicBuilder.topicId(getLongValue(jParser));
                            break;
                        case "created":
                            topicBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "title":
                            topicBuilder.title(jParser.getText());
                            break;
                        case "comments":
                            jParser.nextToken();
                            System.out.println("@#$$#@" + jParser.currentName() + jParser.currentToken());
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                comments = getComments();
                                System.out.println("COMMENTS SIZE:" + comments.size());
                            }
                            break;
                    }
                }
                jParser.nextToken();
            }
            topic = topicBuilder.build();
            topic.setComments(comments);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }

    private List<TopicComment> getComments() {
        List<TopicComment> comments = new ArrayList<>();
        try {
            jParser.nextToken();
            System.out.println("FIRST TOKEN AFTER: " + jParser.currentToken() + jParser.currentName());
            if (jParser.currentToken() == JsonToken.END_OBJECT) {
                return comments;
            }
            while (jParser.currentToken() != JsonToken.START_ARRAY) {
                jParser.nextToken();
            }

            while (jParser.currentToken() != JsonToken.END_ARRAY) {
                TopicComment comment = getComment();
                if (comment != null) {
                    comments.add(comment);
                }
                if (jParser.currentToken() == JsonToken.END_OBJECT) {
                    jParser.nextToken();
                }
            }
            jParser.nextToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comments;
    }

    private TopicComment getComment() {
        TopicComment comment = null;
        try {
            TopicComment.TopicCommentBuilder commentBuilder = TopicComment.builder();
            while (jParser.currentToken() != JsonToken.END_OBJECT) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            commentBuilder.topicCommentId(getLongValue(jParser));
                            break;
                        case "text":
                            commentBuilder.text(jParser.getText());
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "from_id":
                            commentBuilder.userId(getLongValue(jParser));
                            break;
                        case "attachments":
                            jParser.nextToken();
                            jParser.skipChildren();
                            jParser.nextToken();
                            break;
                    }
                }
                jParser.nextToken();
            }
            comment = commentBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comment;
    }

    private Long getLongValue(JsonParser jParser) throws IOException {
        jParser.nextToken();
        return jParser.getLongValue();
    }

    private LocalDateTime getDateTime(long dateAsLong) throws IOException {
        return Instant.ofEpochSecond(dateAsLong)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private void setId
}
