package org.example.service;

import org.example.dao.CommentDao;
import org.example.entity.Comment;
import org.example.entity.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentDao commentDao;
    private final ImageService imageService;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao,
                              ImageService imageService) {
        this.commentDao = commentDao;
        this.imageService = imageService;
    }

    @Override
    public void saveAll(List<Comment> comments) {
        commentDao.saveAll(comments);
        for (Comment comment : comments) {
            imageService.saveAll(comment.getImages());
        }
    }

    @Override
    public List<Comment> findAllByPostId(Long wallPostId) {
        List<Comment> comments = commentDao.findAllByWallPostId(wallPostId);
        List<Long> commentIds = comments.stream().map(Comment::getCommentId).toList();
        Map<Long, List<Image>> commentImagesMap = getImagesForComments(commentIds);

        for (Comment comment : comments) {
            comment.setImages(commentImagesMap.getOrDefault(comment.getCommentId(), new ArrayList<>()));
        }

        return comments;
    }

    @Override
    public int countAllCommentsInTable() {
        return commentDao.countAllCommentsInTable();
    }

    private Map<Long, List<Image>> getImagesForComments(List<Long> commentsIds) {
        System.out.println(imageService.findAllByMultipleCommentIds(commentsIds));

        return imageService.findAllByMultipleCommentIds(commentsIds)
                .stream()
                .collect(Collectors.groupingBy(Image::getCommentId));
    }
}
