package io.kx.loanapp.api;

import io.grpc.Status;
import io.kx.loanapp.domain.LoanAppDomainEvent;
import io.kx.loanapp.domain.LoanAppDomainState;
import io.kx.loanapp.domain.LoanAppDomainStatus;
import kalix.javasdk.annotations.EventHandler;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.eventsourcedentity.EventSourcedEntity;
import kalix.javasdk.eventsourcedentity.EventSourcedEntityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Id("loanAppId")
@TypeId("loanapp")
@RequestMapping("/loanapp/{loanAppId}")
public class LoanAppService extends EventSourcedEntity<LoanAppDomainState, LoanAppDomainEvent> {

    private static Logger logger = LoggerFactory.getLogger(LoanAppService.class);

    private final String loanAppId;
    public LoanAppService(EventSourcedEntityContext context) {
        this.loanAppId = context.entityId();
    }

    @Override
    public LoanAppDomainState emptyState() {
        return LoanAppDomainState.builder()
                .loanAppId(loanAppId)
                .status(LoanAppDomainStatus.STATUS_UNKNOWN)
                .build();
    }


    @PostMapping("/submit")
    public Effect<LoanAppApi.EmptyResponse> submit(@RequestBody LoanAppApi.SubmitRequest request){
        switch (currentState().getStatus()) {
            case STATUS_UNKNOWN -> {
                LoanAppDomainEvent.Submitted event =
                         LoanAppDomainEvent.Submitted.builder()
                                 .loanAppId(loanAppId)
                                 .clientId(request.clientId())
                                 .clientMonthlyIncomeCents(request.clientMonthlyIncomeCents())
                                 .loanAmountCents(request.loanAmountCents())
                                 .loanDurationMonths(request.loanDurationMonths())
                                 .timestamp(Instant.now())
                                 .build();
                return effects().emitEvent(event).thenReply(newState -> LoanAppApi.EmptyResponse.of());
            }
            case STATUS_IN_REVIEW -> {
                return effects().reply(LoanAppApi.EmptyResponse.of());
            }
            default -> {
                return effects().error("Wrong status", Status.Code.INVALID_ARGUMENT);
            }
        }
    }

    @PutMapping("/approve")
    public Effect<LoanAppApi.EmptyResponse> approve(){
        switch (currentState().getStatus()) {
            case STATUS_UNKNOWN -> {
                return effects().error("Not found", Status.Code.NOT_FOUND);
            }
            case STATUS_IN_REVIEW -> {
                LoanAppDomainEvent.Approved event = LoanAppDomainEvent.Approved.builder()
                        .loanAppId(loanAppId)
                        .timestamp(Instant.now())
                        .build();
                return effects().emitEvent(event).thenReply(newState -> LoanAppApi.EmptyResponse.of());
            }
            case STATUS_APPROVED -> {
                return effects().reply(LoanAppApi.EmptyResponse.of());
            }
            default -> {
                return effects().error("Wrong status", Status.Code.INVALID_ARGUMENT);
            }
        }


    }

    @PostMapping("/decline")
    public Effect<LoanAppApi.EmptyResponse> decline(@RequestBody LoanAppApi.DeclineRequest request){
        switch (currentState().getStatus()) {
            case STATUS_UNKNOWN -> {
                return effects().error("Not found", Status.Code.NOT_FOUND);
            }
            case STATUS_IN_REVIEW -> {
                LoanAppDomainEvent.Declined event = LoanAppDomainEvent.Declined.builder()
                        .loanAppId(loanAppId)
                        .reason(request.reason())
                        .timestamp(Instant.now())
                        .build();
                return effects().emitEvent(event).thenReply(newState -> LoanAppApi.EmptyResponse.of());
            }
            case STATUS_DECLINED -> {
                return effects().reply(LoanAppApi.EmptyResponse.of());
            }
            default -> {
                return effects().error("Wrong status", Status.Code.INVALID_ARGUMENT);
            }
        }
    }

    @GetMapping
    public Effect<LoanAppApi.ApiResponse> get(){

        return effects().reply(
                new LoanAppApi.ApiResponse(
                        currentState().getLoanAppId(),
                        currentState().getClientId(),
                        currentState().getClientMonthlyIncomeCents(),
                        currentState().getLoanAmountCents(),
                        currentState().getLoanDurationMonths(),
                        currentState().getStatus(),
                        currentState().getDeclineReason(),
                        currentState().getLastUpdatedTimestamp())

        );
    }

    @EventHandler
    public LoanAppDomainState onSubmitted(LoanAppDomainEvent.Submitted event){
        return currentState()
                .withStatus(LoanAppDomainStatus.STATUS_IN_REVIEW)
                .withClientId(event.getClientId())
                .withClientMonthlyIncomeCents(event.getClientMonthlyIncomeCents())
                .withLoanAmountCents(event.getLoanAmountCents())
                .withLoanDurationMonths(event.getLoanDurationMonths())
                .withLastUpdatedTimestamp(event.getTimestamp());
    }
    @EventHandler
    public LoanAppDomainState onApproved(LoanAppDomainEvent.Approved event){
        return currentState()
                .withStatus(LoanAppDomainStatus.STATUS_APPROVED)
                .withLastUpdatedTimestamp(event.getTimestamp());
    }
    @EventHandler
    public LoanAppDomainState onDeclined(LoanAppDomainEvent.Declined event){
        return currentState()
                .withDeclineReason(event.getReason())
                .withStatus(LoanAppDomainStatus.STATUS_DECLINED)
                .withLastUpdatedTimestamp(event.getTimestamp());
    }
}
