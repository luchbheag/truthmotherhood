package org.example.repository;

import org.example.entity.SimpleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleCommentRepository extends JpaRepository<SimpleComment, Long> {
}
