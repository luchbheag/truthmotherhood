package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.entity.Topic;
import org.example.entity.WallPost;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonWriter {
    public static void writeWallPostsToJsonFile(List<WallPost> posts, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            mapper.writeValue(new File(filename), posts);
            System.out.println("JSON записан в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTopicsToJsonFile(List<Topic> topics, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            mapper.writeValue(new File(filename), topics);
            System.out.println("JSON записан в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLinksToJsonFile(List<String> strs, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            mapper.writeValue(new File(filename), strs);
            System.out.println("JSON записан в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeIdsToJsonFile(List<Long> ids, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            mapper.writeValue(new File(filename), ids);
            System.out.println("JSON записан в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
