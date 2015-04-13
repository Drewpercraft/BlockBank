package com.drewpercraft.blockbank.commands;

import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.OfflinePlayer;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.Utils;

public class CommandBalance implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandBalance(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] params) {

		OfflinePlayer player;
		String walletName = plugin.getMessage("Your");
		if (params.length == 0) {
			if (sender instanceof OfflinePlayer) {
				player = (OfflinePlayer) sender;	
			}else{
				plugin.sendMessage(sender, "InvalidConsoleCommand", label);
				return true;
			}
		}else{
			String playerName = params[0];
			player = Utils.getPlayerByName(playerName);
			if (player == null) {
				plugin.sendMessage(sender, "InvalidPlayer", playerName);
				return true;
			}
			walletName = Utils.getPossessive(playerName);
		}
		double amount = plugin.getVaultAPI().getBalance(player);
		plugin.sendMessage(sender, "WalletBalance", plugin.getVaultAPI().format(amount), walletName);
		
		Set<String> bankNames = plugin.getBanks().keySet();
		double worth = amount;
		for(String bankName : bankNames) {
			double balance = plugin.getBank(bankName).getPlayerBalance(player.getUniqueId());
			if (balance != 0) {
				plugin.sendMessage(sender, "BankBalance", plugin.getBank(bankName).getTitle(), plugin.getVaultAPI().format(balance));
				worth += balance;
			}
		}
		plugin.sendMessage(sender, "TotalWorth", walletName, plugin.getVaultAPI().format(worth));
		return true;
	}
}