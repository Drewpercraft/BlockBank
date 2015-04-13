package com.drewpercraft.blockbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Bank {
	
	private final BlockBank plugin;
	private final Logger log;
	private final String name;
	private Map<String, Branch> branches = new HashMap<String, Branch>();
	private List<Location> atms = new ArrayList<Location>();	
	private ConfigurationSection config = null;
	private ConfigurationSection accounts = null;
	
	public Bank(BlockBank plugin, String name)
	{
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.name = name;
		this.config = this.plugin.getConfig().getConfigurationSection("banks").getConfigurationSection(name);
		if (this.config == null) {
			this.config = this.plugin.getConfig().getConfigurationSection("banks").createSection(name);
			this.config.set("title", name);
			this.config.set("announcements",  this.plugin.getConfig().getBoolean("announcements", true));
			this.config.set("savingsRate", this.plugin.getConfig().getDouble("savingsRate", 0));
			this.config.set("loanRate", this.plugin.getConfig().getDouble("loanRate", 0));
			this.config.set("maxVaults", this.plugin.getConfig().getInt("maxVaults", 10));
			this.plugin.saveConfig();
		}
		loadConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return config.getString("title");
	}
	
	public void loadConfiguration()
	{
		log.info(String.format("Loading Bank: %s", this));
		branches.clear();
		atms.clear();
    	ConfigurationSection branchConfigs = config.getConfigurationSection("branches");
    	if (branchConfigs == null) {
    		branchConfigs = config.createSection("branches");
    	}
    	for(String name : branchConfigs.getKeys(false)) {
    		Branch branch = new Branch(this, name);
    		branches.put(name, branch);
    	}
    	
    	ConfigurationSection atmConfigs = config.getConfigurationSection("atms");
    	if (atmConfigs == null) {
    		atmConfigs = config.createSection("atms");
    	}
    	
    	accounts = this.config.getConfigurationSection("accounts");
    	if (accounts == null) {
    		accounts = config.createSection("accounts");
    	}
    		
	}
		
	public Account createAccount(Player player, double amount)
	{
		//TODO Verify player has amount of cash in hand
		//TODO Get Bank player is standing in
		//TODO Verify player does not already have an account
		//TODO Create account
		return null;
	
	}
	
	public boolean deposit(OfflinePlayer player, double amount)
	{
		UUID uuid = player.getUniqueId();
		EconomyResponse response = plugin.getVaultAPI().withdrawPlayer(player, amount);
		if (!response.transactionSuccess()) return false;
		double previousBalance = getPlayerBalance(uuid);
		accounts.set(uuid.toString(), previousBalance + amount);
		this.plugin.saveConfig();
		return true;
	}
	
	public boolean withdraw(OfflinePlayer player, double amount)
	{
		UUID uuid = player.getUniqueId();
		double previousBalance = getPlayerBalance(uuid);
		if (previousBalance < amount) return false;
		
		EconomyResponse response = plugin.getVaultAPI().depositPlayer(player, amount);
		if (!response.transactionSuccess()) return false;
		accounts.set(uuid.toString(), previousBalance - amount);
		this.plugin.saveConfig();
		return true;		
	}
	
	public double getPlayerBalance(UUID uuid)
	{
		return accounts.getDouble(uuid.toString(), 0.0);
	}
	
	/**
	 * @return the plugin
	 */
	public BlockBank getPlugin() {
		return plugin;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * @return the name
	 */
	public String getTitle() {
		return this.config.getString("title", "Un-named Bank");
	}

	/**
	 * @param name the name to set
	 */
	public void setTitle(String title) {
		//TODO Check to make sure we're not renaming this bank to a name already in use
		//TODO Implement setting error
		if (title.length() < 1) return;
		this.config.set("title", title);
		this.plugin.saveConfig();
	}

	/**
	 * @return the announcements
	 */
	public boolean isAnnouncements() {
		return this.config.getBoolean("announcements", this.plugin.getDefaultAnnouncements());
	}

	/**
	 * @param announcements the announcements to set
	 */
	public void setAnnouncements(boolean announcements) {
		this.config.set("announcements", announcements);
		this.plugin.saveConfig();
	}

	/**
	 * @return the maxVaults
	 */
	public int getMaxVaults() {
		return this.config.getInt("maxVaults", this.plugin.getDefaultMaxVaults());
	}

	/**
	 * @param maxVaults the maxVaults to set
	 */
	public void setMaxVaults(int maxVaults) {
		if (maxVaults < -1) maxVaults = -1;
		//TODO Check that maxVaults does not exceed global maxVaults
		this.config.set("maxVaults", maxVaults);
		this.plugin.saveConfig();
	}

	/**
	 * @return the loanInterestRate
	 */
	public double getLoanRate() {
		return this.config.getDouble("loanRate", this.plugin.getDefaultLoanRate());
	}

	/**
	 * @param loanRate the loanRate to set
	 */
	public void setLoanRate(double loanRate) {
		if (loanRate < 0) loanRate = 0;
		if (loanRate > 30) loanRate = 30;
		this.config.set("loanRate", loanRate);
		this.plugin.saveConfig();
	}

	/**
	 * @return the savingsInterestRate
	 */
	public double getSavingsRate() {
		return this.config.getDouble("savingsRate", this.plugin.getDefaultSavingsRate());
	}

	/**
	 * @param savingsRate the savingsRate to set
	 */
	public void setSavingsRate(double savingsRate) {
		if (savingsRate < 0) savingsRate = 0;
		if (savingsRate > 30) savingsRate = 30;
		this.config.set("savingsRate", savingsRate);
		this.plugin.saveConfig();
	}

	public Branch getBranch(String branchName)
	{
		return branches.get(branchName);
	}
	
	/**
	 * @return the branches
	 */
	public Map<String, Branch> getBranches() 
	{
		return branches;
	}

	/**
	 * @return the atms
	 */
	public List<Location> getAtms() 
	{
		return atms;
	}


	public ConfigurationSection getConfig() {
		// TODO Auto-generated method stub
		return this.config;
	}

	public void createBranch(String world, String regionName)
	{
		Branch branch = new Branch(this, regionName);
		branch.setWorld(world);
		branches.put(regionName, branch);
		plugin.saveConfig();
	}

	public double getTotalDeposits() {
		double totalDeposits = 0;
		for(String uuid : accounts.getKeys(false)) {
			totalDeposits += accounts.getDouble(uuid);
		}
		return totalDeposits;
	}
	
}
