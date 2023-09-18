package io.kx.loanapp.domain;

import kalix.javasdk.annotations.TypeName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


public sealed interface LoanAppDomainEvent {

    @TypeName("loan-submitted")
    @Builder
    @Getter
    @Setter
    final class Submitted implements LoanAppDomainEvent {
        String loanAppId;
        String clientId;
        Integer clientMonthlyIncomeCents;
        Integer loanAmountCents;
        Integer loanDurationMonths;
        Instant timestamp;
    }

    @TypeName("loan-approved")
    @Builder
    @Getter
    @Setter
    final class Approved implements LoanAppDomainEvent {
        String loanAppId;
        Instant timestamp;
    }

    @TypeName("loan-declined")
    @Builder
    @Getter
    @Setter
    final class Declined implements LoanAppDomainEvent {
        String loanAppId;
        String reason;
        Instant timestamp;
    }
}
