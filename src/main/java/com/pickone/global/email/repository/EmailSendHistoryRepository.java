package com.pickone.global.email.repository;

import com.pickone.global.email.entity.EmailSendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailSendHistoryRepository extends JpaRepository<EmailSendHistory, Long> {
  List<EmailSendHistory> findByToEmail(String toEmail);
  List<EmailSendHistory> findBySuccess(boolean success);
}
