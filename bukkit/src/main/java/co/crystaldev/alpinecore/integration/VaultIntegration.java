/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.integration;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegration;
import co.crystaldev.alpinecore.framework.integration.AlpineIntegrationEngine;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A bundled integration of the Vault API.
 * <p>
 * Allows AlpinePlugins to integrate Vault with minimal
 * code and without requiring its API to be added as a
 * dependency to their project.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class VaultIntegration extends AlpineIntegration {
    @Getter
    private static VaultIntegration instance;
    { instance = this; }

    /**
     * Reflectively accessed dependency injection constructor.
     *
     * @param plugin Instance of the owner plugin
     */
    private VaultIntegration(AlpinePlugin plugin) {
        super(plugin);
    }

    @Override
    protected boolean shouldActivate() {
        return this.plugin.getServer().getPluginManager().isPluginEnabled("Vault");
    }

    @Override
    protected @NotNull Class<? extends AlpineIntegrationEngine> getEngineClass() {
        return VaultEngine.class;
    }

    @Override
    public @NotNull VaultEngine getEngine() {
        VaultEngine engine = (VaultEngine) super.getEngine();
        if (engine == null) {
            throw new IllegalStateException("Vault not installed");
        }
        return engine;
    }

    /**
     * @since 0.1.0
     */
    @SuppressWarnings("unused")
    public static final class VaultEngine extends AlpineIntegrationEngine {
        private static final EconomyResponse NO_ECONOMY = new EconomyResponse(0.0D, 0.0D,
                EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No economy provider was registered");

        private VaultEngine(AlpinePlugin plugin) {
            super(plugin);
        }

        public @Nullable Economy getEconomy() {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp == null) return null;
            else return rsp.getProvider();
        }

        public @Nullable String getEconomyName() {
           Economy econ = this.getEconomy();
           if (econ == null) return null;
           else return econ.getName();
        }

        public @Nullable String getCurrencyNameSingular() {
            Economy econ = this.getEconomy();
            if (econ == null) return null;
            else return econ.currencyNameSingular();
        }

        public @Nullable String getCurrencyNamePlural() {
            Economy econ = this.getEconomy();
            if (econ == null) return null;
            else return econ.currencyNamePlural();
        }

        public boolean isEconomyEnabled() {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.isEnabled();
        }

        public boolean doesEconomySupportBanks() {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.hasBankSupport();
        }

        public int getEconomyFractionalDigits() {
            Economy econ = this.getEconomy();
            if (econ == null) return 0;
            else return econ.fractionalDigits();
        }

        public @Nullable String formatAmount(double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return null;
            else return econ.format(amount);
        }

        public boolean hasAccount(@NotNull OfflinePlayer player) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.hasAccount(player);
        }

        public boolean hasAccountInWorld(@NotNull OfflinePlayer player, @NotNull String world) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.hasAccount(player, world);
        }

        public double getBalance(@NotNull OfflinePlayer player) {
            Economy econ = this.getEconomy();
            if (econ == null) return 0.0D;
            else return econ.getBalance(player);
        }

        public double getBalanceInWorld(@NotNull OfflinePlayer player, @NotNull String world) {
            Economy econ = this.getEconomy();
            if (econ == null) return 0.0D;
            else return econ.getBalance(player, world);
        }

        public boolean hasFunds(@NotNull OfflinePlayer player, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.has(player, amount);
        }

        public boolean hasFundsInWorld(@NotNull OfflinePlayer player, @NotNull String world, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.has(player, world, amount);
        }

        public @NotNull EconomyResponse withdrawFunds(@NotNull OfflinePlayer player, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.withdrawPlayer(player, amount);
        }

        public boolean withdrawFundsSimple(@NotNull OfflinePlayer player, double amount) {
            return this.withdrawFunds(player, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse withdrawFundsInWorld(@NotNull OfflinePlayer player, @NotNull String world,
                                                             double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.withdrawPlayer(player, world, amount);
        }

        public boolean withdrawFundsInWorldSimple(@NotNull OfflinePlayer player, @NotNull String world, double amount) {
            return this.withdrawFundsInWorld(player, world, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse depositFunds(@NotNull OfflinePlayer player, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.depositPlayer(player, amount);
        }

        public boolean depositFundsSimple(@NotNull OfflinePlayer player, double amount) {
            return this.depositFunds(player, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse depositFundsInWorld(@NotNull OfflinePlayer player, @NotNull String world,
                                                            double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.depositPlayer(player, world, amount);
        }

        public boolean depositFundsInWorldSimple(@NotNull OfflinePlayer player, @NotNull String world, double amount) {
            return this.depositFundsInWorld(player, world, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse createBank(@NotNull OfflinePlayer player, @NotNull String name) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.createBank(name, player);
        }

        public boolean createBankSimple(@NotNull OfflinePlayer player, @NotNull String name) {
            return this.createBank(player, name).transactionSuccess();
        }

        public @NotNull EconomyResponse deleteBank(@NotNull String name) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.deleteBank(name);
        }

        public boolean deleteBankSimple(@NotNull String name) {
            return this.deleteBank(name).transactionSuccess();
        }

        public @NotNull EconomyResponse checkBankBalance(@NotNull String name) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.bankBalance(name);
        }

        public double checkBankBalanceSimple(@NotNull String name) {
            EconomyResponse response = this.checkBankBalance(name);
            return response.balance;
        }

        public @NotNull EconomyResponse bankHasFunds(@NotNull String name, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.bankHas(name, amount);
        }

        public boolean bankHasFundsSimple(@NotNull String name, double amount) {
            return this.bankHasFunds(name, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse bankWithdrawFunds(@NotNull String name, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.bankWithdraw(name, amount);
        }

        public boolean bankWithdrawFundsSimple(@NotNull String name, double amount) {
            return this.bankWithdrawFunds(name, amount).transactionSuccess();
        }

        public @NotNull EconomyResponse bankDepositFunds(@NotNull String name, double amount) {
            Economy econ = this.getEconomy();
            if (econ == null) return NO_ECONOMY;
            else return econ.bankDeposit(name, amount);
        }

        public boolean bankDepositFundsSimple(@NotNull String name, double amount) {
            return this.bankDepositFunds(name, amount).transactionSuccess();
        }

        public boolean isBankOwner(@NotNull OfflinePlayer player, @NotNull String name) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.isBankOwner(name, player).transactionSuccess();
        }

        public boolean isBankMember(@NotNull OfflinePlayer player, @NotNull String name) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.isBankMember(name, player).transactionSuccess();
        }

        public @Nullable List<String> getBanks() {
            Economy econ = this.getEconomy();
            if (econ == null) return null;
            else return econ.getBanks();
        }

        public boolean createAccount(OfflinePlayer player) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.createPlayerAccount(player);
        }

        public boolean createAccountInWorld(OfflinePlayer player, String world) {
            Economy econ = this.getEconomy();
            if (econ == null) return false;
            else return econ.createPlayerAccount(player, world);
        }
    }
}
