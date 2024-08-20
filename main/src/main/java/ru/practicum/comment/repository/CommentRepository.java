package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId);

    @Query(" select c from Comment c " +
            "where (upper(c.text) like upper(concat('%', ?1, '%')))")
    Page<Comment> search(String text, Pageable pageable);
}
