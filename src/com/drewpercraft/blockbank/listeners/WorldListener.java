package com.drewpercraft.blockbank.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.tasks.AnnouncementTask;

public class WorldListener implements Listener {

	private BlockBank plugin;
	private Map<String, AnnouncementTask> announcementTasks;

    public WorldListener(BlockBank plugin) {
        this.plugin = plugin;
        this.announcementTasks = new HashMap<String, AnnouncementTask>();
        //Add the worlds that have already been loaded
        for(Iterator<World> worldIT = plugin.getServer().getWorlds().iterator(); worldIT.hasNext();) {
        	addTask(worldIT.next());
        }
        plugin.getLogger().info("Listening for world events");
    }
    
    private void addTask(World world)
    {
    	if (!announcementTasks.containsKey(world.getName())) {
			long delay = world.getTime() % 100;
			plugin.getLogger().info("Creating Announcement Task for " + world.getName());
	    	AnnouncementTask announcementTask = new AnnouncementTask(plugin, world.getName());
			announcementTask.runTaskTimer(plugin, delay, 100);
			announcementTasks.put(world.getName(), announcementTask);
    	}	
    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event)
    {
    	addTask(event.getWorld());    	
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event)
    {
    	World world = event.getWorld();
    	if (announcementTasks.containsKey(world.getName())) {
    		plugin.getLogger().info("Cancelling Announcement Task for " + world.getName());
    		announcementTasks.get(world.getName()).cancel();
    	}
    }

}
