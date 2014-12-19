package com.drewpercraft.blockbank;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import com.drewpercraft.blockbank.Player;

/*
 * Implementes the Vault Econonmy interface and contains
 * all the business logic required to support it
 */
public class VaultEconomy implements Economy {

	BlockBank plugin = null;
	Logger log = null;
	private Map<UUID, Player> players = new HashMap<UUID, Player>();
	
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
		
	}

	public Player getPlayer(UUID uniqueId) {
		if (!players.containsKey(uniqueId)) {
			players.put(uniqueId, new Player(plugin, uniqueId));
		}
		return players.get(uniqueId);
	}

	public void unloadPlayer(UUID uniqueId) {
		log.info("Unloading player " + uniqueId.toString());
		players.remove(uniqueId);
	}
	
	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#bankBalance(java.lang.String)
	 */
	@Override
	public EconomyResponse bankBalance(String arg0) 
	{
		// TODO Auto-generated method stub
		//return new EconomyResponse(0, 0, ResponseType.FAILURE, "That bank does not exist!");
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
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
	public EconomyResponse createBank(String arg0, OfflinePlayer arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createBank(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse createBank(String arg0, String arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
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
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(org.bukkit.OfflinePlayer)
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer player) 
	{
		getPlayer(player.getUniqueId()).save();
		return true;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(java.lang.String, java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName, String worldName) 
	{
		// BlockBank does not support separate accounts per world
		return createPlayerAccount(playerName);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#createPlayerAccount(java.lang.String)
	 */
	@Deprecated
	@Override
	public boolean createPlayerAccount(String playerName) 
	{
		return createPlayerAccount(plugin.getServer().getPlayer(playerName));
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#currencyNamePlural()
	 */
	@Override
	public String currencyNamePlural() 
	{
		return plugin.getConfig().getString("currencyPlural", "coins");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#currencyNameSingular()
	 */
	@Override
	public String currencyNameSingular()
	{
		return plugin.getConfig().getString("currencySingular", "coin");
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
	public EconomyResponse depositPlayer(OfflinePlayer arg0, double arg1) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer arg0, String arg1, double arg2) 
	{
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(java.lang.String, double)
	 */
	@Override
	public EconomyResponse depositPlayer(String arg0, double amount) {
		// TODO Auto-generated method stub
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
		}

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#depositPlayer(java.lang.String, java.lang.String, double)
	 */
	@Override
	public EconomyResponse depositPlayer(String arg0, String arg1, double amount) {
		// TODO Auto-generated method stub
		if (amount < 0) {
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot deposit negative funds");
		}

		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#format(double)
	 */
	@Override
	public String format(double amount) {
		String symbol = plugin.getConfig().getString("currencySymbol", "$");
		String formatString = "%s%." + fractionalDigits() + "f";
		return String.format(formatString, symbol, amount);
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#fractionalDigits()
	 */
	@Override
	public int fractionalDigits() {
		return plugin.getConfig().getInt("decimals", 0);
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
	 * @see net.milkbowl.vault.economy.Economy#getBalance(org.bukkit.OfflinePlayer)
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		Player player = players.get(offlinePlayer.getUniqueId());
		if (player instanceof Player) return player.getBalance();
		return 0.0;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(java.lang.String, java.lang.String)
	 */
	@Override
	public double getBalance(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#getBalance(java.lang.String)
	 */
	@Override
	public double getBalance(String arg0) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return "BlockBank";
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(org.bukkit.OfflinePlayer, double)
	 */
	@Override
	public boolean has(OfflinePlayer arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(java.lang.String, double)
	 */
	@Override
	public boolean has(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#has(java.lang.String, java.lang.String, double)
	 */
	@Override
	public boolean has(String arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(org.bukkit.OfflinePlayer, java.lang.String)
	 */
	@Override
	public boolean hasAccount(OfflinePlayer arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(org.bukkit.OfflinePlayer)
	 */
	@Override
	public boolean hasAccount(OfflinePlayer arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean hasAccount(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#hasAccount(java.lang.String)
	 */
	@Override
	public boolean hasAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
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
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankMember(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse isBankMember(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankOwner(java.lang.String, org.bukkit.OfflinePlayer)
	 */
	@Override
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isBankOwner(java.lang.String, java.lang.String)
	 */
	@Override
	public EconomyResponse isBankOwner(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(org.bukkit.OfflinePlayer, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, double arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(org.bukkit.OfflinePlayer, java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(String arg0, double arg1) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	/* (non-Javadoc)
	 * @see net.milkbowl.vault.economy.Economy#withdrawPlayer(java.lang.String, java.lang.String, double)
	 */
	@Override
	public EconomyResponse withdrawPlayer(String arg0, String arg1, double arg2) {
		// TODO Auto-generated method stub
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BlockBank Implementation in progress");
	}

	
}
