package se331.olympicapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se331.olympicapp.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCountryCode(String countryCode);
}
