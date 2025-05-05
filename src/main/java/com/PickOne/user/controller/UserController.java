package com.PickOne.user.controller;

import com.PickOne.user.dto.UserResponse;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(
                new UserResponse(user.getId(), user.getEmailValue())
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(
                new UserResponse(user.getId(), user.getEmailValue())
        );
    }
}