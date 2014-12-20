package com.drewpercraft.blockbank.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBalance implements CommandExecutor {

	private final BlockBank plugin;
	
	public CommandBalance(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] params) {
		if (sender instanceof OfflinePlayer)
		{
			OfflinePlayer player;
			String walletName = "your";
			if (params.length > 0) {
				String playerName = params[0];
				player = plugin.getPlayerByName(playerName);
				if (!(player instanceof OfflinePlayer)) {
					sender.sendMessage(plugin.getMessage("InvalidPlayer", playerName)); //$NON-NLS-1$
					return true;
				}
				walletName = plugin.createPosessive(playerName);
			}else{
				player = (OfflinePlayer) sender;
			}
			double amount = plugin.getVaultAPI().getBalance(player);
			sender.sendMessage(plugin.getMessage("WalletBalance", plugin.getVaultAPI().format(amount), walletName)); //$NON-NLS-1$
			return true;
		}
		return false;
	}

}
