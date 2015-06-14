package com.drewpercraft.blockbank.commands;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Branch;

public class CommandDeposit implements TabExecutor {

private final BlockBank plugin;
	
	public CommandDeposit(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
		if (offlinePlayer == null) {
			plugin.sendMessage(sender, "InvalidConsoleCommand");
			return true;
		}
		
		double amount = 0.0;
		if (args.length > 0) {
			amount = Utils.getDouble(args[0]);
		}
		
		if (amount < 0) {
			plugin.sendMessage(sender, "NegativeAmountUsed");
			return true;
		}
		
		if (amount == 0.0) {
			amount = plugin.getVaultAPI().getBalance(offlinePlayer);
		}
		
		//Verify the user has the deposit amount in hand
		if (!plugin.getVaultAPI().has(offlinePlayer, amount)) {
			plugin.sendMessage(sender, "InsufficientFunds", plugin.getVaultAPI().format(amount));
			return true;
		}
		
		//Get the branch the user is in
		Branch branch = plugin.getPlayerBranch(offlinePlayer);
		if (branch == null) {
			//TODO If the user isn't in a branch, see if they are in an ATM
			plugin.sendMessage(sender, "PlayerNotInBranch");
			return true;
		}else{
			//Verify the branch is open
			if (branch.isClosed()) {
				plugin.sendMessage(sender, "BranchClosed", branch.getTitle(), branch.getBank().getTitle());
				return true;
			}
		}
		
		if (branch.isOutOfOrder()) {
			plugin.sendMessage(sender, "ATMOutOfOrder", branch.getTitle());
			return true;
		}
		
		if (amount < branch.getTransactionFee()) {
			plugin.sendMessage(sender, "DepositTransactionFee", plugin.getVaultAPI().format(branch.getTransactionFee()));
			return true;
		}
			
		Bank bank = branch.getBank();
		if (bank.deposit(offlinePlayer, amount)) {
			plugin.getVaultAPI().getPlayer(offlinePlayer).withdraw(branch.getTransactionFee());
			plugin.sendMessage(sender, "BankDepositSuccess", plugin.getVaultAPI().format(amount));
		}else{
			plugin.sendMessage(sender, "BankDepositFailed", plugin.getVaultAPI().format(amount));
		}
		return true;
	}

}
