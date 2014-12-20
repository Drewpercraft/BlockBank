package com.drewpercraft.blockbank.commands;

import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.drewpercraft.blockbank.BlockBank;

public class CommandPay implements CommandExecutor {

	private final BlockBank plugin;
	private final Logger log;
	
	public CommandPay(BlockBank plugin) {
		this.plugin = plugin;
		this.log = this.plugin.getLogger();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,	String[] params) {
		if (sender instanceof OfflinePlayer)
		{
			if (params.length < 2) {
				return false;
			}
			OfflinePlayer player = (OfflinePlayer) sender;
		
			String payeeName = params[0].toLowerCase();
			double amount = Double.parseDouble(params[1]);
			String displayAmount = plugin.getVaultAPI().format(amount);
			
			if (!plugin.getVaultAPI().has(player, amount))
			{
				sender.sendMessage(String.format(plugin.getMessage("InsufficientFunds"), displayAmount));
				return true;
			}
			
			OfflinePlayer payee = plugin.getServer().getOfflinePlayer(payeeName);
			if (!(payee instanceof OfflinePlayer)) {
				sender.sendMessage(String.format(plugin.getMessage("InvalidPlayer"), payeeName)); //$NON-NLS-1$
				return true;
			}
			
			plugin.getVaultAPI().withdrawPlayer(player, amount);
			plugin.getVaultAPI().depositPlayer(payee, amount);
						
			sender.sendMessage(String.format(plugin.getMessage("PaymentSent"), payeeName, displayAmount)); //$NON-NLS-1$
			if (payee.isOnline()) {
				payee.getPlayer().sendMessage(String.format(plugin.getMessage("PaymentReceived"), player.getName(), displayAmount));
			}
			return true;
		}
		return false;
	}

}
