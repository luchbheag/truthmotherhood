package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static final Long idOfGroup = 86832084L;
    Long currentCommentId;
    Long currentUserId;
    Map<Long, Long> mapOldToNewCommentIds;
    Map<Long, Long> mapOldToNewUserIds;
    final Random random;

    public Parser() {
        mapOldToNewCommentIds = new HashMap<>();
        mapOldToNewUserIds = new HashMap<>();

        this.random = new Random();
        this.currentUserId = 35000L;
        this.currentCommentId = 1L;
    }

    String formatTextInThreadComment(String text) {
        Pattern patternForUser = Pattern.compile("\\[(id\\d+)(?::[^\\|\\]]+)?\\|");
        Matcher matcherForUser = patternForUser.matcher(text);

        Pattern patternForGroup = Pattern.compile("\\[(club\\d+)(?::[^\\|\\]]+)?\\|");
        Matcher matcherForGroup = patternForGroup.matcher(text);

        long newUserId = 0L;

        if (matcherForUser.find()) {
//            System.out.println("FIND IN mathcerForUser");
            String numberStr = matcherForUser.group(1).replaceAll("\\D+", "");
            Long oldUserId = Math.abs(Long.parseLong(numberStr));
            if (!oldUserId.equals(idOfGroup)) {
                newUserId = getNewUserIdByOldId(oldUserId);
            } else {
                newUserId = oldUserId;
            }
            text = text.replaceFirst("\\[id(\\d+)(?::[^\\|\\]]+)?\\|[^\\]]*\\]", String.format("[id%d]", newUserId));
        } else if (matcherForGroup.find()) {
//            System.out.println("FIND IN mathcerForGroup");
            String numberStr = matcherForGroup.group(1).replaceAll("\\D+", "");
            Long oldGroupId = Math.abs(Long.parseLong(numberStr));
            if (!oldGroupId.equals(idOfGroup)) {
                newUserId = getNewUserIdByOldId(oldGroupId);
            } else {
                newUserId = oldGroupId;
            }
            text = text.replaceFirst("\\[club(\\d+)(?::[^\\|\\]]+)?\\|[^\\]]*\\]", String.format("[id%d]", newUserId));
        }
//        System.out.println(text.substring(0, Math.min(text.length(), 15)));

        return text;
    }

    long getNewUserIdByOldId(Long oldUserId) {
        if (oldUserId.equals(idOfGroup)) {
            return oldUserId;
        }
        if (!this.mapOldToNewUserIds.containsKey(oldUserId)) {
            this.mapOldToNewUserIds.put(oldUserId, this.currentUserId);
            this.currentUserId += random.nextInt(20);
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
