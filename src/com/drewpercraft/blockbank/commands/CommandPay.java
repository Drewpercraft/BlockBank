package com.drewpercraft.blockbank.commands;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.Utils;

public class CommandPay implements TabExecutor {

	private final BlockBank plugin;
	
	public CommandPay(BlockBank plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof OfflinePlayer) {
			if (args.length < 2) {
				return false;
			}
			OfflinePlayer player = (OfflinePlayer) sender;
		
			String payeeName = args[0];
			double amount = Utils.getDouble(args[1]);
						
			if (amount < 0) {
				sender.sendMessage(plugin.getMessage("NegativeAmountUsed"));
				return true;
			}
			
			String displayAmount = plugin.getVaultAPI().format(amount);
			
			if (!plugin.getVaultAPI().has(player, amount)) 			{
				sender.sendMessage(plugin.getMessage("InsufficientFunds", displayAmount));
				return true;
			}
			
			OfflinePlayer payee = Utils.getPlayerByName(payeeName);
			if (payee == null) {
				sender.sendMessage(plugin.getMessage("InvalidPlayer", payeeName)); //$NON-NLS-1$
				return true;
			}
			
			//FIXME This returns an EconomyResponse, which should be checked.
			plugin.getVaultAPI().withdrawPlayer(player, amount);
			plugin.getVaultAPI().depositPlayer(payee, amount);
						
			sender.sendMessage(plugin.getMessage("PaymentSent", payeeName, displayAmount)); //$NON-NLS-1$
			if (payee.isOnline()) {
				payee.getPlayer().sendMessage(plugin.getMessage("PaymentReceived", player.getName(), displayAmount));
			}
			return true;
		}	
		sender.sendMessage(plugin.getMessage("InvalidConsoleCommand", label));
		return true;
	}

}
