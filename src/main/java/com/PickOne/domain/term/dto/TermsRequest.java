package com.PickOne.domain.term.dto;

import com.PickOne.domain.term.model.domain.*;
import com.PickOne.term.model.domain.*;

public record TermsRequest(
        Title title,
        Content content,
        TermsType type,
        Version version,
        Required required,
        EffectiveDate effectiveDate
) {}