package com.PickOne.user.controller;

import com.PickOne.user.controller.dto.UserRegistrationRequest;
import com.PickOne.user.controller.dto.PasswordChangeRequest;
import com.PickOne.user.controller.dto.UserResponse;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = userService.register(request.email(), request.password());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(user.getId(), user.getEmailValue()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmailValue()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmailValue()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = userService.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getEmailValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<UserResponse> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequest request) {
        User user = userService.changePassword(id, request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmailValue()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}