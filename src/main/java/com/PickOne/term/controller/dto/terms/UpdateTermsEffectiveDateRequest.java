package com.PickOne.term.controller.dto.terms;

import com.PickOne.term.model.domain.EffectiveDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UpdateTermsEffectiveDateRequest(EffectiveDate effectiveDate) {}