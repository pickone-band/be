package com.PickOne.domain.term.dto;

public record ConsentRequest(
        Long termsId,
        boolean consented
) {}