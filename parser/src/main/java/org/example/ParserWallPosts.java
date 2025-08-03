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

public class ParserWallPosts extends Parser {
    private static final String videoStr = "https://vk.com/video%d_%d?access_key=%s";
    private final List<String> videos;
    private final List<Long> idsWithOnlyStickers;
    private final JsonParser jParser;
    private Long currentWallPostId;
    private Long currentInnerPostId;
    private Long currentImageId;
    private boolean hasDocuments;
    private boolean hasLinks;
    private boolean hasPoll;
    private boolean hasVideos;
    private boolean hasSticker;
    Map<Long, Long> mapOldToNewWallPostIds;
    Map<Long, Long> mapOldToNewInnerPostIds;

    public ParserWallPosts() throws IOException {
        super();
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = ParserWallPosts.class.getClassLoader().getResourceAsStream("wall_comments.json");

        jParser = objectMapper.getFactory().createParser(is);
        jParser.nextToken();
        this.currentWallPostId = 1L;
        this.currentInnerPostId = 1L;
        this.currentImageId = 2L; // because we need 1 for only image in topics
        this.videos = new ArrayList<>();
        this.idsWithOnlyStickers = new ArrayList<>();
        this.mapOldToNewInnerPostIds = new HashMap<>();
        this.mapOldToNewWallPostIds = new HashMap<>();
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
                            //System.out.println("Parsing wallPost with id = " + getLongValue(jParser));
                            mapOldToNewWallPostIds.put(getLongValue(jParser), this.currentWallPostId);
                            wallPostBuilder.wallPostId(this.currentWallPostId);
                            break;
                        case "text":
                            wallPostBuilder.text(formatTextWithReply(jParser.getText()));
                            break;
                        case "date":
                            wallPostBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getAttachments(jParser);
                            wallPostBuilder.hasDocuments(this.hasDocuments);
                            wallPostBuilder.hasPoll(this.hasPoll);
                            wallPostBuilder.hasVideo(this.hasVideos);
                            wallPostBuilder.hasLinks(this.hasLinks);
                            setAllFlagsFalse();
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
            if (post.getHasVideo() == null) {
                post.setHasVideo(false);
            }
            if (post.getHasLinks() == null) {
                post.setHasLinks(false);
            }
            if (post.getHasPoll() == null) {
                post.setHasPoll(false);
            }
            if (post.getHasDocuments() == null) {
                post.setHasDocuments(false);
            }
            if (!images.isEmpty()) {
                setWallPostIdForImages(this.currentWallPostId, images);
            }
            post.setHasImages(!images.isEmpty());
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
                            mapOldToNewInnerPostIds.put(getLongValue(jParser), this.currentInnerPostId);
                            //getLongValue(jParser);
                            innerPostBuilder.innerPostId(this.currentInnerPostId);
                            break;
                        case "text":
                            innerPostBuilder.text(formatTextWithReply(jParser.getText()));
                            break;
                        case "date":
                            innerPostBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getAttachments(jParser);
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
            innerPostBuilder.hasDocuments(this.hasDocuments);
            innerPostBuilder.hasPoll(this.hasPoll);
            innerPostBuilder.hasVideo(this.hasVideos);
            innerPostBuilder.hasLinks(this.hasLinks);
            setAllFlagsFalse();
            innerPost = innerPostBuilder.build();
            if (!images.isEmpty()) {
                setInnerPostIdForImages(innerPost.getInnerPostId(), images);
                setWallPostIdForImages(this.currentWallPostId, images);
            }
            innerPost.setHasImages(!images.isEmpty());
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
        resetMaps();

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
            commentBuilder.hasImages(false);
            List<Image> images = new ArrayList<>();
            List<Comment> thread = new ArrayList<>();
            long oldId = 0L;
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            oldId = getLongValue(jParser);
                            commentBuilder.commentId(getNewCommentIdByOldId(oldId));
                            break;
                        case "from_id":
                            long userId = getNewUserIdByOldId(getLongValue(jParser));
                            String userName = mapNewUserIdToName.get(userId);
                            commentBuilder.userId(userId);
                            commentBuilder.userName(userName);
                            break;
                        case "text":
                            jParser.nextToken();
                            commentBuilder.text(formatTextWithReply(jParser.getText()));
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getAttachments(jParser);
                            commentBuilder.wallPostId(this.currentWallPostId);
                            commentBuilder.hasDocuments(this.hasDocuments);
                            commentBuilder.hasVideo(this.hasVideos);
                            commentBuilder.hasLinks(this.hasLinks);
                            commentBuilder.hasOnlySticker(this.hasSticker);
                            setAllFlagsFalse();
                            break;
                        case "post_id":
                            getLongValue(jParser);
                            commentBuilder.wallPostId(this.currentWallPostId);
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
            comment = commentBuilder.build();
            if (comment.getHasVideo() == null) {
                comment.setHasVideo(false);
            }
            if (comment.getHasLinks() == null) {
                comment.setHasLinks(false);
            }
            if (comment.getHasDocuments() == null) {
                comment.setHasDocuments(false);
            }
            if (!images.isEmpty()) {
                setCommentIdForImages(comment.getCommentId(), images);
            }
            if (comment.getHasOnlySticker() == null) {
                comment.setHasOnlySticker(false);
            }
            if (comment.getWallPostId() == null || comment.getWallPostId().equals(0L)) {
                comment.setWallPostId(currentWallPostId);
            }
            comment.setHasImages(!images.isEmpty());
            comment.setImages(images);
            if (comment.getText().isEmpty()
                && !(comment.getHasDocuments() || comment.getHasLinks() ||
                    comment.getHasVideo())
                && comment.getImages().isEmpty()) {
                if (!thread.isEmpty()) {
                    //System.out.println("This comment is thread-starter: " + comment.getCommentId() + " - " + oldId + " has only sticker " + comment.getHasOnlySticker());
                    comments.add(comment);
                    comments.addAll(thread);
                    if (comment.getHasOnlySticker()) {
                        idsWithOnlyStickers.add(oldId);
                        idsWithOnlyStickers.add(comment.getCommentId());
                    }
                }
            } else {
                comments.add(comment);
                comments.addAll(thread);
            }
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
        commentBuilder.hasImages(false);
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
                            commentBuilder.text(formatTextWithReply(jParser.getText()));
                            break;
                        case "from_id":
                            long userId = getNewUserIdByOldId(getLongValue(jParser));
                            String userName = mapNewUserIdToName.get(userId);
                            commentBuilder.userId(userId);
                            commentBuilder.userName(userName);
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
                            // no cases where reply_to_user = 0
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.userToAnswerId(getNewUserIdByOldId(Math.abs(jParser.getLongValue())));
                            break;
                        case "reply_to_comment":
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.commentToAnswerId(getNewCommentIdByOldId(Math.abs(jParser.getLongValue())));
                            break;
                        case "attachments":
                            images = getAttachments(jParser);
                            break;
                    }
                }
                jParser.nextToken();
            }
            commentBuilder.hasDocuments(this.hasDocuments);
            commentBuilder.hasVideo(this.hasVideos);
            commentBuilder.hasLinks(this.hasLinks);
            commentBuilder.hasOnlySticker(false);
            setAllFlagsFalse();
            comment = commentBuilder.build();
            if (!images.isEmpty()) {
                setCommentIdForImages(comment.getCommentId(), images);
            }
            comment.setHasImages(!images.isEmpty());
            comment.setImages(images);
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
        }
        return comment;
    }

    private List<Image> getAttachments(JsonParser jParser) throws IOException {
        List<Image> images = new ArrayList<>();
        System.out.println("Processing attachments");
        setAllFlagsFalse();
        jParser.nextToken();
        jParser.nextToken();
        setAllFlagsFalse();
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
                        case "link":
                            this.hasLinks = true;
                            jParser.skipChildren();
                            break;
                        case "video":
                            this.hasVideos = true;
//                            getAndWriteInFileVideoSettings(jParser);
                            getVideo(jParser);
                            break;
                        case "sticker":
                            this.hasSticker = true;
                            jParser.skipChildren();
                            break;
                        case "poll":
                            this.hasPoll = true;
                            jParser.skipChildren();
                            break;
                    }
                }
            }
        }
        return images;
    }

    private void getVideo(JsonParser jParser) throws IOException {
        String accessKey = "";
        long ownerId = 0L;
        long id = 0L;
        try {
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && "video".equals(jParser.currentName()))) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "access_key":
                            accessKey = jParser.getText();
                            break;
                        case "owner_id":
                            ownerId = getLongValue(jParser);
                            break;
                        case "id":
                            id = getLongValue(jParser);
                            break;
                        case "restriction":
                            jParser.skipChildren();
                            break;
                    }
                }
                jParser.nextToken();
            }
            videos.add(String.format(videoStr, ownerId, id, accessKey));
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
        }
    }

    private Image getImage(JsonParser jParser) throws IOException {
        jParser.nextToken();
        Image image = null;
        try {
            Image.ImageBuilder imageBuilder = Image.builder();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && "photo".equals(jParser.currentName()))) {
                if (jParser.currentName() != null) {
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
        return Math.abs(jParser.getLongValue());
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

    public void addAllInAFile() {
        //JsonWriter.writeLinksToJsonFile(videos, "video_links.json");
        JsonWriter.writeIdsToJsonFile(idsWithOnlyStickers, "sticker_comments.json");
        JsonWriter.writeIdsMapToJsonFile(mapOldToNewWallPostIds, "wall_post_map.json");
        JsonWriter.writeIdsMapToJsonFile(mapOldToNewInnerPostIds, "inner_post_map.json");
        JsonWriter.writeIdsMapToJsonFile(mapOldToNewCommentIds, "wall_post_comment_map.json");
    }

    private void getAndWriteInFileVideoSettings(JsonParser jParser) throws IOException {
        jParser.skipChildren();
    }

    private void setAllFlagsFalse() {
        this.hasDocuments = false;
        this.hasLinks = false;
        this.hasPoll = false;
        this.hasVideos = false;
        this.hasSticker = false;
    }
}
