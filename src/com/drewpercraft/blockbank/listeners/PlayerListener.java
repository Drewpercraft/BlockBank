/**
 * 
 */
package com.drewpercraft.blockbank.listeners;


import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.BlockBank;



/**
 * @author jjarrell
 *
 */
public final class PlayerListener implements Listener {

	private BlockBank plugin;

    public PlayerListener(BlockBank plugin) {
        this.plugin = plugin;
    }
	
    @EventHandler
    public void onLogin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();
    	if (player.hasPermission("blockbank.user")) {
	    	plugin.getVaultAPI().createPlayerAccount(player);
    	}
    }
    
    public void onDeath(PlayerDeathEvent event)
    {
    	plugin.getLogger().info("Processing death event for:" + event.getEntity().getName());
    	if (plugin.getDropMoney()) {
	    	Player deadMan = event.getEntity();
	    	double pocketCash = plugin.getVaultAPI().getBalance(deadMan);
	    	
	    	if (pocketCash > 0) {
	    		plugin.getVaultAPI().withdrawPlayer(deadMan, pocketCash);
	    		deadMan.sendMessage("Your killer raided your pockets and took " + plugin.getVaultAPI().format(pocketCash));
	    	
	    		Player killer = event.getEntity().getKiller();    	
	    		if (killer instanceof HumanEntity) {
	    			plugin.getVaultAPI().depositPlayer(killer, pocketCash);
	    			killer.sendMessage("You were able to find " + plugin.getVaultAPI().format(pocketCash) + " in " + Utils.getPossessive(deadMan.getDisplayName()) + " pockets.");
	    		}
	    	}
    	}
    }
    

}
