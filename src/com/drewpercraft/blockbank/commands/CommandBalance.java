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
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,	String[] params) {
		if (sender instanceof OfflinePlayer)
		{
			OfflinePlayer player;
			if (params.length > 0) {
				player = plugin.getServer().getPlayer(params[0]);
				if (!(player instanceof OfflinePlayer)) {
					sender.sendMessage(String.format("%s is not a valid player name.", params[0]));
					return true;
				}
			}else{
				player = (OfflinePlayer) sender;
			}
			double amount = plugin.getVaultAPI().getBalance(player);
			sender.sendMessage(String.format("You have %s in your wallet.", plugin.getVaultAPI().format(amount)));
			return true;
		}
		return false;
	}

}
