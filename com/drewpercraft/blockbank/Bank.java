package com.drewpercraft.blockbank;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.Account;

public class Bank {
	
	private final BlockBank plugin;
	private final Logger log;
	private final UUID id;
	private String name = "";
	private List<Branch> branches = new ArrayList<Branch>();
	private List<Location> atms = new ArrayList<Location>();
	private List<Account> accounts = new ArrayList<Account>();
	
	private ConfigurationSection config = null;
	
	public Bank(BlockBank plugin)
	{
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.id = UUID.randomUUID();
		this.name = "Un-named bank";
		this.config = this.plugin.getConfig().getConfigurationSection("banks").createSection(this.id.toString());		
	}
	
	public Bank(BlockBank plugin, String id)
	{
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.id = UUID.fromString(id);
		this.config = this.plugin.getConfig().getConfigurationSection("banks").getConfigurationSection(id);
		loadConfiguration();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	public void loadConfiguration()
	{
		log.info(String.format("Loading Bank: %s", this));
		branches.clear();
		atms.clear();
		accounts.clear();
    	ConfigurationSection branchConfigs = this.config.getConfigurationSection("branches");
    	if (branchConfigs == null) {
    		branchConfigs = this.config.createSection("branches");
    	}
    	
    	ConfigurationSection atmConfigs = this.config.getConfigurationSection("atms");
    	if (atmConfigs == null) {
    		atmConfigs = this.config.createSection("atms");
    	}
    	
    	ConfigurationSection accounts = this.config.getConfigurationSection("accounts");
    	if (accounts == null) {
    		accounts = this.config.createSection("accounts");
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
	
	/**
	 * @return the plugin
	 */
	public BlockBank getPlugin() {
		return plugin;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.config.getString("name", "Un-named Bank");
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		//TODO Check to make sure we're not renaming this bank to a name already in use
		//TODO Implement setting error
		if (name.length() < 1) return;
		this.config.set("name", name);
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

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @return the branches
	 */
	public List<Branch> getBranches() {
		return branches;
	}

	/**
	 * @return the atms
	 */
	public List<Location> getAtms() {
		return atms;
	}

	/**
	 * @return the accounts
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	public ConfigurationSection getConfig() {
		// TODO Auto-generated method stub
		return this.config;
	}
	
	
}
