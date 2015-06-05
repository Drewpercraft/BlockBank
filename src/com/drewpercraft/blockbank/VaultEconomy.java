package com.drewpercraft.blockbank;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import com.drewpercraft.Utils;
import com.drewpercraft.blockbank.Player;

/*
 * Implementes the Vault Econonmy interface and contains
 * all the business logic required to support it
 */
public class VaultEconomy implements Economy {

	private BlockBank plugin = null;
	private Logger log = null;
	private Map<UUID, Player> players = Collections.synchronizedMap(new HashMap<UUID, Player>());
	private double totalEconomy = 0.0;
	private TreeMap<UUID, Player> sortedBalances = new TreeMap<UUID, Player>(new Player.BalanceCompare(players));
	
	
	

	public VaultEconomy(BlockBank plugin) 
	{
		this.plugin = plugin;
		this.log = plugin.getLogger();
		this.log.info("VaultEconomy loading existing accounts...");
		
		File playerDataPath = new File(this.plugin.getPlayerDataPath());
		File[] playerDataFiles = playerDataPath.listFiles();
		for (File file : playerDataFiles)
		{
			String filename = file.getName();
			if (filename.endsWith(".json"))
			{
				getPlayer(UUID.fromString(filename.substring(0, filename.length() - 5)));
			}
			
		}
		updateBalanceTop();
	}

	public Map<UUID, Player> getPlayerBalances()
	{
		return players;
	}
	
	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#bankBalance(java.lang.String)
	 */
	@Override
	public EconomyResponse bankBalance(String bankName) 
	{
		if (!plugin.getBanks().containsKey(bankName)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
		}
		Bank bank = plugin.getBank(bankName);
		return new EconomyResponse(0, bank.getTotalDeposits(), ResponseType.SUCCESS, "Total of all deposits");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#bankDeposit(java.lang.String, double)
	 */
	@Override
	public EconomyResponse bankDeposit(String arg0, double arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}
	
	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#bankHas(java.lang.String, double)
	 */
	@Override
	public EconomyResponse bankHas(String arg0, double arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#bankWithdraw(java.lang.String, double)
	 */
	@Override
	public EconomyResponse bankWithdraw(String arg0, double arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createBank(java.lang.String, org.bukkit.OfflinePlayer)
	 */
	@Override
	public EconomyResponse createBank(String bankName, OfflinePlayer player) 
	{
		return createBank(bankName, player);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createBank(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse createBank(String bankName, String playerName) 
	{
		return createBank(bankName, Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(org.bukkit.OfflinePlayer)
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer player) 
	{
		if (player == null) return false;
		getPlayer(player.getUniqueId()).save();
		return true;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(org.bukkit.OfflinePlayer, java.lang.String)
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) 
	{
		// BlockBank does not support separate accounts per world
		return createPlayerAccount(player);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName) 
	{
		return createPlayerAccount(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(java.lang.String, java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName, String worldName) 
	{
		// BlockBank does not support separate accounts per world
		return createPlayerAccount(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#currencyNamePlural()
	 */
	@Override
	public String currencyNamePlural() 
	{
		return plugin.getCurrencyPlural();
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#currencyNameSingular()
	 */
	@Override
	public String currencyNameSingular()
	{
		return plugin.getCurrencySingular();
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#deleteBank(java.lang.String)
	 */
	@Override
	public EconomyResponse deleteBank(String arg0) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(org.bukkit.OfflinePlayer, double)
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) 
	{
		Player player = getPlayer(offlinePlayer);
		double balance = player.deposit(amount);
		if (amount < 0) {
			return new EconomyResponse(amount, balance, ResponseType.FAILURE, plugin.getMessage("NegativeAmountUsed"));
		}
		if (plugin.getLogTransactions()) {
			plugin.getLogger().info(String.format("Deposit Player: %s %s%12.2f", player.getName(), plugin.getCurrencySymbol(), amount));
		}
		return new EconomyResponse(amount, balance, ResponseType.SUCCESS, plugin.getMessage("Deposit", format(balance)));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) 
	{
		return depositPlayer(player, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(java.lang.String, double)
	 */
	@Deprecated
	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		return depositPlayer(Utils.getPlayerByName(playerName), amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(java.lang.String, java.lang.String, double)
	 */
	@Deprecated
	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(Utils.getPlayerByName(playerName), amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#format(double)
	 */
	@Override
	public String format(double amount) {
		String symbol = plugin.getCurrencySymbol();
		String formatString = "%s%,." + fractionalDigits() + "f";
		return String.format(formatString, symbol, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#fractionalDigits()
	 */
	@Override
	public int fractionalDigits() {
		return plugin.getDecimals();
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(org.bukkit.OfflinePlayer)
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		if (offlinePlayer == null) return 0.0;
		
		Player player = players.get(offlinePlayer.getUniqueId());
		if (player instanceof Player) return player.getBalance();
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(org.bukkit.OfflinePlayer, java.lang.String)
	 */
	@Override
	public double getBalance(OfflinePlayer player, String world) {
		//BlockBank does not support separate balances per world.
		return getBalance(player);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(java.lang.String)
	 */
	@Override
	public double getBalance(String playerName) {
		return getBalance(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(java.lang.String, java.lang.String)
	 */
	@Override
	public double getBalance(String playerName, String worldName) {
		return getBalance(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBanks()
	 */
	@Override
	public List<String> getBanks() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getName()
	 */
	@Override
	public String getName() {
		return "BlockBank";
	}

	public Player getPlayer(OfflinePlayer offlinePlayer) {
		return getPlayer(offlinePlayer.getUniqueId());
	}

	public Player getPlayer(UUID uniqueId) {
		if (!players.containsKey(uniqueId)) {
			players.put(uniqueId, new Player(plugin, uniqueId));
		}
		return players.get(uniqueId);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(org.bukkit.OfflinePlayer, double)
	 */
	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return getBalance(player) >= amount;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public boolean has(OfflinePlayer player, String world, double amount) {
		return has(player, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(java.lang.String, double)
	 */
	@Override
	public boolean has(String playerName, double amount) {
		return has(Utils.getPlayerByName(playerName), amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(java.lang.String, java.lang.String, double)
	 */
	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(org.bukkit.OfflinePlayer)
	 */
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(org.bukkit.OfflinePlayer, java.lang.String)
	 */
	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return hasAccount(player);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean hasAccount(String playerName) {
		return hasAccount(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(java.lang.String, java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return hasAccount(Utils.getPlayerByName(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasBankSupport()
	 */
	@Override
	public boolean hasBankSupport() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankMember(java.lang.String, org.bukkit.OfflinePlayer)
	 */
	@Override
	public EconomyResponse isBankMember(String bankName, OfflinePlayer player) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankMember(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse isBankMember(String bankName, String playerName) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankOwner(java.lang.String, org.bukkit.OfflinePlayer)
	 */
	@Override
	public EconomyResponse isBankOwner(String bankName, OfflinePlayer player) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse isBankOwner(String bankName, String playerName) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	public void unloadPlayer(UUID uniqueId) {
		log.info("Unloading player " + uniqueId.toString());
		players.remove(uniqueId);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(org.bukkit.OfflinePlayer, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		
		if (!(player instanceof OfflinePlayer)) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, String.format(plugin.getMessage("InvalidPlayer"), "Unknown"));
		}
		
		double balance = players.get(player.getUniqueId()).getBalance();
		if (amount < 0) {
			return new EconomyResponse(amount, balance, ResponseType.FAILURE, plugin.getMessage("NegativeAmountUsed"));
		}
		String displayAmount = format(amount);
		
		if (balance < amount) {
			return new EconomyResponse(amount, balance, ResponseType.FAILURE, String.format(plugin.getMessage("InsufficientFunds"), displayAmount));
		}
		balance = players.get(player.getUniqueId()).withdraw(amount);
		if (plugin.getLogTransactions()) {
			plugin.getLogger().info(String.format("Withdraw Player: %s %s%12.2f", player.getName(), plugin.getCurrencySymbol(), amount));
		}
		return new EconomyResponse(amount, balance, ResponseType.SUCCESS, String.format(plugin.getMessage("Withdraw"), displayAmount));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		return withdrawPlayer(Utils.getPlayerByName(playerName), amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(java.lang.String, java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(Utils.getPlayerByName(playerName), amount);
	}

	public void savePlayers() {
		plugin.getLogger().fine("Saving modified player accounts:");
		Set<UUID> keys = players.keySet();
		synchronized (players) {
			for(Iterator<UUID> uid = keys.iterator(); uid.hasNext();) {
				Player player = players.get(uid.next());
				if (player.isModified()) {
					player.save();
				}
			}
		}
		
	}

	public void updateBalanceTop() {
		sortedBalances.clear(); 
		totalEconomy = 0.0;
		synchronized(players) {
			for(UUID playerId : players.keySet()) {
				if (players.get(playerId).getWorth() > 0 && !plugin.getServer().getOfflinePlayer(playerId).isBanned()) {
					sortedBalances.put(playerId, players.get(playerId));
					totalEconomy += players.get(playerId).getWorth();
				}
			}
		}
	}

	public double getTotalEconomy() {
		return totalEconomy;
	}

	/**
	 * @return the sortedBalances
	 */
	public TreeMap<UUID, Player> getSortedBalances() {
		return sortedBalances;
	}
}
