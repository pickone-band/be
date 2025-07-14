package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserIdResponse;
import com.pickone.domain.user.dto.UserResponse;
import java.util.List;

public interface UserQueryService {
  UserResponse getUser(Long userId);
  List<UserIdResponse> getUserIdsByNicknames(List<String> nicknames);
}
