package io.kx.loanapp.workflow;

public record TransferState(Transfer transfer, TransferStatus status) {

    public record Transfer(String from, String to, int amount) {}

    public enum TransferStatus {
        STARTED, WITHDRAW_SUCCEED, COMPLETED
    }

    public TransferState(Transfer transfer) {
        this(transfer, TransferStatus.STARTED);
    }

    public TransferState withStatus(TransferStatus newStatus) {
        return new TransferState(transfer, newStatus);
    }
}
