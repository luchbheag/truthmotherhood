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
    public List<WallPost> parse() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("wall_comments.json");

        List<WallPost> posts = new ArrayList<>();
        List<SimpleComment> comments = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        try (JsonParser jParser = objectMapper.getFactory().createParser(is)) {
            String text = "";
            int id = 0;

            jParser.nextToken();
            WallPost.WallPostBuilder wallPostBuilder = WallPost.builder();
            while (jParser.nextToken() != null) {
                if (jParser.currentName() != null) {
                    //System.out.println(jParser.currentToken() + " " + jParser.currentName());
                    switch (jParser.currentName()) {
                        case "id":
                            jParser.nextToken();
                            wallPostBuilder.id(jParser.getLongValue());
                            //System.out.println("ID: " + jParser.getLongValue());
                            break;
                        case "text":
                            wallPostBuilder.text(jParser.getText());
                           //System.out.println("Text: " + jParser.getText());
                            break;
                        case "date":
                            //System.out.println("!!!" + jParser.currentToken() + " " + jParser.currentName());
                            wallPostBuilder.date(getDateTime(getLongValue(jParser)));
                            //System.out.println("Date: " + getDateTime(getLongValue(jParser)));
                            break;
                        case "attachments":
                            images = getImages(jParser);
                            break;
                        case "copy_history":
                            //System.out.println("Copy history");
                            wallPostBuilder.innerPost(getInnerPost(jParser));
                            //System.out.println("Inner post");
//                            wallPostBuilder.innerPost(getInnerPost(jParser));
//                            getInnerPost(jParser);
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
                if (jParser.currentToken() == JsonToken.END_OBJECT) {
//                    wallPostBuilder.images(images);
                    WallPost post = wallPostBuilder.build();
//                    post.setComments(comments);
//                    post.setImages(new ArrayList<>());
                    posts.add(post);
                    wallPostBuilder = WallPost.builder();
                    comments = new ArrayList<>();
                    images = new ArrayList<>();
                    jParser.nextToken();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        posts.forEach(it -> {
//            System.out.println(it.getId());
//            System.out.println(it.getText());
//            System.out.println("Comments: " + it.getComments().size());
//            it.getComments().forEach(comment -> {
//                System.out.println("\t" + comment);
//                if (!comment.getImages().isEmpty()) {
//                    System.out.println(comment.getImages());
//                }
//                comment.getThreadComments().forEach(
//                        threadComment -> {
//                            System.out.println("\t\t" + threadComment);
//                            if (!threadComment.getImages().isEmpty()) {
//                                System.out.println(threadComment.getImages());
//                            }
//                        }
//                );
//            });
//            it.getImages().forEach(System.out::println);
//        });
//        System.out.println("!!!");
//        posts.forEach(System.out::println);
//        System.out.println("!!!");
        return posts;
    }

    private InnerPost getInnerPost(JsonParser jParser) throws IOException {
        // CAN IT BE MORE THAN 1 COPY_HISTORY? YES
        //System.out.println("HERE");
        InnerPost.InnerPostBuilder innerPostBuilder = InnerPost.builder();
        jParser.nextToken();
        jParser.nextToken();
        jParser.nextToken();
        while (!(jParser.currentToken() == JsonToken.END_OBJECT)) {
            //System.out.println(jParser.currentToken() + " " + jParser.currentName());
            switch (jParser.currentName()) {
                case "id":
                    innerPostBuilder.id(getLongValue(jParser));
                    break;
                case "text":
                    innerPostBuilder.text(jParser.getText());
                    break;
                case "date":
                    innerPostBuilder.date(getDateTime(getLongValue(jParser)));
                    break;
                case "attachments":
//                    innerPostBuilder.images(getImages(jParser));
                    getImages(jParser);
                    //System.out.println("ATTACHMENTS + image");
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
        InnerPost innerPost = innerPostBuilder.build();
        //System.out.println("HERE!");
        //System.out.println("InnerPost: " + innerPost);
        return innerPost;
    }

    private List<SimpleComment> getComments(JsonParser jParser) throws IOException {
        List<SimpleComment> comments = new ArrayList<>();
        List<ThreadComment> threadComments;
        List<Image> images = new ArrayList<>();
        SimpleComment.SimpleCommentBuilder<?, ?> commentBuilder = SimpleComment.builder();

        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "comms".equals(jParser.currentName()))) {

            if (jParser.currentName() != null) {
                switch (jParser.currentName()) {
                    case "id":
                        jParser.nextToken();
                        commentBuilder.id(jParser.getLongValue());
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
                    case "post_id":
                        jParser.nextToken();
                        commentBuilder.postId(jParser.getLongValue());
                        break;
                    case "parents_stack":
                        jParser.skipChildren();
                        break;
                    case "thread":
                        threadComments = getThreadComments(jParser);
                        commentBuilder.threadComments(threadComments);
                        break;
                }
            }
            if (jParser.getCurrentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null) {
                commentBuilder.images(images);
                SimpleComment comment = commentBuilder.build();
                if (!comment.isEmpty()) {
                    comments.add(comment);
                }
                commentBuilder = SimpleComment.builder();
                images = new ArrayList<>();
            }
            jParser.nextToken();
        }
        return comments;
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
        ThreadComment.ThreadCommentBuilder<?, ?> commentBuilder = ThreadComment.builder();
        List<Image> images = new ArrayList<>();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "items".equals(jParser.currentName()))) {
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
                        images = getImages(jParser);
                        break;
                }
            }
            if (jParser.getCurrentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null) {
                commentBuilder.images(images);
                ThreadComment comment = commentBuilder.build();
                if (!comment.isEmpty()) {
                    threadComments.add(comment);
                }
                commentBuilder = ThreadComment.builder();
                images = new ArrayList<>();
            }
            jParser.nextToken();
        }
        skipUntilEndOfThread(jParser);

        return threadComments;
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
//        jParser.nextToken();
//        jParser.nextToken();
        //System.out.println(jParser.currentToken() + " " + jParser.currentName());
        Image.ImageBuilder imageBuilder = Image.builder();
        while (!(jParser.currentToken() == JsonToken.END_OBJECT
                && "photo".equals(jParser.currentName()))) {
            //System.out.println(jParser.currentToken() + " " + jParser.currentName());
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
        return imageBuilder.build();
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
}
