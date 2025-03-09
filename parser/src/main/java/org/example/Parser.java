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
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final JsonParser jParser;

    public Parser() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("wall_comments.json");

        jParser = objectMapper.getFactory().createParser(is);
        jParser.nextToken();
    }

    public WallPost parseWallPost() throws IOException {
        WallPost post = null;
        try {
            WallPost.WallPostBuilder wallPostBuilder = WallPost.builder();
            List<Image> images = new ArrayList<>();
            List<SimpleComment> comments = new ArrayList<>();
            while (jParser.nextToken() != null && jParser.currentToken() != JsonToken.END_OBJECT) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            jParser.nextToken();
                            wallPostBuilder.wallPostId(jParser.getLongValue());
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
                            InnerPost innerPost = getInnerPost(jParser);
                            if (innerPost != null) {
                                wallPostBuilder.innerPost(innerPost);
                            }
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
            post.setImages(images);
            post.setComments(comments);
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren();
        }
        return post;
    }

    private InnerPost getInnerPost(JsonParser jParser) throws IOException {
        // TODO: process other InnerPosts (can be multiple if it is repost of repost etc
        InnerPost innerPost = null;
        try {
            InnerPost.InnerPostBuilder innerPostBuilder = InnerPost.builder();
            List<Image> images = new ArrayList<>();
            jParser.nextToken();
            jParser.nextToken();
            jParser.nextToken();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT)) {
                switch (jParser.currentName()) {
                    case "id":
                        innerPostBuilder.innerPostId(getLongValue(jParser));
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
                jParser.nextToken();
            }
            jParser.nextToken();
            jParser.nextToken();
            innerPost = innerPostBuilder.build();
            innerPost.setImages(images);
        } catch (IOException e) {
            e.printStackTrace();
            jParser.skipChildren(); // bad ones
        }
        return innerPost;
    }

    private List<SimpleComment> getComments(JsonParser jParser) throws IOException {
        List<SimpleComment> comments = new ArrayList<>();

        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "comms".equals(jParser.currentName()))) {
            if (jParser.currentToken() == JsonToken.START_OBJECT
            && jParser.currentName() == null) {
                SimpleComment comment = getComment(jParser);
                if (comment != null) {
                    comments.add(comment);
                }
            }
            jParser.nextToken();
        };
        return comments;
    }

    private SimpleComment getComment(JsonParser jParser) {
        SimpleComment comment = null;
        try {
            SimpleComment.SimpleCommentBuilder commentBuilder = SimpleComment.builder();
            List<Image> images = new ArrayList<>();
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            jParser.nextToken();
                            commentBuilder.simpleCommentId(jParser.getLongValue());
                            break;
                        case "from_id":
                            jParser.nextToken();
                            commentBuilder.userId(jParser.getLongValue());
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
//                        case "post_id":
//                            jParser.nextToken();
//                            commentBuilder.postId(jParser.getLongValue());
//                            break;
                        case "parents_stack":
                            jParser.skipChildren();
                            break;
                        case "thread":
                            jParser.nextToken();
                            getThreadComments(jParser);
                            break;
                    }
                }
                jParser.nextToken();
            }
            comment = commentBuilder.build();
            comment.setImages(images);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comment;
    }

    List<ThreadComment> getThreadComments(JsonParser jParser) throws IOException {
        List<ThreadComment> threadComments = new ArrayList<>();
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
            ThreadComment threadComment = getThreadComment(jParser);
            if (threadComment != null) {
                threadComments.add(threadComment);
            }
            jParser.nextToken();
        }
        skipUntilEndOfThread(jParser);

        return threadComments;
    }

    private ThreadComment getThreadComment(JsonParser jParser) throws IOException {
        ThreadComment comment = null;
        ThreadComment.ThreadCommentBuilder<?, ?> commentBuilder = ThreadComment.builder();
        try {
            while (!(jParser.currentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null)) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            commentBuilder.id(getLongValue(jParser));
                            break;
                        case "text":
                            jParser.nextToken();
                            commentBuilder.text(jParser.getText());
                            break;
                        case "from_id":
                            commentBuilder.userId(getLongValue(jParser));
                            break;
                        case "post_id":
                            commentBuilder.postId(getLongValue(jParser));
                            break;
                        case "date":
                            commentBuilder.date(getDateTime(getLongValue(jParser)));
                            break;
                        case "parents_stack":
                            jParser.nextToken();
                            commentBuilder.threadStarterId(getLongValue(jParser));
                            jParser.nextToken();
                            break;
                        case "reply_to_user":
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.userOfReply(jParser.getLongValue());
                            break;
                        case "reply_to_comment":
                            // TODO: careful: reply_to_comment and reply_to_user can be null if it adresses to threadStarter
                            if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                                jParser.nextToken();
                            }
                            commentBuilder.commentOfReply(jParser.getLongValue());
                            break;
                        case "attachments":
                            commentBuilder.images(getImages(jParser));
                            break;
                    }
                }
                jParser.nextToken();
            }
            comment = commentBuilder.build();
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
                        imageBuilder.id(getLongValue(jParser));
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
}
