package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Image;
import org.example.entity.SimpleComment;
import org.example.entity.ThreadComment;
import org.example.entity.WallPost;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Parser {
    public void parse() {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("wall_comments.json");

        List<WallPost> posts = new ArrayList<>();
        List<SimpleComment> comments = new ArrayList<>();
        try (JsonParser jParser = objectMapper.getFactory().createParser(is)) {
            String text = "";
            int id = 0;

            jParser.nextToken();
            WallPost.WallPostBuilder wallPostBuilder = WallPost.builder();
            while (jParser.nextToken() != null) {
                if (jParser.currentName() != null) {
                    switch (jParser.currentName()) {
                        case "id":
                            jParser.nextToken();
                            wallPostBuilder.id(jParser.getIntValue());
                            break;
                        case "text":
                            wallPostBuilder.text(jParser.getText());
                            break;
                        case "copy_history":
                        case "attachments":
                        case "likes":
                        case "comments":
                        case "reposts":
                        case "post_source":
                        case "views":
                            jParser.nextToken();
                            jParser.skipChildren();
                            jParser.nextToken();
                            break;
                        case "comms":
                            comments = getComments(jParser);
                            break;
                    }
                }
                if (jParser.getCurrentToken() == JsonToken.END_OBJECT) {
                    WallPost post = wallPostBuilder.build();
                    post.setComments(comments);
                    post.setImages(new ArrayList<>());
                    posts.add(post);
                    wallPostBuilder = WallPost.builder();
                    comments = new ArrayList<>();
                    jParser.nextToken();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        posts.forEach(it -> {
            System.out.println(it);
            System.out.println(it.getComments().size());
            it.getComments().forEach(comment -> {
                System.out.println("\t" + comment);
                System.out.println("thread size: " + comment.getThreadComments().size());
                comment.getThreadComments().forEach(
                        threadComment -> System.out.println("\t\t" + threadComment)
                );
            });
        });
    }

    private Optional<Image> getImage(JsonParser jParser) throws IOException {
        return Optional.empty();
    }

    private List<SimpleComment> getComments(JsonParser jParser) throws IOException {
        List<SimpleComment> comments = new ArrayList<>();
        List<ThreadComment> threadComments;
        SimpleComment.SimpleCommentBuilder<?, ?> commentBuilder = SimpleComment.builder();

        jParser.nextToken();
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "comms".equals(jParser.currentName()))) {

            if (jParser.currentName() != null) {
                switch (jParser.currentName()) {
                    case "id":
                        jParser.nextToken();
                        commentBuilder.id(jParser.getIntValue());
                        break;
                    case "from_id":
                        jParser.nextToken();
                        commentBuilder.userId(jParser.getIntValue());
                        break;
                    case "text":
                        jParser.nextToken();
                        commentBuilder.text(jParser.getText());
                        break;
                    case "attachments":
                        jParser.nextToken();
                        jParser.skipChildren();
                        jParser.nextToken();
                        break;
                    case "post_id":
                        jParser.nextToken();
                        commentBuilder.postId(jParser.getIntValue());
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
                SimpleComment comment = commentBuilder.build();
                if (!comment.isEmpty()) {
                    comments.add(comment);
                }
                commentBuilder = SimpleComment.builder();
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
        while(!(jParser.currentToken() == JsonToken.END_ARRAY
                && "items".equals(jParser.currentName()))) {
            if (jParser.currentName() != null) {
                switch (jParser.currentName()) {
                    case "id":
                        jParser.nextToken();
                        commentBuilder.id(jParser.getIntValue());
                        break;
                    case "text":
                        jParser.nextToken();
                        commentBuilder.text(jParser.getText());
                        break;
                    case "from_id":
                        jParser.nextToken();
                        commentBuilder.userId(jParser.getIntValue());
                        break;
                    case "post_id":
                        jParser.nextToken();
                        commentBuilder.postId(jParser.getIntValue());
                        break;
                    case "parents_stack":
                        jParser.nextToken();
                        jParser.nextToken();
                        commentBuilder.threadStarterId(jParser.getIntValue());
                        jParser.nextToken();
                        break;
                    case "reply_to_user":
                        if (jParser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
                            jParser.nextToken();
                        }
                        commentBuilder.userOfReply(jParser.getIntValue());
                        break;
                    case "reply_to_comment":
                        jParser.nextToken();
                        commentBuilder.commentOfReply(jParser.getIntValue());
                        break;
                    case "attachments":
                        jParser.nextToken();
                        jParser.skipChildren();
                        jParser.nextToken();
                        break;
                }
            }
            if (jParser.getCurrentToken() == JsonToken.END_OBJECT
                    && jParser.currentName() == null) {
                ThreadComment comment = commentBuilder.build();
                if (!comment.isEmpty()) {
                    threadComments.add(comment);
                }
                commentBuilder = ThreadComment.builder();
            }
            jParser.nextToken();
        }
        skipUntilEndOfThread(jParser);

        return threadComments;
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
}
