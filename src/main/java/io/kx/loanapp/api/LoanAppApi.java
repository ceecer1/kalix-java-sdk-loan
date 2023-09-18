package io.kx.loanapp.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.kx.loanapp.domain.LoanAppDomainState;
import io.kx.loanapp.domain.LoanAppDomainStatus;
import lombok.*;

import java.time.Instant;

public sealed interface LoanAppApi {
    record SubmitRequest(String clientId,
                         Integer clientMonthlyIncomeCents,
                         Integer loanAmountCents,
                         Integer loanDurationMonths) implements LoanAppApi {
    }

    record DeclineRequest(String reason) implements LoanAppApi {
    }

    record EmptyResponse() implements LoanAppApi {
        public static EmptyResponse of() {
            return new EmptyResponse();
        }
    }

    record ApiResponse(
            String loanAppId,
            String clientId,
            Integer clientMonthlyIncomeCents,
            Integer loanAmountCents,
            Integer loanDurationMonths,
            LoanAppDomainStatus status,
            String declineReason,
            Instant lastUpdatedTimestamp
    ) implements LoanAppApi {}

//    @Builder
//    @Data
//    final class ApiResponse implements LoanAppApi {
//
//        String loanAppId;
//        String clientId;
//        Integer clientMonthlyIncomeCents;
//        Integer loanAmountCents;
//        Integer loanDurationMonths;
//        LoanAppDomainStatus status;
//        String declineReason;
//        Instant lastUpdatedTimestamp;
//
//        @JsonCreator
//        public ApiResponse(
//                @JsonProperty("loanAppId") String loanAppId,
//                @JsonProperty("clientId") String clientId,
//                @JsonProperty("clientMonthlyIncomeCents") Integer clientMonthlyIncomeCents,
//                @JsonProperty("loanAmountCents") Integer loanAmountCents,
//                @JsonProperty("loanDurationMonths") Integer loanDurationMonths,
//                @JsonProperty("status") LoanAppDomainStatus status,
//                @JsonProperty("declineReason") String declineReason,
//                @JsonProperty("lastUpdatedTimestamp") Instant lastUpdatedTimestamp
//        ) {
//            this.loanAppId = loanAppId;
//            this.clientId = clientId;
//            this.clientMonthlyIncomeCents = clientMonthlyIncomeCents;
//            this.loanAmountCents = loanAmountCents;
//            this.loanDurationMonths = loanDurationMonths;
//            this.status = status;
//            this.declineReason = declineReason;
//            this.lastUpdatedTimestamp = lastUpdatedTimestamp;
//        }
//    }
}
