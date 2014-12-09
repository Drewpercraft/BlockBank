package com.drewpercraft.blockbank;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

import com.drewpercraft.blockbank.commands.CommandBank;


public final class BlockBank extends JavaPlugin {
	
	protected static Economy economy = null;
	
	@Override
    public void onEnable() {        
    	getLogger().info("Enabling BlockBank");
		if (!setupEconomy() ) {
            getLogger().info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
    	this.getCommand("bank").setExecutor(new CommandBank(this));
    	
    	//TODO Load configuration
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	getLogger().info("Disabling BlockBank");
    }
    
	/*****************************/
	/*  Vault Specific functions */
	/*****************************/
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
	
}
