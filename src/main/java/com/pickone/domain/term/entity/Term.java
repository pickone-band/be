package com.pickone.domain.term.entity;

import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"title", "version"})
})
public class Term extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  public static Term create(String title, String content, String version, boolean required, LocalDateTime effectiveDate) {
    return new Term(null, title, content, version, required, effectiveDate);
  }

  public Term withUpdate(String title, String content, boolean required, LocalDateTime effectiveDate) {
    return new Term(this.id, title, content, this.version, required, effectiveDate);
  }
}
