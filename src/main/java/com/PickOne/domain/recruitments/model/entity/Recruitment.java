package com.PickOne.domain.recruitments.model.entity;

import com.PickOne.common.entity.BaseEntity;
import com.PickOne.domain.recruitments.model.Genre;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Status;
import com.PickOne.domain.recruitments.model.Type;
import com.PickOne.domain.recruitments.model.Visibility;
import com.PickOne.user.model.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@AllArgsConstructor()
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recruitment_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private String title;
    private String description;
    private String region;
    private String thumbnail;
    private String snsLink;
}
