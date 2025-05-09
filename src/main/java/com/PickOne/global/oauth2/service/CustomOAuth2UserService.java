package com.PickOne.global.oauth2.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserRepository;

import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.OAuth2UserInfo;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;
import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.PermissionEntity;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.SecurityUser;

import com.PickOne.global.security.service.PasswordEncoder;
import com.PickOne.global.security.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            // 기존 OAuth2 사용자 정보 로드
            OAuth2User oAuth2User = super.loadUser(userRequest);

            // 구글 인증만 처리
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            if (!registrationId.equals("google")) {
                throw new OAuth2AuthenticationException("구글 로그인만 지원합니다");
            }

            // 사용자 프로필 정보 속성 이름
            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

            // 사용자 정보 변환
            OAuth2UserInfo oAuth2UserInfo = extractGoogleUserInfo(oAuth2User.getAttributes());

            // 사용자 처리 (등록 또는 조회)
            User user = processOAuth2User(oAuth2UserInfo);

            // SecurityUser 생성 - 도메인 객체 ID로 엔티티 조회
            UserEntity userEntity = userJpaRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + user.getId()));

            // 사용자의 역할 조회 후 도메인 객체에서 엔티티로 변환
            Set<Role> userRoles = userRoleService.getUserRoles(user.getId());
            Set<RoleEntity> roleEntities = userRoles.stream()
                    .map(RoleEntity::from)
                    .collect(Collectors.toSet());

            // 사용자의 모든 권한 조회
            Set<PermissionEntity> permissions = getUserPermissions(roleEntities);

            SecurityUser securityUser = new SecurityUser(userEntity, roleEntities, permissions);

            // OAuth2User 반환
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("securityUser", securityUser);

            return new DefaultOAuth2User(
                    securityUser.getAuthorities(),
                    attributes,
                    userNameAttributeName
            );
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("OAuth2 인증 중 오류 발생: {}", ex.getMessage(), ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * 역할에 해당하는 모든 권한을 가져오는 메소드
     */
    private Set<PermissionEntity> getUserPermissions(Set<RoleEntity> roles) {
        Set<PermissionEntity> permissions = new HashSet<>();

        // 각 역할에 할당된 모든 권한을 수집
        for (RoleEntity role : roles) {
            // RoleEntity에서 활성화된 역할-권한 관계만 필터링
            role.getRolePermissions().stream()
                    .filter(rp -> rp.isActive())
                    .map(rp -> rp.getPermission())
                    .forEach(permissions::add);
        }

        return permissions;
    }

    private OAuth2UserInfo extractGoogleUserInfo(Map<String, Object> attributes) {
        return OAuth2UserInfo.of(
                attributes.get("sub").toString(),
                attributes.get("email").toString(),
                attributes.get("name").toString(),
                OAuth2Provider.GOOGLE,
                attributes
        );
    }

    @Transactional
    public User processOAuth2User(OAuth2UserInfo oAuth2UserInfo) {
        // 기존 연결 조회
        Optional<UserConnectionEntity> existingConnection = userConnectionRepository.findByProviderAndProviderId(
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId()
        );

        if (existingConnection.isPresent()) {
            // 기존 연결이 있는 경우 사용자 정보 업데이트
            UserConnectionEntity connection = existingConnection.get();
            connection.updateConnectionInfo(oAuth2UserInfo.getEmailValue(), oAuth2UserInfo.getName());
            userConnectionRepository.save(connection);

            // UserEntity에서 UserID를 가져와 도메인 객체 조회
            UserEntity userEntity = connection.getUser();
            return userRepository.findById(userEntity.getId())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + userEntity.getId()));
        } else {
            // 이메일로 기존 사용자 조회
            Optional<User> existingUser = userRepository.findByEmail(oAuth2UserInfo.getEmailValue());

            if (existingUser.isPresent()) {
                // 기존 사용자가 있는 경우 연결 생성
                User user = existingUser.get();

                // 도메인 ID로 엔티티 조회하여 연결 생성
                UserEntity userEntity = userJpaRepository.findById(user.getId())
                        .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + user.getId()));

                createUserConnection(userEntity, oAuth2UserInfo);
                return user;
            } else {
                // 신규 사용자 등록
                return registerNewUser(oAuth2UserInfo);
            }
        }
    }

    private void createUserConnection(UserEntity userEntity, OAuth2UserInfo oAuth2UserInfo) {
        UserConnectionEntity connection = UserConnectionEntity.create(
                userEntity,
                oAuth2UserInfo.getProviderId(),
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getEmailValue(),
                oAuth2UserInfo.getName()
        );

        userConnectionRepository.save(connection);
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        // 랜덤 비밀번호 생성 및 암호화
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        // 도메인 객체 생성
        User user = User.create(
                Email.of(oAuth2UserInfo.getEmailValue()),
                Password.ofEncoded(encodedPassword)
        );

        // 도메인 객체 저장
        User savedUser = userRepository.save(user);

        // 기본 USER 역할 부여
        userRoleService.assignRoleToUser(savedUser.getId(), "USER", null, null);

        // 엔티티 버전 조회
        UserEntity userEntity = userJpaRepository.findById(savedUser.getId())
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다: " + savedUser.getId()));

        // 사용자 연결 생성
        createUserConnection(userEntity, oAuth2UserInfo);

        return savedUser;
    }
}