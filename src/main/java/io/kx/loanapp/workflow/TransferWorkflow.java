//package io.kx.loanapp.workflow;
//
//import kalix.javasdk.annotations.Id;
//import kalix.javasdk.annotations.TypeId;
//import kalix.javasdk.workflow.Workflow;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@TypeId("transfer")
//@Id("transferId")
//@RequestMapping("/transfer/{transferId}")
//public class TransferWorkflow extends Workflow<TransferState> {
//
//    public record Withdraw(String from, int amount) {}
//    public record Deposit(String to, int amount) {}
//    public record Message(String msg) {}
//
//    @PutMapping
//    public Effect<Message> startTransfer(@RequestBody TransferState.Transfer transfer) {
//        if (transfer.amount() <= 0) {
//            return effects().error("transfer amount should be greater than zero");
//        } else if (currentState() != null) {
//            return effects().error("transfer already started");
//        } else {
//            TransferState initialState = new TransferState(transfer);
//            Withdraw withdrawInput = new Withdraw(transfer.from(), transfer.amount());
//            return effects().updateState(initialState)
//                    .transitionTo("withdraw", withdrawInput)
//                    .thenReply(new Message("transfer workflow successfully started"));
//        }
//    }
//
//    @Override
//    public WorkflowDef<TransferState> definition() {
//        Step withdraw =
//                step("withdraw")
//                        .call(Withdraw.class, cmd -> {
//                            return
//                        })
//        return null;
//    }
//}
