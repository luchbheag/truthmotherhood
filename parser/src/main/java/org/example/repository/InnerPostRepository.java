package org.example.repository;

import org.example.entity.InnerPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InnerPostRepository extends JpaRepository<InnerPost, Long> {
}
