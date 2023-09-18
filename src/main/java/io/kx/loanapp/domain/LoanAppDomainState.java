package io.kx.loanapp.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

import java.time.Instant;

@Builder
@Getter
@Setter
public class LoanAppDomainState {
    String loanAppId;
    @With
    String clientId;
    @With
    Integer clientMonthlyIncomeCents;
    @With
    Integer loanAmountCents;
    @With
    Integer loanDurationMonths;
    @With
    LoanAppDomainStatus status;
    @With
    String declineReason;
    @With
    Instant lastUpdatedTimestamp;
}
