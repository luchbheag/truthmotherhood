package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private final JsonParser jParser;
    private Long currentWallPostId;
    private Long currentInnerPostId;
    private Long currentImageId;
    private Long currentCommentId;
    private Long currentUserId;
    private final Random random;
    private Map<Long, Long> mapOldToNewCommentIds;
    private Map<Long, Long> mapOldToNewUserIds;

    public Parser() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("wall_comments.json");

        jParser = objectMapper.getFactory().createParser(is);
        jParser.nextToken();
        this.currentWallPostId = 1L;
        this.currentInnerPostId = 1L;
        this.currentImageId = 2L; // because we need 1 for only image in topics
        this.currentCommentId = 1L;
        this.random = new Random();
    }

    public WallPost parseWallPost() throws IOException {
        WallPost post = null;
        try {
            WallPost.WallPostBuilder wallPostBuilder = WallPost.builder();
            List<Image> images = new ArrayList<>();
            List<InnerPost> innerPosts = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();
            while (jParser.nextToken() != null && jParser.currentToken() != JsonToken.END_OBJECT) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            System.out.println("Parsing wallPost with id = " + getLongValue(jParser));
                            wallPostBuilder.wallPostId(this.currentWallPostId);
                            break;
                        case "text":
                            wallPostBuilder.text(jParser.getText());
                            break;
                        case "date":
                            wallPostBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getImages(jParser);
                            break;
                        case "copy_history":
                            innerPosts = getInnerPosts(jParser);
                            break;
                        case "comms":
                            comments = getComments(jParser);
                            break;
                        case "likes":
                        case "comments":
                        case "reposts":
                        case "post_source":
                        case "views":
                            jParser.nextToken();
                            jParser.skipChildren();
                            jParser.nextToken();
                            break;
                    }
                }
            }
            post = wallPostBuilder.build();
            if (!images.isEmpty()) {
                setWallPostIdForImages(this.currentWallPostId, images);
            }
            post.setImages(images);
            if (!innerPosts.isEmpty()) {
                setWallPostIdForInnerPosts(this.currentWallPostId, innerPosts);
            }
            post.setInnerPosts(innerPosts);
            post.setComments(comments);
            this.currentWallPostId++;
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
            this.currentWallPostId++;
        }
        return post;
    }

    private List<InnerPost> getInnerPosts(JsonParser jParser) throws IOException {
        List<InnerPost> innerPosts = new ArrayList<>();

        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "copy_history".equals(jParser.currentName()))) {
            if (jParser.currentToken() == JsonToken.START_OBJECT
                    && jParser.currentName() == null) {
                InnerPost innerPost = getInnerPost(jParser);
                if (innerPost != null) {
                    innerPosts.add(innerPost);
                }
            }
            jParser.nextToken();
        };
        return innerPosts;
    }

    private InnerPost getInnerPost(JsonParser jParser) throws IOException {
        InnerPost innerPost = null;
        try {
            InnerPost.InnerPostBuilder innerPostBuilder = InnerPost.builder();
            List<Image> images = new ArrayList<>();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            getLongValue(jParser);
                            innerPostBuilder.innerPostId(this.currentInnerPostId);
                            break;
                        case "text":
                            innerPostBuilder.text(jParser.getText());
                            break;
                        case "date":
                            innerPostBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getImages(jParser);
                            break;
                        case "post_source":
                            jParser.nextToken();
                            jParser.skipChildren();
                            jParser.nextToken();
                            break;
                    }
                }
                jParser.nextToken();
            }
            innerPost = innerPostBuilder.build();
            if (!images.isEmpty()) {
                setInnerPostIdForImages(innerPost.getInnerPostId(), images);
            }
            innerPost.setImages(images);
            innerPost.setWallPostId(this.currentWallPostId);
            this.currentInnerPostId++;
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren(); // bad ones
            this.currentInnerPostId++;
        }
        return innerPost;
    }

    private List<Comment> getComments(JsonParser jParser) throws IOException {
        List<Comment> comments = new ArrayList<>();
        this.mapOldToNewCommentIds = new HashMap<>();
        this.mapOldToNewUserIds = new HashMap<>();
        this.currentUserId = 40000L + random.nextInt(1000);

        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "comms".equals(jParser.currentName()))) {
            if (jParser.currentToken() == JsonToken.START_OBJECT
            && jParser.currentName() == null) {
                getComment(jParser, comments);
            }
            jParser.nextToken();
        };
        return comments;
    }

    private void getComment(JsonParser jParser, List<Comment> comments) {
        Comment comment = null;
        try {
            Comment.CommentBuilder commentBuilder = Comment.builder();
            List<Image> images = new ArrayList<>();
            List<Comment> thread = new ArrayList<>();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            commentBuilder.commentId(getNewCommentIdByOldId(getLongValue(jParser)));
                            break;
                        case "from_id":
                            commentBuilder.userId(getNewUserIdByOldId(getLongValue(jParser)));
                            break;
                        case "text":
                            jParser.nextToken();
                            commentBuilder.text(jParser.getText());
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getImages(jParser);
                            break;
                        case "post_id":
                            getLongValue(jParser);
                            break;
                        case "parents_stack":
                            jParser.skipChildren();
                            break;
                        case "thread":
                            jParser.nextToken();
                            thread = getThreadComments(jParser);
                            break;
                    }
                }
                jParser.nextToken();
            }
            commentBuilder.wallPostId(this.currentWallPostId);
            comment = commentBuilder.build();
            if (!images.isEmpty()) {
                setCommentIdForImages(comment.getCommentId(), images);
            }
            comment.setImages(images);
            comments.add(comment);
            comments.addAll(thread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Comment> getThreadComments(JsonParser jParser) throws IOException {
        List<Comment> threadComments = new ArrayList<>();
        jParser.nextToken();
        while (!(jParser.currentToken() == JsonToken.VALUE_NUMBER_INT
                && "count".equals(jParser.currentName()))) {
            jParser.nextToken();
        }
        if (jParser.getIntValue() == 0) {
            skipUntilEndOfThread(jParser);
            return threadComments;
        }
        skipUntilItems(jParser);
        skipUntilItems(jParser);
        jParser.nextToken();
        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "items".equals(jParser.currentName()))) {
            Comment threadComment = getThreadComment(jParser);
            if (threadComment != null) {
                threadComments.add(threadComment);
            }
            jParser.nextToken();
        }
        skipUntilEndOfThread(jParser);

        return threadComments;
    }

    private Comment getThreadComment(JsonParser jParser) throws IOException {
        Comment comment = null;
        Comment.CommentBuilder commentBuilder = Comment.builder();
        try {
            List<Image> images = new ArrayList<>();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            commentBuilder.commentId(getNewCommentIdByOldId(getLongValue(jParser)));
                            break;
                        case "text":
                            jParser.nextToken();
                            commentBuilder.text(formatTextInThreadComment(jParser.getText()));
                            break;
                        case "from_id":
                            commentBuilder.userId(getNewUserIdByOldId(getLongValue(jParser)));
                            break;
                        case "post_id":
                            getLongValue(jParser);
                            commentBuilder.wallPostId(this.currentWallPostId);
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "parents_stack":
                            jParser.nextToken();
                            commentBuilder.threadStarterId(getNewCommentIdByOldId(getLongValue(jParser)));
                            jParser.nextToken();
                            break;
                        case "reply_to_user":
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.userToAnswerId(getNewUserIdByOldId(jParser.getLongValue()));
                            jParser.getLongValue();
                            break;
                        case "reply_to_comment":
                            // what if comment was deleted?
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.commentToAnswerId(getNewCommentIdByOldId(jParser.getLongValue()));
                            break;
                        case "attachments":
                            images = getImages(jParser);
                            break;
                    }
                }
                jParser.nextToken();
            }
            comment = commentBuilder.build();
            if (!images.isEmpty()) {
                setCommentIdForImages(comment.getCommentId(), images);
            }
            comment.setImages(images);
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
        }
        return comment;
    }

    private List<Image> getImages(JsonParser jParser) throws IOException {
        List<Image> images = new ArrayList<>();
        jParser.nextToken();
        jParser.nextToken();
        while (!(jParser.currentToken() == JsonToken.END_ARRAY
                && "attachments".equals(jParser.currentName()))) {
            jParser.nextToken();
            if (jParser.currentToken() == JsonToken.START_OBJECT
                    && "photo".equals(jParser.currentName())) {
                images.add(getImage(jParser));
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

    private void skipUntilEndOfThread(JsonParser jParser) throws IOException {
        while (!(jParser.currentToken() == JsonToken.END_OBJECT
                && "thread".equals(jParser.currentName()))) {
            jParser.nextToken();
        }
    }

    private void skipUntilItems(JsonParser jParser) throws IOException {
        while (!"items".equals(jParser.currentName())) {
            jParser.nextToken();
        }
    }

    private long getLongValue(JsonParser jParser) throws IOException {
        jParser.nextToken();
        return jParser.getLongValue();
    }

    private int getIntValue(JsonParser jParser) throws IOException {
        jParser.nextToken();
        return jParser.getIntValue();
    }

    private LocalDateTime getDateTime(long dateAsLong) throws IOException {
        return Instant.ofEpochSecond(dateAsLong)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public boolean isEmpty() {
        return jParser.currentToken() == null;
    }

    private void setWallPostIdForInnerPosts(Long wallPostId, List<InnerPost> innerPosts) {
        innerPosts.forEach(innerPost -> innerPost.setWallPostId(wallPostId));
    }

    private void setWallPostIdForImages(Long wallPostId, List<Image> images) {
        images.forEach(image -> image.setWallPostId(wallPostId));
    }

    private void setInnerPostIdForImages(Long innerPostId, List<Image> images) {
        images.forEach(image -> image.setInnerPostId(innerPostId));
    }

    private void setCommentIdForImages(Long commentId, List<Image> images) {
        images.forEach(image -> image.setCommentId(commentId));
    }

    private long getNewUserIdByOldId(Long oldUserId) {
        if (!this.mapOldToNewUserIds.containsKey(oldUserId)) {
            this.mapOldToNewUserIds.put(oldUserId, this.currentUserId);
            this.currentUserId += random.nextInt(20);
        }
        return this.mapOldToNewUserIds.get(oldUserId);
    }

    private long getNewCommentIdByOldId(Long oldId) {
        if (!this.mapOldToNewCommentIds.containsKey(oldId)) {
            this.mapOldToNewCommentIds.put(oldId, this.currentCommentId++);
        }
        return this.mapOldToNewCommentIds.get(oldId);
    }

    private String formatTextInThreadComment(String text) {
        Pattern pattern = Pattern.compile("\\[id(\\d+)\\|");
        Matcher matcher = pattern.matcher(text);
        Long newUserId = 0L;

        if (matcher.find()) {
            String numberStr = matcher.group(1);
            Long oldUserId = Long.parseLong(numberStr);
            newUserId = getNewUserIdByOldId(oldUserId);
        }

        return text.replaceFirst("\\[id\\d+\\|[^\\]]+]", String.format("[id%d]", newUserId));
    }
}
