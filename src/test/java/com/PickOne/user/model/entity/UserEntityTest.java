package com.PickOne.user.model.entity;

import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    @DisplayName("User 도메인 객체로부터 UserEntity 생성")
    void createUserEntityFromUser() {
        // given
        Email email = Email.of("test@example.com");
        Password password = Password.of("ValidPass1!");
        User user = User.create(email, password);

        // when
        UserEntity entity = UserEntity.from(user);

        // then
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("ValidPass1!", entity.getPassword());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("UserEntity에서 User 도메인 객체로 변환")
    void convertUserEntityToDomain() {
        // given
        Email email = Email.of("test@example.com");
        Password password = Password.of("ValidPass1!");
        User user = User.create(email, password);
        UserEntity entity = UserEntity.from(user);

        // when
        User convertedUser = entity.toDomain();

        // then
        // ID는 null이므로 테스트하지 않음
        assertEquals(email.getValue(), convertedUser.getEmailValue());
        assertEquals(password.getValue(), convertedUser.getPasswordValue());
    }

    @Test
    @DisplayName("비밀번호 변경된 새 UserEntity 생성")
    void createNewEntityWithChangedPassword() {
        // given
        Email email = Email.of("test@example.com");
        Password password = Password.of("ValidPass1!");
        User user = User.create(email, password);
        UserEntity entity = UserEntity.from(user);
        String newPassword = "NewPass1!";

        // when
        UserEntity newEntity = entity.withPassword(newPassword);

        // then
        assertEquals(entity.getEmail(), newEntity.getEmail());
        assertEquals(newPassword, newEntity.getPassword());
        assertNotSame(entity, newEntity);
    }

//    @Test
//    @DisplayName("UserEntity는 BaseEntity를 상속받아 상태 관리 기능을 가짐")
//    void userEntityInheritsBaseEntity() {
//        // given
//        Email email = Email.of("test@example.com");
//        Password password = Password.of("ValidPass1!");
//        User user = User.create(email, password);
//        UserEntity entity = UserEntity.from(user);
//
//        // when & then
//        // 초기 상태는 ACTIVE
//        assertTrue(entity.isActive());
//        assertFalse(entity.isInactive());
//        assertFalse(entity.isDeleted());
//
//        // 상태 변경
//        entity.inactivate();
//        assertFalse(entity.isActive());
//        assertTrue(entity.isInactive());
//
//        entity.markDeleted();
//        assertTrue(entity.isDeleted());
//    }
}