package com.drewpercraft.blockbank;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.drewpercraft.blockbank.commands.CommandBalance;
import com.drewpercraft.blockbank.commands.CommandBalanceTop;
import com.drewpercraft.blockbank.commands.CommandBank;
import com.drewpercraft.blockbank.commands.CommandPay;
import com.drewpercraft.blockbank.listeners.PlayerListener;


public final class BlockBank extends JavaPlugin {
	
	protected Logger log;
	private Map<UUID, Bank> banks = new HashMap<UUID, Bank>();
	private VaultEconomy vaultAPI = null;	
	private ResourceBundle userMessages;
 
    public String getCurrencyPlural() {
		return getConfig().getString("currencyPlural", "dollars");
	}
    
    public String getCurrencySingular() {
		return getConfig().getString("currencySingular", "dollar");
	}
    
    public String getCurrencySymbol() {
		return getConfig().getString("currencySymbol", "$");
	}
    
	public int getDecimals() {
		return getConfig().getInt("decimals", 2);
	}

	public boolean getDefaultAnnouncements() {
		return getConfig().getBoolean("announcements", true);
	}

	public int getDefaultCloseHour() {
		return getConfig().getInt("closeHour", 17);
	}
	
	public double getDefaultLoanRate() {
		return getConfig().getDouble("loanRate", 0);
	}
	
	public int getDefaultMaxVaults() {
		return getConfig().getInt("maxVaults", 10);
	}
	
	public int getDefaultOpenHour() {
		return getConfig().getInt("openHour", 8);
	}
    
	public double getDefaultSavingsRate() {
		return getConfig().getDouble("savingsRate", 0);
	}
	
	public boolean getDropMoney() {
		return getConfig().getBoolean("dropMoney", true);
	}
	
	/*
	 * Get the message using the key from the language file
	 * with the config.yml dialogPrefix/dialogSuffix added
	 */
	public String getMessage(String key)
	{
		if (!userMessages.containsKey(key)) {
			log.severe(String.format("Language file is missing the %s key", key));
			return key;
		}
		return getConfig().getString("dialogPrefix", "") + userMessages.getString(key) + getConfig().getString("dialogSuffix", "");
	}
	
	/*
	 * Get the message using the key from the language file
	 * and format the message using the params provided
	 */
	public String getMessage(String key, Object... args)
	{
		return String.format(getMessage(key), args);
	}
	
	/*
	 * Returns the bank owning the branch or ATM the player is standing
	 * in or null if he is not in a bank
	 */
	public Bank getPlayerBank(Player player) 
	{
		// TODO
		return null;
	}
	
	/*
	 * Returns the bank owning the branch or ATM the player is standing
	 * in or null if he is not in a bank
	 */
	public Branch getPlayerBranch(Player player)
	{
		// TODO
		return null;
	}

	/*
	 * Get the full path to where the player data files are stored
	 */
	public String getPlayerDataPath() {
		String filePath = getConfig().getString("playerDataPath", null);
		if (filePath == null) {
			filePath = getDataFolder().getAbsolutePath() + File.separator + "playerData";
		}
		return filePath;
	}
	
	public VaultEconomy getVaultAPI()
    {
    	if (vaultAPI == null) vaultAPI = new VaultEconomy(this);
    	return vaultAPI;
    }
	
	public boolean loadConfiguration() {
    	log.info("Loading configuration");
    	
    	try {
	    	banks.clear();
	   		this.saveDefaultConfig();
	   		if (!this.getConfig().contains("banks")) this.getConfig().createSection("banks");
	
	   		ConfigurationSection bankConfig = this.getConfig().getConfigurationSection("banks");
	    	Set<String> bank_ids = bankConfig.getKeys(false);
	    	for(Iterator<String> bankIT = bank_ids.iterator(); bankIT.hasNext();) {
	    		String bankID = bankIT.next();
	    		Bank bank = new Bank(this, bankID);
	    		banks.put(bank.getId(), bank);
	    	}
	    	this.saveConfig();
	    	
	    	// Load the language file
	    	File messageFile = new File(this.getDataFolder() + File.separator + "language.txt");
	    	InputStream fis;
	    	if (messageFile.exists())
	    	{
	    		log.info("Loading language settings from " + messageFile);
	    		fis = new FileInputStream(messageFile);
	    	}else{
	    		log.info("Using default language settings");
	    		fis = this.getClass().getClassLoader().getResourceAsStream("language.txt");
	    	}
	    	 
			try {
			  userMessages = new PropertyResourceBundle(fis);
			} 
			catch (Exception e) {
				log.severe("Error loading language file:");
				throw e;
			}
			finally {
			  fis.close();
			}
	    	
	    	// Load the player accounts
	    	File playerDataPath = new File(getPlayerDataPath());
	    	if (!playerDataPath.isDirectory()) {
	    		log.info("Player Data Path " + getPlayerDataPath() + " was not found - creating");
	    		playerDataPath.mkdirs();
	    	}else{
	    		log.info("Found player path " + getPlayerDataPath());
	    	}
	    	
	    	// Make everything work with vault
	    	getVaultAPI();
	    
	    	log.info("Configuration Load Completed");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		log.severe("Error loading configuration. Disabling BlockBank.");
    		Bukkit.getPluginManager().disablePlugin(this);
    		return false;
    	}
    	return true;
    }

	@Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	log.info(String.format("%s Disabled", this.getName()));
    }

	@Override
    public void onEnable() {
		log = getLogger();
    	
    	if (loadConfiguration()) {
        	log.info(String.format("Enabling %s commands", this.getName()));
        	this.getCommand("bank").setExecutor(new CommandBank(this));
        	this.getCommand("balance").setExecutor(new CommandBalance(this));
        	this.getCommand("pay").setExecutor(new CommandPay(this));
        	this.getCommand("balanceTop").setExecutor(new CommandBalanceTop(this));
    	
        	log.info(String.format("Enabling %s event handlers", this.getName()));
        	getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	    	//TODO Start Security
	    	//TODO Start InterestManager
        	//TODO Start Background data file saver
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
    }

	/*
	 *  Send the player a message based on the language file
	 */
	public void sendMessage(CommandSender player, String key, Object... args) 
	{
		player.sendMessage(getMessage(key, args));		
	}

	
}
