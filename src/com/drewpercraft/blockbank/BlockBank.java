package com.drewpercraft.blockbank;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.drewpercraft.WorldGuard;
import com.drewpercraft.blockbank.commands.CommandBalance;
import com.drewpercraft.blockbank.commands.CommandBalanceTop;
import com.drewpercraft.blockbank.commands.CommandBank;
import com.drewpercraft.blockbank.commands.CommandBorrow;
import com.drewpercraft.blockbank.commands.CommandBranch;
import com.drewpercraft.blockbank.commands.CommandCredit;
import com.drewpercraft.blockbank.commands.CommandDeposit;
import com.drewpercraft.blockbank.commands.CommandPay;
import com.drewpercraft.blockbank.commands.CommandWithdraw;
import com.drewpercraft.blockbank.listeners.PlayerListener;
import com.drewpercraft.blockbank.listeners.WorldListener;
import com.drewpercraft.blockbank.tasks.CalculateInterestTask;
import com.drewpercraft.blockbank.tasks.SavePlayersTask;


public final class BlockBank extends JavaPlugin {
	
	protected Logger log;
	private Map<String, Bank> banks = new HashMap<String, Bank>();
	private VaultEconomy vaultAPI = null;	
	private ResourceBundle userMessages;
	private CalculateInterestTask interestTask = null;
	private SavePlayersTask savePlayersTask = null;
 
	public int getAbandonedAccountDays()
	{
		return getConfig().getInt("abandonedAccountDays", 30);
	}
	
	public boolean getAbandonedCheck()
	{
		return getConfig().getBoolean("abandonedCheck", false);
	}
	
	public String getAbandonedDistribution()
	{
		return getConfig().getString("abandonedDistribution", "even");
	}

	public int getATMFailRate()
	{
		return getConfig().getInt("atmFailRate", 0);
	}

	public int getATMOfflineTime()
	{
		return getConfig().getInt("atmOfflineTime");
	}
	
	public String getCurrencyPlural() 
    {
		return getConfig().getString("currencyPlural", "dollars");
	}
    
    public String getCurrencySingular() 
    {
		return getConfig().getString("currencySingular", "dollar");
	}
    
    public String getCurrencySymbol() 
    {
		return getConfig().getString("currencySymbol", "$");
	}
    
	public int getDecimals() 
	{
		return getConfig().getInt("decimals", 2);
	}

	public boolean getDefaultAnnouncements() 
	{
		return getConfig().getBoolean("announcements", true);
	}

	public int getDefaultCloseHour() 
	{
		return getConfig().getInt("closeHour", 17);
	}
	
	public double getDefaultLoanRate() 
	{
		return getConfig().getDouble("loanRate", 0);
	}
	
	public World getMasterWorld()
	{
		String worldName = getConfig().getString("masterWorld");
		if (worldName == null) {
			return getServer().getWorlds().get(0);
		}
		World world = getServer().getWorld(worldName);
		if (world == null) {
			return getServer().getWorlds().get(0);
		}
		return world;
	}
	
	public int getDefaultMaxVaults() 
	{
		return getConfig().getInt("maxVaults", 10);
	}
	
	public int getDefaultOpenHour() 
	{
		return getConfig().getInt("openHour", 8);
	}
    
	public double getDefaultSavingsRate() 
	{
		return getConfig().getDouble("savingsRate", 0);
	}
	
	public boolean getDropMoney() 
	{
		return getConfig().getBoolean("dropMoney", true);
	}
	
	public boolean getAllowLoans() 
	{
		return getConfig().getBoolean("allowLoans", false);
	}
	
	public int getMaxBranches() 
	{
		return getConfig().getInt("maxBranches", 3);
	}
	
	public int getMaxOfflineDays()
	{
		return getConfig().getInt("maxOfflineDays", 3);
	}
	
	public boolean getLogTransactions() 
	{
		return getConfig().getBoolean("logTransactions", true);
	}
	
	public String getBankRelation() 
	{
		return getConfig().getString("bankRelation", "world");
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
	public Bank getPlayerBank(OfflinePlayer player) 
	{
		Branch branch = getPlayerBranch(player);
		if (branch == null) return null;
		return branch.getBank();
	}
	
	/*
	 * Returns the bank owning the branch or ATM the player is standing
	 * in or null if he is not in a bank
	 */
	public Branch getPlayerBranch(OfflinePlayer offlinePlayer)
	{
		for(String bankName : banks.keySet()) {
			Map<String, Branch> branches = banks.get(bankName).getBranches();
			for(String regionName : branches.keySet()) {
				if (WorldGuard.isPlayerInRegion(offlinePlayer, regionName)) {
					Branch branch = branches.get(regionName);
					org.bukkit.entity.Player player = offlinePlayer.getPlayer();
					if (player.getWorld().getName().equalsIgnoreCase(branch.getWorld())) {
						return branch;
					}
				}
			}
		}
		return null;
	}

	/*
	 * Get the full path to where the player data files are stored
	 */
	public String getPlayerDataPath() 
	{
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
	
	public boolean loadConfiguration() 
	{
    	log.info("Loading configuration");
    	
    	try {
	    	banks.clear();
	   		this.saveDefaultConfig();
	   		if (!this.getConfig().contains("banks")) this.getConfig().createSection("banks");
	
	   		ConfigurationSection bankConfig = this.getConfig().getConfigurationSection("banks");
	    	Set<String> bankNames = bankConfig.getKeys(false);
	    	for(Iterator<String> bankIT = bankNames.iterator(); bankIT.hasNext();) {
	    		String bankName = bankIT.next();
	    		Bank bank = new Bank(this, bankName);
	    		banks.put(bank.getName(), bank);
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
	    	vaultAPI = null;
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
    public void onDisable() 
	{
        // TODO Insert logic to be performed when the plugin is disabled
		if (interestTask != null) {
			interestTask.cancel();
		}
		if (savePlayersTask != null) {
			savePlayersTask.cancel();
		}
		vaultAPI.savePlayers();
    	log.info(String.format("%s Disabled", this.getName()));
    }

	@Override
    public void onEnable() 
	{
		log = getLogger();
    	
    	if (loadConfiguration()) {
        	log.info(String.format("Enabling %s commands", this.getName()));
        	CommandBalance commandBalance = new CommandBalance(this);
        	getCommand("balance").setExecutor(commandBalance);
        	getCommand("balance").setTabCompleter(commandBalance);
        	
        	CommandBalanceTop commandBalanceTop = new CommandBalanceTop(this);
        	getCommand("balanceTop").setExecutor(commandBalanceTop);
        	getCommand("balance").setTabCompleter(commandBalanceTop);
        	
        	CommandBank commandBank = new CommandBank(this);
        	getCommand("bank").setExecutor(commandBank);
        	getCommand("bank").setTabCompleter(commandBank);
        	
        	CommandBorrow commandBorrow = new CommandBorrow(this);
        	getCommand("borrow").setExecutor(commandBorrow);
        	getCommand("borrow").setTabCompleter(commandBorrow);
        	
        	CommandBranch commandBranch = new CommandBranch(this);
        	getCommand("branch").setExecutor(commandBranch);
        	getCommand("branch").setTabCompleter(commandBranch);
        	
        	CommandCredit commandCredit = new CommandCredit(this);
        	getCommand("credit").setExecutor(commandCredit);
        	getCommand("credit").setTabCompleter(commandCredit);
        	
        	CommandDeposit commandDeposit = new CommandDeposit(this);
        	getCommand("deposit").setExecutor(commandDeposit);
        	getCommand("deposit").setTabCompleter(commandDeposit);
        	
        	CommandPay commandPay = new CommandPay(this);
        	getCommand("pay").setExecutor(commandPay);
        	getCommand("pay").setTabCompleter(commandPay);
        	
        	CommandWithdraw commandWithdraw = new CommandWithdraw(this);
        	getCommand("withdraw").setExecutor(commandWithdraw);
        	getCommand("withdraw").setTabCompleter(commandWithdraw);
    	
        	log.info(String.format("Enabling %s event handlers", this.getName()));
        	getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        	getServer().getPluginManager().registerEvents(new WorldListener(this), this);
	    	//TODO Start Security
	    	
        	//Start the Interest Manager
        	World world = getMasterWorld();
        	getLogger().info("Using world " + world.getName() + " as the master clock");
        	interestTask = new CalculateInterestTask(this);        	
			// Run the interestManager at the stroke of midnight (18000)

			long delay = 18000 - world.getTime();
			if (delay < 0) delay += 24000;
			//TODO Should this really be synchronous or asynchronous?
			log.info("Calculate interest task delay: " + delay);
			interestTask.runTaskTimerAsynchronously(this, delay, 24000);
			
			savePlayersTask = new SavePlayersTask(this);
			savePlayersTask.runTaskTimerAsynchronously(this, 1000, 1000);
			
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

	public <T> void setConfig(String key, T setting) 
	{
		getConfig().set(key, setting);
		//TODO instead of saveConfig, this can be a timer/dirty flag or trigger an event
		// so the save happens in another thread
		saveConfig();
	}

	public List<String> getValidBankNames() 
	{
		String relationType = getConfig().getString("bankRelation").toLowerCase();
		if (relationType != "world" && relationType != "factions") return null;
		
		List<String> results = new ArrayList<String>();
		if (relationType.equals("world") || relationType.equals("all")) {
			for(World world : Bukkit.getWorlds()) {
				results.add(world.getName().toLowerCase());
			}			
		}
		if (relationType.equals("factions")) {
			//TODO Relating to factions is not implemented yet
			//for(Faction faction : ?.getFactions()) {
			//	results.add(faction.getName());
			//}			
		}
		return results;
	}

	public Bank getBank(String bankName) 
	{
		return banks.get(bankName);
	}
	
	public Map<String, Bank> getBanks()
	{
		return banks;
	}
	
	public void addBank(Bank bank)
	{
		banks.put(bank.getName(), bank);
		saveConfig();
	}

	public Bank getBankByRegion(String world, String regionName) 
	{
		for (Bank bank : banks.values()) {
			if (bank.getBranches().containsKey(regionName)) {
				if (bank.getBranches().get(regionName).getWorld().equalsIgnoreCase(world)) {
					return bank;
				}
			}
		}
		return null;
	}

	public void broadcastMessage(String key, Object... args) 
	{
		getServer().broadcastMessage(getMessage(key, args));		
	}

}
