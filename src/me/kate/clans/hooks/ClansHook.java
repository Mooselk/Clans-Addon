package me.kate.clans.hooks;

import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import me.kate.clans.ClansPlugin;
import me.kate.clans.raids.WrappedFaction;

public class ClansHook 
{
	private ClansPlugin plugin;
	
	public ClansHook(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public Plugin getHook()
	{	
		final Plugin plug = plugin.getServer().getPluginManager().getPlugin("Clans");
		
		if (plug == null) 
		{
			plugin.getLogger().severe("Clans not present, disabling!");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		
		for (Faction faction : getPlugin().getAllFactions())
		{
			plugin.getFactionManager().add(new WrappedFaction(faction));
		}
		
		return plug;
	}

	public Factions getPlugin() 
	{
		return Factions.getInstance();
	}
}
