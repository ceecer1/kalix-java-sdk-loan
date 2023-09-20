package io.kx.loanproc.action;

import io.kx.loanapp.api.LoanAppApi;
import io.kx.loanproc.api.LoanProcApi;
import io.kx.loanproc.api.LoanProcService;
import io.kx.loanproc.domain.LoanProcDomainEvent;
import kalix.javasdk.action.Action;
import kalix.javasdk.action.ActionCreationContext;
import kalix.javasdk.annotations.Subscribe;
import kalix.spring.WebClientProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.CompletionStage;

@Subscribe.EventSourcedEntity(value = LoanProcService.class, ignoreUnknown = true)
public class LoanProcToLoanAppEventingAction extends Action {

    private final ActionCreationContext ctx;
    private final WebClient webClient;

    public LoanProcToLoanAppEventingAction(ActionCreationContext ctx, WebClientProvider webClientProvider) {
        this.ctx = ctx;
        this.webClient = webClientProvider.webClientFor("spring-loan");
    }

    public Effect<LoanProcApi.EmptyResponse> onApproved(LoanProcDomainEvent.Approved event){
        var approveResponse = webClient
                .put()
                .uri("/loanapp/" + event.loanAppId() + "/approve")
                .retrieve()
                .bodyToMono(LoanAppApi.EmptyResponse.class)
                .map(__ -> LoanProcApi.EmptyResponse.of())
                .toFuture();

        return effects().asyncReply(approveResponse);
    }

    public Effect<LoanProcApi.EmptyResponse> onDeclined(LoanProcDomainEvent.Declined event){
        var declinedResponse = webClient
                .put()
                .uri("/loanapp/" + event.loanAppId() + "/decline")
                .retrieve()
                .bodyToMono(LoanAppApi.EmptyResponse.class)
                .map(__ -> LoanProcApi.EmptyResponse.of())
                .toFuture();

        return effects().asyncReply(declinedResponse);
    }
}
