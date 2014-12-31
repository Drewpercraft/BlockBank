package com.drewpercraft.blockbank.commands;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Player;
import com.drewpercraft.blockbank.PlayerBalanceCompare;

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
		
		Map<UUID, Player> players = plugin.getVaultAPI().getPlayerBalances();
		log.info(String.format("Found %d players", players.size()));
		TreeMap<UUID, Player> sortedBalances = new TreeMap<UUID, Player>(new PlayerBalanceCompare(players));
		for(UUID playerId : players.keySet()) {
			if (players.get(playerId).getWorth() > 0 && !plugin.getServer().getOfflinePlayer(playerId).isBanned()) {
				sortedBalances.put(playerId, players.get(playerId));
			}
		}
		log.info(String.format("Found %d sortedBalances", sortedBalances.size()));
		NavigableSet<UUID> keySet = sortedBalances.descendingKeySet();
		UUID[] keys = keySet.toArray(new UUID[sortedBalances.size()]);
		
		int pageCount = (keys.length / 10) + 1;
		
		int lastIndex = index + 10;
		if (lastIndex > keys.length) {
			lastIndex = keys.length;
		}
		
		log.info(String.format("Showing balances #%d to %d", index+1, lastIndex));
		plugin.sendMessage(sender, "BalanceTopHeader", page, pageCount);
		for(int i = index; i < lastIndex; i++ ) {
			String name = players.get(keys[i]).getName();
			Double balance = players.get(keys[i]).getBalance();
			String formattedBalance = plugin.getVaultAPI().format(balance);
			plugin.sendMessage(sender, "BalanceTopEntry", i+1, name, formattedBalance);
		}
		return true;
	}

}
