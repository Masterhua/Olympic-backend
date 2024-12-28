package se331.olympicapp.controller;

import se331.olympicapp.entity.UserEntity;
import se331.olympicapp.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserEntity userEntity) {
        if (userRepository.findByUsername(userEntity.getUsername()) != null) {
            logger.warn("Registration failed: Username {} already exists", userEntity.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("status", 409, "message", "Username already exists")
            );
        }
        userEntity.setRole("USER");
        userRepository.save(userEntity);
        logger.info("New user registered: {}", userEntity.getUsername());
        return ResponseEntity.ok(Map.of("status", 200, "message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData, HttpSession session) {
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
        session.setAttribute("userId", user.getId()); // 添加用户ID到会话
        logger.info("User logged in successfully: {}", username);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Login successful",
                "role", user.getRole(),
                "userId", user.getId()
        ));
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

        logger.info("Profile retrieved for user: {}", username);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Profile retrieved successfully",
                "data", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "nickname", user.getNickname(),
                        "role", user.getRole()
                )
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        logger.info("User logged out successfully");
        return ResponseEntity.ok(Map.of("status", 200, "message", "Logout successful"));
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

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return "ADMIN".equals(role);
    }
}