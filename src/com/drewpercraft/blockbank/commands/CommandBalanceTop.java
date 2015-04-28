package com.drewpercraft.blockbank.commands;

import java.text.DecimalFormat;
import java.util.List;
import java.util.NavigableSet;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.BlockBank;

public class CommandBalanceTop implements TabExecutor {

	private final BlockBank plugin;
	private final Logger log;
	
	public CommandBalanceTop(BlockBank plugin) {
		this.plugin = plugin;
		this.log = this.plugin.getLogger();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] params) {
		
		int page = 1;
		if (params.length > 0) {
			page = Utils.getInt(params[0]);
		}
		int index = (page - 1) * 10;
		if (index < 0) index = 0;
		
		
		NavigableSet<UUID> keySet = plugin.getVaultAPI().getSortedBalances().descendingKeySet();
		UUID[] keys = keySet.toArray(new UUID[keySet.size()]);
		
		int pageCount = (keys.length / 10) + 1;
		
		int lastIndex = index + 10;
		if (lastIndex > keys.length) {
			lastIndex = keys.length;
		}
		
		log.info(String.format("Showing balances #%d to %d", index+1, lastIndex));
		plugin.sendMessage(sender, "TotalEconomy", plugin.getVaultAPI().format(plugin.getVaultAPI().getTotalEconomy()));
		plugin.sendMessage(sender, "BalanceTopHeader", page, pageCount);
		for(int i = index; i < lastIndex; i++ ) {
			String name = plugin.getVaultAPI().getPlayer(keys[i]).getName();
			Double worth = plugin.getVaultAPI().getPlayer(keys[i]).getWorth();
			String formattedBalance = plugin.getVaultAPI().format(worth);
			DecimalFormat dFormat = new DecimalFormat("##.##"); 
			plugin.sendMessage(sender, "BalanceTopEntry", i+1, name, formattedBalance, dFormat.format(worth * 100 / plugin.getVaultAPI().getTotalEconomy()));
		}
		return true;
	}

}
