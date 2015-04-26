package com.drewpercraft.blockbank.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Player;

public class CalculateInterestTask extends BukkitRunnable {

	public static final int Minecraft_Days_In_Month = 8;
	public static final int Minecraft_Months_In_Year = 9;
	public static final int Minecraft_Days_In_Year = Minecraft_Days_In_Month * Minecraft_Months_In_Year;

	private final BlockBank plugin;
	
	public CalculateInterestTask(BlockBank plugin) 
	{
		this.plugin = plugin;
	}

	@Override
	public String toString() {
		return "BlockBank Calculate Interest Task";
	}
	
	@Override
	public void run() 
	{
		//FIXME If the clock is modified (sleep, time, etc) then the calculation
		// no longer runs at midnight and doesn't match the Minecraft day count
		plugin.getLogger().info("Starting Interest Calculation");
		Map<String, Bank> banks = plugin.getBanks();
		Map<String, Double> totalInterestPaid = new HashMap<String, Double>();
		List<UUID> abandonedAccounts = new ArrayList<UUID>();
		
		for(Iterator<String> bankNameIT = banks.keySet().iterator(); bankNameIT.hasNext();){
			String bankName = bankNameIT.next();
			totalInterestPaid.put(bankName, 0.0);
		}
		Map<UUID, Player> players = plugin.getVaultAPI().getPlayerBalances();
		long now = new Date().getTime();
		for(Iterator<UUID> playerIT = players.keySet().iterator(); playerIT.hasNext();) {
			UUID uuid = playerIT.next();
			Player player = players.get(uuid);
			long lastSeen = plugin.getServer().getOfflinePlayer(player.getUID()).getLastPlayed();
			int lastSeenDays = Math.round((now - lastSeen) / 86400000);

			//If the player has not logged in, the account is considered abandoned
			boolean accountAbandoned = (lastSeenDays >= plugin.getAbandonedAccountDays());
			
			//Check to see if the account has been banned
			boolean isBanned = false;
			if (!accountAbandoned) {
				// Old way is just to look here...
				//boolean isBanned = plugin.getServer().getOfflinePlayer(uuid).isBanned();
				// Only consider the player banned if it is a permanent ban							
				org.bukkit.OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(uuid);
				if (offlinePlayer == null) {
					isBanned = true;
				}else{
					isBanned = Bukkit.getBanList(Type.NAME).isBanned(offlinePlayer.getName());
					//If the player is banned, make sure it is a permanent ban
					if (isBanned) {
						isBanned = (Bukkit.getBanList(Type.NAME).getBanEntry(offlinePlayer.getName()).getExpiration() == null);
					}
				}
			}
			if (accountAbandoned || isBanned) {
				abandonedAccounts.add(player.getUID());
			}else{
				for(Iterator<String> bankNameIT = banks.keySet().iterator(); bankNameIT.hasNext();){
					String bankName = bankNameIT.next();
					double balance = player.getBankBalance(bankName);
					if (balance > 0) {
						if (lastSeenDays > plugin.getMaxOfflineDays()) {
							plugin.getLogger().info(player.getName() + " has been offline for " + lastSeenDays + " days - no interest paid");
						}else{
							double interestEarned = balance * banks.get(bankName).getSavingsRate() / Minecraft_Days_In_Year / 100;
							if (interestEarned >= 0.01) {
								player.setBankBalance(bankName, balance + interestEarned);
								plugin.getLogger().info(String.format("%s paid %s %s in interest", bankName, player.getName(), plugin.getVaultAPI().format(interestEarned)));
								totalInterestPaid.put(bankName, totalInterestPaid.get(bankName) + interestEarned);
								if (plugin.getServer().getOfflinePlayer(player.getUID()).isOnline()) {
									plugin.sendMessage(plugin.getServer().getPlayer(player.getUID()), "InterestEarned", plugin.getBank(bankName).getTitle(), plugin.getVaultAPI().format(interestEarned));
								}
							}
						}
					}
				}
			}
		}
		for(Iterator<String> bankNameIT = totalInterestPaid.keySet().iterator(); bankNameIT.hasNext();) {
			String bankName = bankNameIT.next();
			if (totalInterestPaid.get(bankName) > 0.0) {
				plugin.getLogger().info(String.format("%s paid %s in interest", bankName, plugin.getVaultAPI().format(totalInterestPaid.get(bankName))));
			}
		}
		
		double abandonedMoney = 0;
		for(Iterator<UUID> uidIT = abandonedAccounts.iterator(); uidIT.hasNext();) {
			UUID uid = uidIT.next();
			Player player = plugin.getVaultAPI().getPlayer(uid);
			double playerAbandoned = player.getWorth();
			if (playerAbandoned > 0.0) {
				plugin.getLogger().info(String.format("%s abandoned %s", player.getName(), plugin.getVaultAPI().format(playerAbandoned)));
				abandonedMoney += playerAbandoned;
			}
			plugin.getLogger().info("Removing " + player.getName());
			player.deletePlayerFile();
			plugin.getVaultAPI().getPlayerBalances().remove(uid);
		}
		
		
		if (abandonedMoney > 0.0) {
			plugin.getLogger().info(String.format("Found %s in abandoned accounts", plugin.getVaultAPI().format(abandonedMoney)));
			if (plugin.getAbandonedDistribution().equals("even") && players.size() > 0) {
				double distribution = abandonedMoney / players.size();
				if (distribution > 1) {
					for(Iterator<UUID> playerIT = players.keySet().iterator(); playerIT.hasNext();) {
						UUID uid = playerIT.next();
						players.get(uid).deposit(distribution);
					}
				}
				plugin.broadcastMessage("PayAbandonedMoney", plugin.getVaultAPI().format(distribution));

			}
		}
		plugin.getLogger().info("Interest Calculation Complete");
	}
	
}
