package com.drewpercraft.blockbank;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.drewpercraft.blockbank.Bank;
import com.drewpercraft.blockbank.commands.CommandBalance;
import com.drewpercraft.blockbank.commands.CommandBalanceTop;
import com.drewpercraft.blockbank.commands.CommandBank;
import com.drewpercraft.blockbank.commands.CommandPay;
import com.drewpercraft.blockbank.listeners.PlayerListener;


public final class BlockBank extends JavaPlugin {
	
	protected Logger log;
	private Map<UUID, Bank> banks = new HashMap<UUID, Bank>();

	@Override
    public void onEnable() {
		log = getLogger();
    	log.info(String.format("Enabling %s commands", this.getName()));
    	this.getCommand("bank").setExecutor(new CommandBank(this));
    	this.getCommand("balance").setExecutor(new CommandBalance(this));
    	this.getCommand("pay").setExecutor(new CommandPay(this));
    	this.getCommand("balanceTop").setExecutor(new CommandBalanceTop(this));
    	
    	loadConfiguration();
    	
    	log.info(String.format("Enabling %s event handlers", this.getName()));
    	getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    	//TODO Start Security
    	//TODO Start InterestManager
    	/*
		BukkitRunnable interestManager = new InterestManagerTask(this);
		// Run the interestManager at the stroke of midnight (18000)
		String worldName = getServer().getWorlds().get(0).getName();
		long delay = 18000 - getServer().getWorld(worldName).getTime();
		if (delay < 0) delay += 24000;
		interestManager.runTaskTimer(this, delay, 24000);
		getServer().getPluginManager().registerEvents(this, this);
		log.info(String.format("%s has been enabled.", getDescription().getName()));	    	  
    	 
    	*/
    	log.info(String.format("%s enabled", this.getName()));
    }
 
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	log.info(String.format("%s Disabled", this.getName()));
    }
    
    
    public void loadConfiguration() {
    	log.info("Loading configuration");
    	banks.clear();
   		this.saveDefaultConfig();
   		if (!this.getConfig().contains("banks")) this.getConfig().createSection("banks");
		initializeMessages();

   		ConfigurationSection bankConfig = this.getConfig().getConfigurationSection("banks");
    	Set<String> bank_ids = bankConfig.getKeys(false);
    	for(Iterator<String> bankIT = bank_ids.iterator(); bankIT.hasNext();) {
    		String bankID = bankIT.next();
    		Bank bank = new Bank(this, bankID);
    		banks.put(bank.getId(), bank);
    	}
    	this.saveConfig();
    	log.info("Configuration Load Completed");
    }
    
    private void initializeMessages()
    {
   		if (!this.getConfig().contains("messages")) {
   			this.getConfig().createSection("messages");
   		}
   		ConfigurationSection messages = this.getConfig().getConfigurationSection("messages");
    	//TODO abstract all initial chat strings here
   		messages.addDefault("BRANCH_CLOSED", "This branch is currently closed.");
   		messages.addDefault("BRANCH_CLOSING", "The {branch.name} of {bank.name} is closing.");
    }
    
    protected String getMessage(String id)
    {
    	return "Not Implemented";
    }
    
    
    
    protected static String intToTime(int value) {
		int hour = value;
		String ampm = "am";
		if (hour > 12) {
			hour -= 12;
			if (hour != 12) ampm = "pm";
		}
		if (hour == 0) hour = 12;
		return String.format("%d:00%s", hour, ampm);
	}
	
	protected static boolean booleanValue(String value) {		
		if (Integer.parseInt("0" + value) > 0 || value.equals("on") || value.startsWith("y") || value.startsWith("t")) return true;
		return false;
	}

	public int getDefaultMaxVaults() {
		return this.getConfig().getInt("maxVaults", 10);
	}

	public boolean getDefaultAnnouncements() {
		return this.getConfig().getBoolean("announcements", true);
	}
	
	public double getDefaultSavingsRate() {
		return this.getConfig().getDouble("savingsRate", 0);
	}
	
	public double getDefaultLoanRate() {
		return this.getConfig().getDouble("loanRate", 0);
	}
	
	public int getDefaultOpenHour() {
		return this.getConfig().getInt("openHour", 8);
	}
    
	public int getDefaultCloseHour() {
		return this.getConfig().getInt("closeHour", 17);
	}
}
