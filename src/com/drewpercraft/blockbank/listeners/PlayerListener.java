/**
 * 
 */
package com.drewpercraft.blockbank.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onLogin(PlayerLoginEvent event)
    {
    	Player player = event.getPlayer();
    	if (player.hasPermission("blockbank.user")) {
	    	plugin.getVaultAPI().createPlayerAccount(player);
    	}
    }
    
    @EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
    	Player player = event.getPlayer();
    	if (player.hasPermission("blockbank.user")) {
	    	//FIXME plugin.getVaultAPI().getPlayer(player.getUniqueId()).save();
    	}
    }
    
}
