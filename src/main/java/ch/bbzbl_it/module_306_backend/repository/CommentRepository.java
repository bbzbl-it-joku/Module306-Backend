package ch.bbzbl_it.module_306_backend.repository;

import ch.bbzbl_it.module_306_backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTicket_Id(Long ticketId);
}
