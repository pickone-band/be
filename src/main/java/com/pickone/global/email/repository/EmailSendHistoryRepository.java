package com.pickone.global.email.repository;

import com.pickone.global.email.model.entity.EmailSendHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailSendHistoryRepository extends JpaRepository<EmailSendHistoryEntity, Long> {
  List<EmailSendHistoryEntity> findByToEmail(String toEmail);
  List<EmailSendHistoryEntity> findBySuccess(boolean success);
}
