package com.pickone.domain.term.model.entity;

import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"title", "version"})
})
public class TermEntity extends BaseEntity {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private String version;

  @Column(nullable = false)
  private boolean isRequired;

  @Column(nullable = false)
  private LocalDateTime effectiveDate;

  public static TermEntity create(String title, String content, String version, boolean required, LocalDateTime effectiveDate) {
    return new TermEntity(null, title, content, version, required, effectiveDate);
  }
}
