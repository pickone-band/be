package com.PickOne.domain.term.mapper;

import com.PickOne.domain.term.dto.TermResponseDto;
import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.model.entity.TermEntity;

public class TermMapper {

    public static Term toDomain(TermEntity entity) {
        return new Term(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getVersion(),
                entity.isRequired(),
                entity.getEffectiveDate()
        );
    }

    public static TermEntity toEntity(Term term) {
        return new TermEntity(
                term.getId(),
                term.getTitle(),
                term.getContent(),
                term.getVersion(),
                term.isRequired(),
                term.getEffectiveDate()
        );
    }

    public static TermResponseDto toResponse(Term term) {
        return TermResponseDto.from(term);
    }
}