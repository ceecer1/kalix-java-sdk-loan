package io.kx.loanapp.action;

import io.kx.loanapp.api.LoanAppApi;
import io.kx.loanapp.api.LoanAppService;
import io.kx.loanapp.domain.LoanAppDomainEvent;
import io.kx.loanproc.api.LoanProcApi;
import kalix.javasdk.Kalix;
import kalix.javasdk.action.Action;
import kalix.javasdk.action.ActionCreationContext;
import kalix.javasdk.annotations.Subscribe;
import kalix.spring.WebClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.CompletableFuture;

@Subscribe.EventSourcedEntity(value = LoanAppService.class, ignoreUnknown = true)
public class LoanAppToLoanProcEventingAction extends Action {

    private Logger logger = LoggerFactory.getLogger(LoanAppToLoanProcEventingAction.class);

    private final ActionCreationContext ctx;

    final private WebClient webClient;

    public LoanAppToLoanProcEventingAction(ActionCreationContext ctx, WebClientProvider webClientProvider) {
        this.webClient = webClientProvider.webClientFor("spring-loan");
        this.ctx = ctx;
    }

    public Action.Effect<LoanAppApi.EmptyResponse> onSubmitted(LoanAppDomainEvent.Submitted event) {
        logger.info("+++++++++++++++++++++++++");
        logger.info("+++++++++++++++++++++++++");
        logger.info(event.getLoanAppId());
        logger.info("+++++++++++++++++++++++++");
        logger.info("+++++++++++++++++++++++++");

        var emptyResponse = webClient
                .put()
                .uri("/loanproc/" + event.getLoanAppId() + "/process")
                .retrieve()
                .bodyToMono(LoanProcApi.EmptyResponse.class)
                .map(__ -> LoanAppApi.EmptyResponse.of())
                .toFuture();

        return effects().asyncReply(emptyResponse);
    }
}
