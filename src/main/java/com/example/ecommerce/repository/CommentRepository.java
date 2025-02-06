package com.example.ecommerce.repository;

import com.example.ecommerce.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByProductId(Long product_id);
}
