package me.kate.clans.hooks;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import me.kate.clans.ClansPlugin;

public class WorldGuardHook
{
	private ClansPlugin plugin;
	
	public WorldGuardHook(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public Plugin getHook()
	{	
		final Plugin plug = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (plug == null) 
		{
			plugin.getLogger().severe("WorldGuard not present, disabling!");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		
		return plug;
	}

	public WorldGuard getPlugin() 
	{
		return WorldGuard.getInstance();
	}
	
	public RegionContainer getRegionContainer()
	{
		return getPlugin().getPlatform().getRegionContainer();
	}
	
	public RegionManager getRegionManager(World world)
	{
		return getRegionContainer().get(BukkitAdapter.adapt(world));
	}
}
