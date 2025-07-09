package com.pickone.global.oauth2.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.oauth2.model.factory.OAuth2UserInfoFactory;
import com.pickone.global.security.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserCommandService userCommandService;
  private final UserJpaRepository userRepository;
  private final UserConnectionService userConnectionService;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Provider provider = OAuth2Provider.from(registrationId);
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.create(registrationId, oAuth2User.getAttributes());

    String email = userInfo.getEmail();
    String providerUserId = userInfo.getId(); // ✅ 정확한 provider 사용자 ID
    String nickname = userInfo.getNickname();

    // 이메일 기반 사용자 조회/생성
    User user = userRepository.findByProfileEmail(email)
        .orElseGet(() -> userCommandService.signupWithOAuth2(userInfo));

    // 소셜 연결 정보 저장 or 업데이트
    userConnectionService.findOrCreateConnection(provider, providerUserId, email, nickname, user.getId());

    return UserPrincipal.from(user);
  }
}
