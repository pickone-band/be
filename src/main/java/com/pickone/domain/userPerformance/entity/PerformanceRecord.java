package com.pickone.domain.userPerformance.entity;

import com.pickone.domain.user.entity.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PerformanceRecord {
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String title;
private String description;
private LocalDate date;
private String teamName;
private String thumbnailUrl;
private String videoUrl;
@ElementCollection
@CollectionTable(name = "performance_songs", joinColumns = @JoinColumn(name = "performance_record_id"))
private List<Song> songs = new ArrayList<>();

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "user_id", nullable = false)
private User user;

}
