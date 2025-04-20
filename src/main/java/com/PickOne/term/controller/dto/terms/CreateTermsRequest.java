package com.PickOne.term.controller.dto.terms;

import com.PickOne.term.model.domain.*;

public record CreateTermsRequest(
        Title title,
        Content content,
        TermsType type,
        Version version,
        EffectiveDate effectiveDate,
        Required required
) {}
