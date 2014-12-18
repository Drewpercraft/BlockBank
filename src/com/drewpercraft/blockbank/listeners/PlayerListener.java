/**
 * 
 */
package com.drewpercraft.blockbank.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

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
	public void onQuit(PlayerQuitEvent event)
	{
    	Player player = event.getPlayer();
    	if (player.hasPermission("blockbank.user")) {
	    	plugin.unloadPlayer(player.getUniqueId());
    	}
    }
    
}
