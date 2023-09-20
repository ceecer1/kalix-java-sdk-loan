package io.kx.loanapp.api;

import kalix.javasdk.annotations.Id;
import kalix.javasdk.annotations.TypeId;
import kalix.javasdk.valueentity.ValueEntity;
import org.springframework.web.bind.annotation.*;

@Id("id")
@TypeId("wallet")
@RequestMapping("/wallet/{id}")
public class WalletEntity extends ValueEntity<WalletEntity.Wallet> {

    public record Wallet(String id, int balance) {
        public Wallet withdraw(int amount) {
            return new Wallet(id, balance - amount);
        }
        public Wallet deposit(int amount) {
            return new Wallet(id, balance + amount);
        }
    }

    @PostMapping("/create/{initBalance}")
    public Effect<String> create(@PathVariable String id, @PathVariable int initBalance) {
        return effects().updateState(new Wallet(id, initBalance)).thenReply("Ok");
    }

    @PatchMapping("/withdraw/{amount}")
    public Effect<String> withdraw(@PathVariable int amount) {
        Wallet updatedWallet = currentState().withdraw(amount);
        if (updatedWallet.balance < 0) {
            return effects().error("Insufficient balance");
        } else {
            return effects().updateState(updatedWallet).thenReply("Ok");
        }
    }

    @PatchMapping("/deposit/{amount}")
    public Effect<String> deposit(@PathVariable int amount) {
        Wallet updatedWallet = currentState().deposit(amount);
        return effects().updateState(updatedWallet).thenReply("Ok");
    }

    @GetMapping
    public Effect<Integer> get() {
        return effects().reply(currentState().balance());
    }
}
