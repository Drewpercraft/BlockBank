package com.drewpercraft.blockbank.listeners;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.BlockBank;


public final class PlayerListener implements Listener {

	private BlockBank plugin;

    public PlayerListener(BlockBank plugin) {
        this.plugin = plugin;
    }
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();
    	
    	if (player.hasPermission("blockbank.user")) {
	    	plugin.getVaultAPI().createPlayerAccount(player);
	    	plugin.getLogger().fine("Checking for offline interest and dividends");
	    	double offlineDividend = plugin.getVaultAPI().getPlayer(player.getUniqueId()).getOfflineDividend();
	    	if (offlineDividend > 0.0) {
	    		plugin.sendMessage(player, "OfflineDividends", plugin.getVaultAPI().format(offlineDividend));
	    		plugin.getLogger().fine("Offline Dividend reported: " + plugin.getVaultAPI().format(offlineDividend));
	    		plugin.getVaultAPI().getPlayer(player.getUniqueId()).resetOfflineDividend();
	    	}
    		Set<String> bankNames = plugin.getBanks().keySet();
    		for(Iterator<String> bankNameIT = bankNames.iterator(); bankNameIT.hasNext();) {
    			String bankName = bankNameIT.next();
    			double offlineInterest = plugin.getVaultAPI().getPlayer(player.getUniqueId()).getOfflineInterest(bankName);
    			if (offlineInterest > 0.0) {
    				plugin.getLogger().fine("Offline Interest reported: " + bankName + " : " + plugin.getVaultAPI().format(offlineInterest));
    				plugin.sendMessage(player, "OfflineInterest", plugin.getBank(bankName).getTitle(), plugin.getVaultAPI().format(offlineInterest));
    				plugin.getVaultAPI().getPlayer(player.getUniqueId()).resetOfflineInterest(bankName);
    			}
    		}	    	
    	}    	
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
    	plugin.getLogger().info("Processing death event for:" + event.getEntity().getName());
    	if (plugin.getDropMoney()) {
	    	Player deadMan = event.getEntity();
	    	double pocketCash = plugin.getVaultAPI().getBalance(deadMan);
	    	Player killer = deadMan.getKiller();
	    	
	    	if (pocketCash > 0 && killer != null) {
	    		plugin.getVaultAPI().withdrawPlayer(deadMan, pocketCash);
	    		plugin.sendMessage(deadMan, "DeathNotice", plugin.getVaultAPI().format(pocketCash));
	    		plugin.getVaultAPI().depositPlayer(killer, pocketCash);
	    		plugin.sendMessage(killer, "KillerNotice", plugin.getVaultAPI().format(pocketCash), Utils.getPossessive(deadMan.getDisplayName()));
	    	}
    	}
    }
    

}
