package com.drewpercraft.blockbank.tasks;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Player;

public class CalculateInterestTask extends BukkitRunnable {

	public static final int Minecraft_Days_In_Month = 8;
	public static final int Minecraft_Months_In_Year = 9;
	public static final int Minecraft_Days_In_Year = Minecraft_Days_In_Month * Minecraft_Months_In_Year;

	private final BlockBank plugin;
	private final UUID uuid;
	private long lastTimeCheck = 0;
	
	
	public CalculateInterestTask(BlockBank plugin, UUID uuid) 
	{
		this.plugin = plugin;
		this.uuid = uuid;
	}

	@Override
	public void run() 
	{
		if (alreadyRun()) return;
		World world = plugin.getServer().getWorld(uuid);
		plugin.getLogger().info("Starting Interest Calculation");
		Map<String, Double> totalInterestPaid = new HashMap<String, Double>();
		Map<String, Bank> banks = plugin.getBanks();
		Map<UUID, Player> players = plugin.getVaultAPI().getPlayerBalances();
		long now = new Date().getTime();
		for(Iterator<UUID> playerIT = players.keySet().iterator(); playerIT.hasNext();) {
			UUID uuid = playerIT.next();
			Player player = players.get(uuid);
			
			//TODO Verify player has been on the server and has not abandoned the account
			for(Iterator<String> bankNameIT = banks.keySet().iterator(); bankNameIT.hasNext();){
				String bankName = bankNameIT.next();
				double balance = player.getBankBalance(bankName);
				if (balance > 0) {
					long lastSeen = plugin.getServer().getOfflinePlayer(player.getUID()).getLastPlayed();
					int lastSeenDays = Math.round((now - lastSeen) / 86400000);
					if (lastSeenDays > plugin.getMaxOfflineDays()) {
						plugin.getLogger().info(player.getName() + " has been offline for " + lastSeenDays + " days - no interest paid");
					}else{
						double interestEarned = balance * banks.get(bankName).getSavingsRate() / Minecraft_Days_In_Year / 100;
						if (interestEarned >= 0.01) {
							player.setBankBalance(bankName, balance + interestEarned);
							plugin.getLogger().info(String.format("%s paid %s %s%6.2f in interest", bankName, player.getName(), plugin.getCurrencySymbol(), interestEarned));
							double interestPaid = 0;
							if (totalInterestPaid.containsKey(bankName)) {
								 interestPaid = totalInterestPaid.get(bankName); 
							}
							totalInterestPaid.put(bankName, interestPaid + interestEarned);
							if (plugin.getServer().getOfflinePlayer(player.getUID()).isOnline()) {
								plugin.sendMessage(plugin.getServer().getPlayer(player.getUID()), "InterestEarned", plugin.getBank(bankName).getTitle(), plugin.getCurrencySymbol(), interestEarned);
							}
						}
					}
				}
			}
		}
		for(Iterator<String> bankNameIT = totalInterestPaid.keySet().iterator(); bankNameIT.hasNext();) {
			String bankName = bankNameIT.next();
			plugin.getLogger().info(String.format("%s paid %s%9.2f in interest", bankName, plugin.getCurrencySymbol(), totalInterestPaid.get(bankName)));
		}
		plugin.getLogger().info("Interest Calculation Complete");
	}
	
	/*
	 * Ensure the task only runs once per day at the first check after 6am Minecraft time
	 */
	private boolean alreadyRun()
	{
		long currentTime = plugin.getServer().getWorld(uuid).getTime();
		if (currentTime > lastTimeCheck) {
			lastTimeCheck = currentTime;
			plugin.getLogger().info("Not running interest calc");
			return true;
		}
		lastTimeCheck = 0;
		return false;
	}

}
