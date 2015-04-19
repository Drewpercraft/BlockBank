package com.drewpercraft.blockbank.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.drewpercraft.blockbank.BlockBank;
import com.drewpercraft.blockbank.tasks.AnnouncementTask;

public class WorldListener implements Listener {

	private BlockBank plugin;
	private Map<String, AnnouncementTask> announcementTasks;

    public WorldListener(BlockBank plugin) {
        this.plugin = plugin;
        this.announcementTasks = new HashMap<String, AnnouncementTask>();
    }
    
    @EventHandler
    public void onWorldInit(WorldInitEvent event)
    {
    	World world = event.getWorld();
		long delay = world.getTime() % 100;
		
    	AnnouncementTask announcementTask = new AnnouncementTask(plugin, world.getName());
		announcementTask.runTaskTimer(plugin, delay, 100);
		announcementTasks.put(world.getName(), announcementTask);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event)
    {
    	World world = event.getWorld();
    	if (announcementTasks.containsKey(world.getName())) {
    		announcementTasks.get(world.getName()).cancel();
    	}
    }

}
