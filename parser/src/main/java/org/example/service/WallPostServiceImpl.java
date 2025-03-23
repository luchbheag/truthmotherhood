package org.example.service;

import org.example.dao.WallPostDao;
import org.example.entity.WallPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WallPostServiceImpl implements WallPostService {

    private WallPostDao wallPostDao;

    @Autowired
    public WallPostServiceImpl(WallPostDao wallPostDao) {
        this.wallPostDao = wallPostDao;
    }

    @Override
    public void save(WallPost wallPost) {
        wallPostDao.save(wallPost);
    }

    @Override
    public List<WallPost> findAll() {
        return wallPostDao.findAll();
    }
}
