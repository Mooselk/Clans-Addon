package me.kate.clans.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kate.clans.ClansPlugin;
import me.kate.clans.objects.Boundry;
import me.kate.clans.objects.DoubleBound;

public class PluginConfig
{
	private ClansPlugin plugin;
	private File pluginFile;
	private FileConfiguration pluginConfiguration;
	
	public PluginConfig(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void create()
	{
		pluginFile = new File(plugin.getDataFolder(), "config.yml");
			
		if (!pluginFile.exists()) 
		{
			pluginFile.getParentFile().mkdirs();
			plugin.saveResource("config.yml", false);
			plugin.getLogger().info("Creating config.yml...");
		}
			
		pluginConfiguration = new YamlConfiguration();
			
		try 
		{
			pluginConfiguration.load(pluginFile);
				
		} 
		catch (IOException | InvalidConfigurationException e) 
		{
			plugin.getLogger().severe("Failed to create config.yml!");
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() 
	{
		return pluginConfiguration;
	}
	
	public Location getSpawnFor(String spawn, Location paste)
	{
		String coords = getConfig().getString("settings.spawns." + spawn).replaceAll("\\s", "");
		
		String[] split = coords.split(",");
		
		Location loc = Boundry.translateLocation(
				Integer.valueOf(split[0]), 
				Integer.valueOf(split[1]), 
				Integer.valueOf(split[2]), 
				paste);
		
		return loc;
	}

	public DoubleBound getMainBound()
	{
		final String pointAx1 = getConfig().getString("settings.mainBoundry.pointA.x");
		final String pointAz1 = getConfig().getString("settings.mainBoundry.pointA.z");
		
		final Boundry boundryA = new Boundry(pointAx1, pointAz1);
		
		final String pointAx2 = getConfig().getString("settings.mainBoundry.pointB.x");
		final String pointAz2 = getConfig().getString("settings.mainBoundry.pointB.z");
		
		final Boundry boundryB = new Boundry(pointAx2, pointAz2);
		
		return new DoubleBound(boundryA, boundryB);
	}
	
	public List<DoubleBound> getSubBoundries()
	{
		final List<DoubleBound> miniBounds = new ArrayList<>();
		
		for (String key : getConfig().getConfigurationSection("settings.miniBoundries").getKeys(false))
		{
			ConfigurationSection section = getConfig().getConfigurationSection("settings.miniBoundries" + key);
			
			String x1 = section.getString("x1");
			String z1 = section.getString("z1");
			
			final Boundry boundA = new Boundry(x1, z1);
			
			String x2 = section.getString("x2");
			String z2 = section.getString("z2");
			
			final Boundry boundB = new Boundry(x2, z2);
			
			miniBounds.add(new DoubleBound(boundA, boundB));
		}
		
		return miniBounds;
	}
	
//	public List<DoubleBound> getBoundries()
//	{
//		final List<DoubleBound> bounds = new ArrayList<>();
//		
//		for (String strBound : getConfig().getStringList("settings.boundries"))
//		{
//			String[] splitDBounds = strBound.replaceAll("\\s", "").split(",");
//			
//			bounds.add(new DoubleBound(
//					new Boundry(
//							splitDBounds[0], 
//							splitDBounds[1]), 
//					new Boundry(
//							splitDBounds[2], 
//							splitDBounds[3]))
//					);
//		}
//		return bounds;
//	}
	
	public Location getServerSpawn()
	{
		String[] spawn = pluginConfiguration.getString("settings.serverSpawn").split(",");
		
		if (spawn.length < 4)
		{
			Bukkit.getLogger().info(" ");
			Bukkit.getLogger().severe("CONFIG ERROR: INVALID WORLD SPAWN, DEFAULTING TO (0, 80, 0) SPAWN COORDS");
			Bukkit.getLogger().info(" ");
			return new Location((World) Bukkit.getWorlds().toArray()[0], 0, 80, 0);
		}		
		
		return new Location(Bukkit.getWorld(spawn[0]), Double.valueOf(spawn[1]), Double.valueOf(spawn[2]), Double.valueOf(spawn[3]));
	}
	
	public File getSchematic()
	{
		return new File(plugin.getDataFolder() + "/" + getConfig().getString("settings.schematic"));
	}
	
	public String getWorldName()
	{
		return pluginConfiguration.getString("settings.worldName");
	}
	
	public int getMaxBreakable()
	{
		return pluginConfiguration.getInt("settings.raid.maxBrokenSpawners");
	}
	
	public int getMaxTakeable()
	{
		return pluginConfiguration.getInt("settings.raid.maxItemsTaken");
	}
	
	public int getPrepTime()
	{
		return pluginConfiguration.getInt("settings.raid.prepTime");
	}
	
	public int getRaidTime()
	{
		return pluginConfiguration.getInt("settings.raid.raidTime");
	}
	
	public List<String> getBlockedCommands()
	{
		return pluginConfiguration.getStringList("settings.blockedCommands");
	}
	
	public int getShieldExpireTime()
	{
		return pluginConfiguration.getInt("settings.shieldExpire");
	}
	
	public int getPointPercent()
	{
		return pluginConfiguration.getInt("settings.raid.pointPercentage");
	}
	
	public int getClaimRadius()
	{
		return pluginConfiguration.getInt("settings.baseClaimRadius");
	}
}
