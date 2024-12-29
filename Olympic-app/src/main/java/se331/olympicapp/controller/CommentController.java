package se331.olympicapp.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import se331.olympicapp.dto.CommentDTO;
import se331.olympicapp.entity.Comment;
import se331.olympicapp.entity.UserEntity;
import se331.olympicapp.repository.CommentRepository;
import se331.olympicapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public ResponseEntity<List<CommentDTO>> getCommentsByCountry(@PathVariable String countryCode) {
        List<Comment> comments = commentRepository.findByCountryCode(countryCode);
        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            String avatarUrl = comment.getUser() != null && comment.getUser().getAvatarUrl() != null
                    ? comment.getUser().getAvatarUrl()
                    : "/uploads/avatars/default-avatar.png"; // 默认头像
            return new CommentDTO(
                    comment.getId(),
                    comment.getNickname(),
                    comment.getContent(),
                    avatarUrl,
                    comment.getUser() != null ? comment.getUser().getId() : null,
                    comment.getCountryCode()
            );
        }).collect(Collectors.toList());
        return ResponseEntity.ok(commentDTOs);
    }

    @PostMapping(value = "/{countryCode}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addComment(@PathVariable String countryCode, @RequestBody Map<String, String> requestBody, HttpSession session) {
        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Comment content cannot be empty"));
        }

        String username = (String) session.getAttribute("username");
        UserEntity user = null;
        String nickname = "Guest";

        if (username != null) {
            user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }
            nickname = user.getNickname();
        }

        Comment comment = new Comment();
        comment.setCountryCode(countryCode);
        comment.setNickname(nickname);
        comment.setContent(content.trim());
        comment.setUser(user);

        commentRepository.save(comment);
        return ResponseEntity.ok(Map.of("message", "Comment added successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Comment not found"));
        }
        commentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
}
