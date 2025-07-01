package com.pickone.domain.consent.model.policy;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ConsentPolicy {
  public void validateSaveConsent(UserEntity user, TermEntity term, boolean consented) {
    if (user == null || term == null) {
      throw new IllegalArgumentException("User와 Term은 필수입니다.");
    }
    // 추가 정책 구현 필요시 이곳에
  }
}
