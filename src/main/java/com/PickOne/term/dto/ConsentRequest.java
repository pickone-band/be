package com.PickOne.term.dto;

public record ConsentRequest(
        Long termsId,
        boolean consented
) {}