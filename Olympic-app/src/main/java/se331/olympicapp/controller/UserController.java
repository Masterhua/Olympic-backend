package se331.olympicapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import se331.olympicapp.entity.UserEntity;
import se331.olympicapp.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final String DEFAULT_AVATAR_URL = "/uploads/avatars/default-avatar.png";

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody Map<String, String> loginData,
            HttpSession session
    ) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        UserEntity user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            logger.warn("Login failed for username: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("status", 401, "message", "Invalid username or password")
            );
        }

        session.setAttribute("username", username);
        session.setAttribute("role", user.getRole());
        session.setAttribute("userId", user.getId());
        logger.info("User logged in successfully: {}", username);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Login successful",
                "role", user.getRole(),
                "userId", user.getId()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        logger.info("User logged out successfully");
        return ResponseEntity.ok(Map.of("status", 200, "message", "Logout successful"));
    }

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");

        if (userRepository.findByUsername(username) != null) {
            logger.warn("Registration failed: Username {} already exists", username);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("status", 409, "message", "Username already exists")
            );
        }

        String avatarUrl = DEFAULT_AVATAR_URL; // 默认头像路径

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.setRole("USER");
        userEntity.setAvatarUrl(avatarUrl);
        userRepository.save(userEntity);
        logger.info("New user registered: {}", username);
        return ResponseEntity.ok(Map.of("status", 200, "message", "User registered successfully"));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            logger.warn("Profile request failed: User not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("status", 401, "message", "User not logged in")
            );
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Profile request failed: User {} not found", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", 404, "message", "User not found")
            );
        }

        String avatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : DEFAULT_AVATAR_URL;

        logger.info("Profile retrieved for user: {}", username);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Profile retrieved successfully",
                "data", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "role", user.getRole(),
                        "avatar", avatarUrl
                )
        ));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        if (!isAdmin(session)) {
            logger.warn("Access denied: User is not an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("status", 403, "message", "Access denied")
            );
        }

        List<UserEntity> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates, HttpSession session) {
        if (!isAdmin(session)) {
            logger.warn("Access denied: User is not an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("status", 403, "message", "Access denied")
            );
        }

        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) {
            logger.warn("Update failed: User with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", 404, "message", "User not found")
            );
        }

        String nickname = updates.get("nickname");
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", 400, "message", "Nickname cannot be empty")
            );
        }

        user.setNickname(nickname);
        userRepository.save(user);
        logger.info("User with ID {} updated successfully", id);
        return ResponseEntity.ok(Map.of("status", 200, "message", "User updated successfully"));
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            logger.warn("Access denied: User is not an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("status", 403, "message", "Access denied")
            );
        }

        if (id.equals(session.getAttribute("userId"))) {
            logger.warn("Admin tried to delete their own account");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("status", 403, "message", "Cannot delete your own account")
            );
        }

        userRepository.deleteById(id);
        logger.info("User with ID {} deleted successfully", id);
        return ResponseEntity.ok(Map.of("status", 200, "message", "User deleted successfully"));
    }

    @PutMapping("/admin/users/{id}/promote")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            logger.warn("Access denied: User is not an admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("status", 403, "message", "Access denied")
            );
        }

        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) {
            logger.warn("Promote failed: User with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("status", 404, "message", "User not found")
            );
        }

        if ("ADMIN".equals(user.getRole())) {
            logger.info("User with ID {} is already an admin", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", 400, "message", "User is already an admin")
            );
        }

        user.setRole("ADMIN");
        userRepository.save(user);
        logger.info("User with ID {} promoted to admin successfully", id);
        return ResponseEntity.ok(Map.of("status", 200, "message", "User promoted to admin successfully"));
    }

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }
}