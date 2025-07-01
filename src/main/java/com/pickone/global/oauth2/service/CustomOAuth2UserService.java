package com.pickone.global.oauth2.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.oauth2.model.factory.OAuth2UserInfoFactory;
import com.pickone.global.security.model.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserCommandService userCommandService;
    private final UserJpaRepository userRepository;

    protected OAuth2User loadOAuth2User(OAuth2UserRequest request) {
        return super.loadUser(request);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = loadOAuth2User(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(registrationId, oAuth2User.getAttributes());

        UserEntity user = userRepository.findByProfileEmail(userInfo.getEmail())
            .orElseGet(() -> userCommandService.signupWithOAuth2(userInfo));
        return UserPrincipal.from(user);
    }
}
