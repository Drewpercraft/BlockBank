package com.drewpercraft.blockbank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Player {

	private final BlockBank plugin;
	private final String filePath;
	private final String filename;
	private final UUID uuid;
	private JSONObject data = new JSONObject();

	
	public Player(BlockBank plugin, UUID uuid) {
		this.plugin = plugin;
		this.uuid = uuid;
		
		this.filePath = plugin.getPlayerDataPath();
		this.filename = this.filePath + File.separator + uuid + ".json";
		load();
	}
	
	
	public File getPlayerFile()
	{
		return new File(filename);
	}
	
	public void load()
	{
		File playerFile = getPlayerFile();
		try
		{
			boolean newPlayer = playerFile.createNewFile();
			if (newPlayer) {
				data.put("uuid", uuid.toString());
				data.put("balance", 0.0);
			}else{
				plugin.getLogger().info("Loading UUID: " + uuid.toString());
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(new FileReader(playerFile));
				data = (JSONObject) obj;
			}
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } 
	}
	
	public void save()
	{
		File playerFile = getPlayerFile();
		try
		{
			playerFile.createNewFile();
			FileWriter os = new FileWriter(playerFile);
			plugin.getLogger().info("Saving " + playerFile.getAbsolutePath());
			os.write(data.toString());
			os.close();
		}
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	public double getBalance()
	{
		Double balance = (Double) data.get("balance");
		return balance.doubleValue();
	}
	
	public void setBalance(double amount)
	{
		data.put("balance", amount);
	}
}
