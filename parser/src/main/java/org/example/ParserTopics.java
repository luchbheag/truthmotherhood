package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Image;
import org.example.entity.Topic;
import org.example.entity.TopicComment;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserTopics extends Parser {

    private final JsonParser jParser;
    private Long currentImageId;
    private boolean hasDocuments;

    public ParserTopics() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = ParserWallPosts.class.getClassLoader().getResourceAsStream("topics.json");

        jParser = objectMapper.getFactory().createParser(is);
        jParser.nextToken();
        this.hasDocuments = false;
        this.currentImageId = 1L;
    }

    public List<Topic> parseTopics() throws IOException {
        List<Topic> topics = new ArrayList<>();

        try {
            while (!(jParser.currentToken() == JsonToken.START_ARRAY && "items".equals(jParser.currentName()))) {
                jParser.nextToken();
            }
            while (!(jParser.currentToken() == JsonToken.END_ARRAY && "items".equals(jParser.currentName()))) {
                Topic topic = parseTopic();
                if (topic != null && topic.getComments() != null && !topic.getComments().isEmpty()) {
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
                    //System.out.println("!!" + jParser.currentToken() + " " + jParser.currentName());
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
                            //System.out.println("@#$$#@" + jParser.currentName() + jParser.currentToken());
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                comments = getComments();
                                //System.out.println("COMMENTS SIZE:" + comments.size());
                            }
                            break;
                    }
                }
                jParser.nextToken();
            }
            topic = topicBuilder.build();
            if (!comments.isEmpty()) {
                setTopicIdForComments(topic.getTopicId(), comments);
                topic.setNumberOfComments(comments.size());
                topic.setComments(comments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topic;
    }

    private List<TopicComment> getComments() {
        List<TopicComment> comments = new ArrayList<>();
        try {
            jParser.nextToken();
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
        hasDocuments = false;
        List<Image> images = new ArrayList<>();
        try {
            TopicComment.TopicCommentBuilder commentBuilder = TopicComment.builder();
            while (jParser.currentToken() != JsonToken.END_OBJECT) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            commentBuilder.topicCommentId(getNewCommentIdByOldId(getLongValue(jParser)));
                            break;
                        case "text":
                            commentBuilder.text(formatTextWithReply(jParser.getText()));
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "from_id":
                            long userId = getNewUserIdByOldId(getLongValue(jParser));
                            String userName = mapNewUserIdToName.get(userId);
                            commentBuilder.userId(userId);
                            commentBuilder.userName(userName);
                            break;
                        case "attachments":
                            images = getAttachments(jParser);
                            break;
                    }
                }
                jParser.nextToken();
            }
            comment = commentBuilder.build();
            if (!images.isEmpty()) {
                setTopicCommentIdForImages(comment.getTopicCommentId(), images);
            }
            comment.setHasImages(!images.isEmpty());
            comment.setImages(images);
            comment.setHasDocuments(this.hasDocuments);
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

    private void setTopicIdForComments(Long topicId, List<TopicComment> comments) {
        comments.forEach(comment -> comment.setTopicId(topicId));
    }

    private List<Image> getAttachments(JsonParser jParser) throws IOException {
        List<Image> images = new ArrayList<>();
        jParser.nextToken();
        jParser.nextToken();
        while (!(jParser.currentToken() == JsonToken.END_ARRAY
                && "attachments".equals(jParser.currentName()))) {
            jParser.nextToken();
            if (jParser.currentToken() == JsonToken.START_OBJECT) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "photo":
                            Image image = getImage(jParser);
                            if (image != null) {
                                images.add(image);
                            }
                            break;
                        case "doc":
                            this.hasDocuments = true;
                            jParser.skipChildren();
                            break;
                    }
                }
            }
        }
        return images;
    }

    private Image getImage(JsonParser jParser) throws IOException {
        jParser.nextToken();
        Image image = null;
        try {
            Image.ImageBuilder imageBuilder = Image.builder();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && "photo".equals(jParser.currentName()))) {
                switch (jParser.currentName()) {
                    case "id":
                        getLongValue(jParser);
                        imageBuilder.id(this.currentImageId++);
                        break;
                    case "orig_photo":
                        fillImageSizesAndUrl(jParser, imageBuilder);
                        break;
                    case "sizes":
                        jParser.skipChildren();
                        break;
                }
                jParser.nextToken();
            }
            image = imageBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
        }
        return image;
    }

    private void fillImageSizesAndUrl(JsonParser jParser, Image.ImageBuilder imageBuilder) throws IOException {
        jParser.nextToken();
        while(jParser.currentToken() != JsonToken.END_OBJECT) {
            switch (jParser.currentName()) {
                case "url":
                    imageBuilder.url(jParser.getText());
                    break;
                case "width":
                    imageBuilder.width(getIntValue(jParser));
                    break;
                case "height":
                    imageBuilder.height(getIntValue(jParser));
                    break;
            }
            jParser.nextToken();
        }
    }

    private int getIntValue(JsonParser jParser) throws IOException {
        jParser.nextToken();
        return Math.abs(jParser.getIntValue());
    }

    private void setTopicCommentIdForImages(Long topicCommentId, List<Image> images) {
        images.forEach(image -> image.setTopicCommentId(topicCommentId));
    }

    public void addAllInAFile() {
        JsonWriter.writeIdsMapToJsonFile(super.mapOldToNewCommentIds, "topic_comment_map.json");
    }

}
