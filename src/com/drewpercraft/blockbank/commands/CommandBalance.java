package com.drewpercraft.blockbank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.Utils;

public class CommandBalance implements CommandExecutor {

	private final BlockBank plugin;
	
	public CommandBalance(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] params) {

		OfflinePlayer player;
		String walletName = plugin.getMessage("Your");
		if (params.length == 0) {
			if (sender instanceof OfflinePlayer) {
				player = (OfflinePlayer) sender;	
			}else{
				sender.sendMessage(plugin.getMessage("InvalidConsoleCommand", params[0]));
				return true;
			}
		}else{
			String playerName = params[0];
			player = Utils.getPlayerByName(playerName);
			if (player == null) {
				sender.sendMessage(plugin.getMessage("InvalidPlayer", playerName)); //$NON-NLS-1$
				return true;
			}
			walletName = Utils.getPossessive(playerName);
		}
		double amount = plugin.getVaultAPI().getBalance(player);
		sender.sendMessage(plugin.getMessage("WalletBalance", plugin.getVaultAPI().format(amount), walletName)); //$NON-NLS-1$
		return true;
	}

}
