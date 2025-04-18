package com.PickOne.user.service;

import com.PickOne.user.exception.EmailAlreadyExistsException;
import com.PickOne.user.exception.PasswordMismatchException;
import com.PickOne.user.exception.UserNotFoundException;
import com.PickOne.user.model.domain.user.Email;
import com.PickOne.user.model.domain.user.Password;
import com.PickOne.user.model.domain.user.User;
import com.PickOne.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(String emailValue, String passwordValue) {
        Email email = Email.of(emailValue);

        userRepository.findByEmail(emailValue)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException(emailValue);
                });

        Password rawPassword = Password.of(passwordValue);
        String encodedPassword = passwordEncoder.encode(rawPassword.getValue());
        Password password = Password.ofEncoded(encodedPassword);

        User user = User.create(email, password);
        // 저장 후 ID 포함된 도메인 객체 반환
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User changePassword(Long id, String currentPassword, String newPassword) {
        User user = findById(id);

        String currentPasswordValue = user.getPasswordValue();

        if (currentPasswordValue == null || !passwordEncoder.matches(currentPassword, currentPasswordValue)) {
            throw new PasswordMismatchException();
        }

        Password newPasswordObj = Password.of(newPassword);
        String encodedNewPassword = passwordEncoder.encode(newPasswordObj.getValue());

        user.changePassword(
                user.getPassword(),
                Password.ofEncoded(encodedNewPassword)
        );

        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}