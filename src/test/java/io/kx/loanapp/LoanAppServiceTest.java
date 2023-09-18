package io.kx.loanapp;

import io.kx.loanapp.api.LoanAppApi;
import io.kx.loanapp.api.LoanAppService;
import io.kx.loanapp.domain.LoanAppDomainEvent;
import io.kx.loanapp.domain.LoanAppDomainState;
import io.kx.loanapp.domain.LoanAppDomainStatus;
import kalix.javasdk.testkit.EventSourcedResult;
import kalix.javasdk.testkit.EventSourcedTestKit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class LoanAppServiceTest {

    @Test
    public void happyPath(){

        var loanAppId = UUID.randomUUID().toString();
        EventSourcedTestKit<LoanAppDomainState, LoanAppDomainEvent, LoanAppService> testKit = EventSourcedTestKit.of(loanAppId, LoanAppService::new);

        // submit a request
        var submitRequest = new LoanAppApi.SubmitRequest(
                "clientId",
                5000,
                2000,
                36);
        EventSourcedResult<LoanAppApi.EmptyResponse> submitResult = testKit.call(service -> service.submit(submitRequest));

        // capture the generated event
        LoanAppDomainEvent.Submitted submittedEvent = submitResult.getNextEventOfType(LoanAppDomainEvent.Submitted.class);
        Assertions.assertEquals(loanAppId, submittedEvent.getLoanAppId());

        // capture the updated domain state
        LoanAppDomainState updatedStat = (LoanAppDomainState)submitResult.getUpdatedState();
        Assertions.assertEquals(LoanAppDomainStatus.STATUS_IN_REVIEW, updatedStat.getStatus());

        // approve a request
        EventSourcedResult<LoanAppApi.EmptyResponse> approveResponse = testKit.call(service -> service.approve());
        // capture approved event
        LoanAppDomainEvent.Approved approvedEvent = approveResponse.getNextEventOfType(LoanAppDomainEvent.Approved.class);
        Assertions.assertEquals(loanAppId, approvedEvent.getLoanAppId());

        // capture the updated state after approve
        updatedStat = (LoanAppDomainState)approveResponse.getUpdatedState();
        Assertions.assertEquals(LoanAppDomainStatus.STATUS_APPROVED, updatedStat.getStatus());


    }
}