package com.pickone.global.email.service;

import com.pickone.global.email.dto.EmailSendHistoryResponse;
import java.util.List;

public interface EmailQueryService {
  List<EmailSendHistoryResponse> getAllHistory();
}
