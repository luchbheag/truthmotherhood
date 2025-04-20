package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.entity.WallPost;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonWriter {
    public static void writeToJsonFile(List<WallPost> posts, String filename) {
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
}
