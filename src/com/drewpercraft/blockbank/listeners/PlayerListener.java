/**
 * 
 */
package com.drewpercraft.blockbank.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;



/**
 * @author jjarrell
 *
 */
public final class PlayerListener implements Listener {

	private Plugin plugin;

    public PlayerListener(Plugin plugin) {
        this.plugin = plugin;
    }
	
    @EventHandler
	public void onLogin(PlayerLoginEvent event)
	{
    	Player player = event.getPlayer();
    	if (player.hasPermission("blockbank.user")) {
	    	if (!player.hasMetadata("blockbank.money")) {
	    		player.setMetadata("blockbank.money", new FixedMetadataValue(plugin, 0));
	    		player.sendMessage("BlockBank has given you a wallet.");
	    		plugin.getLogger().info(String.format("Created a wallet for %s", player.getName()));
	    	}else{
	    		String money = player.getMetadata("blockbank.money").get(0).asString();
	    		player.sendMessage(String.format("Your BlockBank wallet contains $%s", money));
	    		plugin.getLogger().info(String.format("%s wallet contains %s", player.getName(), money));
	    	}
    	}
    }
    
    
}
