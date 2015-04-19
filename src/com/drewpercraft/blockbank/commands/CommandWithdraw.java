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

public class CommandWithdraw implements TabExecutor {

private final BlockBank plugin;
	
	public CommandWithdraw(BlockBank plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
		if (offlinePlayer == null) {
			plugin.sendMessage(sender, "InvalidConsoleCommand");
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
		
		double amount = 0.0;
		if (args.length > 0) {
			amount = Utils.getDouble(args[0]);
		}

		if (amount < 0.0) {
			plugin.sendMessage(sender, "NegativeAmountUsed");
			return true;
		}

		Bank bank = branch.getBank();
		if (amount == 0.0) {
			amount = bank.getPlayerBalance(offlinePlayer.getUniqueId());
		}
		
		//Verify the user has the withdrawal amount in bank
		if (bank.withdraw(offlinePlayer, amount)) {
			plugin.sendMessage(sender, "BankWithdrawSuccess", plugin.getVaultAPI().format(amount));
		}else{
			plugin.sendMessage(sender, "BankWithdrawFailed", plugin.getVaultAPI().format(amount));
		}
		return true;
	}

}
