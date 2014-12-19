package com.drewpercraft.blockbank.commands;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;

import com.drewpercraft.blockbank.BlockBank;

public class CommandBalance implements CommandExecutor {

	private final BlockBank plugin;
	private final Logger log;
	
	public CommandBalance(BlockBank plugin) {
		this.plugin = plugin;
		this.log = this.plugin.getLogger();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,	String[] params) {
		if (sender instanceof OfflinePlayer)
		{
			OfflinePlayer player;
			if (params.length > 0) {
				String playerName = params[0].toLowerCase();
				player = plugin.getServer().getOfflinePlayer(playerName);
				if (!(player instanceof OfflinePlayer)) {
					sender.sendMessage(String.format(plugin.getMessage("InvalidPlayer"), playerName)); //$NON-NLS-1$
					return true;
				}
			}else{
				player = (OfflinePlayer) sender;
			}
			double amount = plugin.getVaultAPI().getBalance(player);
			sender.sendMessage(String.format(plugin.getMessage("WalletBalance"), plugin.getVaultAPI().format(amount))); //$NON-NLS-1$
			return true;
		}
		return false;
	}

}
