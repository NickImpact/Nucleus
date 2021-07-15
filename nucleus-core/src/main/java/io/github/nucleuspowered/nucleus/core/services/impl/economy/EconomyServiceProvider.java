/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.core.services.impl.economy;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IEconomyServiceProvider;
import io.github.nucleuspowered.nucleus.core.services.interfaces.IMessageProviderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class EconomyServiceProvider implements IEconomyServiceProvider {

    private final IMessageProviderService messageProviderService;

    @Inject
    public EconomyServiceProvider(final IMessageProviderService messageProviderService) {
        this.messageProviderService = messageProviderService;
    }

    @Override
    public boolean serviceExists() {
        return Sponge.server().serviceProvider().economyService().isPresent();
    }

    @Override public String getCurrencySymbol(final double cost) {
        final Optional<EconomyService> oes = Sponge.server().serviceProvider().economyService();
        return oes.map(economyService -> economyService.defaultCurrency().format(BigDecimal.valueOf(cost)).toString())
                .orElseGet(() -> String.valueOf(cost));

    }

    @Override public boolean hasBalance(final UUID src, final double balance) {
        final Optional<EconomyService> oes = Sponge.server().serviceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> ua = es.findOrCreateAccount(src);
            return ua.isPresent() && ua.get().balance(es.defaultCurrency()).doubleValue() >= balance;
        }

        // No economy
        return true;
    }

    @Override public boolean withdrawFromPlayer(final UUID src, final double cost) {
        return this.withdrawFromPlayer(src, cost, true);
    }

    @Override public boolean withdrawFromPlayer(final UUID src, final double cost, final boolean message) {
        final Optional<EconomyService> oes = Sponge.server().serviceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> a = es.findOrCreateAccount(src);
            if (!a.isPresent()) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.noaccount"));
                return false;
            }

            // TODO: try (final CauseStackManager.StackFrame frame = )
            final TransactionResult tr = a.get().withdraw(es.defaultCurrency(), BigDecimal.valueOf(cost));
            if (tr.result() == ResultType.ACCOUNT_NO_FUNDS) {
                if (message) {
                    Sponge.server().player(src).ifPresent(x ->
                            this.messageProviderService.sendMessageTo(x, "cost.nofunds", this.getCurrencySymbol(cost)));
                }

                return false;
            } else if (tr.result() != ResultType.SUCCESS) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.error"));
                return false;
            }

            if (message) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.complete", this.getCurrencySymbol(cost)));
            }
        }

        return true;
    }

    @Override public boolean depositInPlayer(final UUID src, final double cost) {
        return this.depositInPlayer(src, cost, true);
    }

    @Override public boolean depositInPlayer(final UUID src, final double cost, final boolean message) {
        final Optional<EconomyService> oes = Sponge.server().serviceProvider().economyService();
        if (oes.isPresent()) {
            // Check balance.
            final EconomyService es = oes.get();
            final Optional<UniqueAccount> a = es.findOrCreateAccount(src);
            if (!a.isPresent()) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.noaccount"));
                return false;
            }

            final TransactionResult tr = a.get().deposit(es.defaultCurrency(), BigDecimal.valueOf(cost));
            if (tr.result() != ResultType.SUCCESS) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.error"));
                return false;
            }

            if (message) {
                Sponge.server().player(src).ifPresent(x ->
                        this.messageProviderService.sendMessageTo(x, "cost.refund", this.getCurrencySymbol(cost)));
            }
        }

        return true;
    }

}
