package io.kx.loanproc.domain;

import kalix.javasdk.annotations.TypeName;
import java.time.Instant;


public sealed interface LoanProcDomainEvent {
    @TypeName("ready-for-review")
    record ReadyForReview(String loanAppId,
                          Instant timestamp) implements LoanProcDomainEvent {}

    @TypeName("approved")
    record Approved(String loanAppId, String reviewerId, Instant timestamp) implements LoanProcDomainEvent {}

    @TypeName("declined")
    record Declined(String loanAppId, String reviewerId, String reason, Instant timestamp) implements LoanProcDomainEvent {}
}
