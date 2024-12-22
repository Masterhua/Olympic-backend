package se331.olympicapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se331.olympicapp.entity.Comment;
import se331.olympicapp.entity.UserEntity;
import se331.olympicapp.repository.CommentRepository;
import se331.olympicapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{countryCode}")
    public ResponseEntity<List<Comment>> getCommentsByCountry(@PathVariable String countryCode) {
        List<Comment> comments = commentRepository.findByCountryCode(countryCode);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{countryCode}")
    public ResponseEntity<?> addComment(
            @PathVariable String countryCode,
            @RequestBody Map<String, String> requestBody,
            HttpSession session
    ) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not logged in"));
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Comment content cannot be empty"));
        }

        Comment comment = new Comment();
        comment.setCountryCode(countryCode);
        comment.setNickname(user.getNickname());
        comment.setContent(content.trim());
        comment.setUser(user);

        commentRepository.save(comment);
        return ResponseEntity.ok(Map.of("message", "Comment added successfully"));
    }
}
