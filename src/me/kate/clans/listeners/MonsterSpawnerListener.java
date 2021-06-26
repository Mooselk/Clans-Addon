package me.kate.clans.listeners;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import me.kate.clans.ClansPlugin;

public class MonsterSpawnerListener implements Listener
{
	private ClansPlugin plugin;
	private final int min = 200;
	private final int max = 800;
	
	public MonsterSpawnerListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSpawn(SpawnerSpawnEvent event)
	{
		final CreatureSpawner spawner = event.getSpawner();
		final World world = spawner.getWorld();
		
		if (!world.getName().equals(plugin.getClansWorld().getName()))
			return;
		
		final int delay = new Random().nextInt(max - min) + min;
		
		spawner.setMaxSpawnDelay((int) (max / 1.25));
		spawner.setMinSpawnDelay((int) (min / 1.25));
		
		spawner.setDelay(delay);
		spawner.update();
	}
}
