package io.kx.loanapp.workflow;

import io.kx.loanapp.api.WalletEntity;
import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.client.ComponentClient;
import kalix.javasdk.workflow.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static io.kx.loanapp.workflow.TransferState.TransferStatus.COMPLETED;

@TypeId("transfer")
@Id("transferId")
@RequestMapping("/transfer/{transferId}")
public class TransferWorkflow extends Workflow<TransferState> {

    private static final Logger logger = LoggerFactory.getLogger(TransferWorkflow.class);

    final private ComponentClient componentClient;

    public TransferWorkflow(ComponentClient componentClient) {
        this.componentClient = componentClient;
    }

    public record Withdraw(String from, int amount) {}
    public record Deposit(String to, int amount) {}
    public record Message(String msg) {}

    @PutMapping
    public Effect<Message> startTransfer(@RequestBody TransferState.Transfer transfer) {
        if (transfer.amount() <= 0) {
            return effects().error("transfer amount should be greater than zero");
        } else if (currentState() != null) {
            return effects().error("transfer already started");
        } else {
            TransferState initialState = new TransferState(transfer);
            Withdraw withdrawInput = new Withdraw(transfer.from(), transfer.amount());
            return effects().updateState(initialState)
                    .transitionTo("withdraw", withdrawInput)
                    .thenReply(new Message("transfer workflow successfully started"));
        }
    }

    @Override
    public WorkflowDef<TransferState> definition() {
        Step withdraw =
                step("withdraw")
                        .call(Withdraw.class, cmd -> {
                            return componentClient.forValueEntity(cmd.from).call(WalletEntity::withdraw).params(cmd.amount);
                        }).andThen(String.class, __ -> {
                            Deposit depositInput = new Deposit(currentState().transfer().to(), currentState().transfer().amount());
                            return effects()
                                    .updateState(currentState().withStatus(TransferState.TransferStatus.WITHDRAW_SUCCEED))
                                    .transitionTo("deposit", depositInput);
                        });
        Step deposit =
                step("deposit")
                        .call(Deposit.class, cmd -> {
                            return componentClient.forValueEntity(cmd.to)
                                    .call(WalletEntity::deposit)
                                    .params(cmd.amount);
                        })
                        .andThen(String.class, __ -> {
                            return effects()
                                    .updateState(currentState().withStatus(COMPLETED))
                                    .end();
                        });

        return workflow().addStep(withdraw).addStep(deposit);
    }
}
