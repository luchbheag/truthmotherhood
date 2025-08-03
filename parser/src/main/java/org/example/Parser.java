package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static final Long idOfGroup = 86832084L;
    Long currentCommentId;
    Long currentUserId;
    Map<Long, Long> mapOldToNewCommentIds;
    Map<Long, Long> mapOldToNewUserIds;
    Map<Long, String> mapNewUserIdToName;
    List<String> listOfNames;
    int curNameIndex = 0;

    final Random random;

    public Parser() {
        mapOldToNewCommentIds = new HashMap<>();
        mapOldToNewUserIds = new HashMap<>();
        mapNewUserIdToName = new HashMap<>();
        mapNewUserIdToName.put(idOfGroup, "Правда о беременности, родах и материнстве");

        this.random = new Random();
        this.currentUserId = 35000L;
        this.currentCommentId = 1L;

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("names.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Файл names.json не найден в ресурсах");
            }
            listOfNames = objectMapper.readValue(
                    inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла names.json", e);
        }
        Collections.shuffle(listOfNames);
    }

    String formatTextWithReply(String text) {
        Pattern patternForUser = Pattern.compile("\\[(id\\d+)(?::[^\\|\\]]+)?\\|");
        Matcher matcherForUser = patternForUser.matcher(text);

        Pattern patternForGroup = Pattern.compile("\\[(club\\d+)(?::[^\\|\\]]+)?\\|");
        Matcher matcherForGroup = patternForGroup.matcher(text);

        long newUserId = 0L;

        if (matcherForUser.find()) {
            String numberStr = matcherForUser.group(1).replaceAll("\\D+", "");
            Long oldUserId = Math.abs(Long.parseLong(numberStr));
            if (!oldUserId.equals(idOfGroup)) {
                newUserId = getNewUserIdByOldId(oldUserId);
            } else {
                newUserId = oldUserId;
            }
            //System.out.println("User");
            //System.out.println(newUserId);
            //System.out.println(mapNewUserIdToName);
            text = text.replaceFirst("\\[id(\\d+)(?::[^\\|\\]]+)?\\|[^\\]]*\\]", mapNewUserIdToName.get(newUserId));
        } else if (matcherForGroup.find()) {
            //System.out.println("Group");
            String numberStr = matcherForGroup.group(1).replaceAll("\\D+", "");
            Long oldGroupId = Math.abs(Long.parseLong(numberStr));
            if (!oldGroupId.equals(idOfGroup)) {
                newUserId = getNewUserIdByOldId(oldGroupId);
            } else {
                newUserId = oldGroupId;
            }
            //System.out.println(newUserId);
            //System.out.println(mapNewUserIdToName);
            text = text.replaceFirst("\\[club(\\d+)(?::[^\\|\\]]+)?\\|[^\\]]*\\]", mapNewUserIdToName.get(newUserId));
        }
        return text;
    }

    void resetMaps() {
        mapOldToNewCommentIds = new HashMap<>();
        mapOldToNewUserIds = new HashMap<>();
        mapNewUserIdToName = new HashMap<>();
        mapNewUserIdToName.put(idOfGroup, "Правда о беременности, родах и материнстве");
        mapNewUserIdToName.put(0L, "Неизвестная пользовательница");
        Collections.shuffle(listOfNames);
        curNameIndex = 0;
        currentUserId = 40000L + random.nextInt(1000);
    }

    long getNewUserIdByOldId(Long oldUserId) {
        if (oldUserId.equals(idOfGroup) || oldUserId.equals(0L)) {
            return oldUserId;
        }
        if (!this.mapOldToNewUserIds.containsKey(oldUserId)) {
            long idToReturn = this.currentUserId;
            this.mapOldToNewUserIds.put(oldUserId, idToReturn);
            this.currentUserId += 1 + random.nextInt(20);
            this.mapNewUserIdToName.put(idToReturn, listOfNames.get(curNameIndex++));
        }
        return this.mapOldToNewUserIds.get(oldUserId);
    }

    long getNewCommentIdByOldId(Long oldId) {
        if (!this.mapOldToNewCommentIds.containsKey(oldId)) {
            this.mapOldToNewCommentIds.put(oldId, this.currentCommentId++);
        }
        return this.mapOldToNewCommentIds.get(oldId);
    }
}
