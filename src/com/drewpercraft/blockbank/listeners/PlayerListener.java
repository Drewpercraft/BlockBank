/**
 * 
 */
package com.drewpercraft.blockbank.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;

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
    

}
