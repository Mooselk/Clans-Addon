package me.kate.clans.worlds;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import me.kate.clans.ClansPlugin;

public class MultiverseWorldBuilder 
{
	private String worldName;
	private MultiverseCore core;
	
	public MultiverseWorldBuilder(ClansPlugin plugin, String worldName)
	{
		this.worldName = worldName;
		this.core = (MultiverseCore) plugin
				.getServer()
				.getPluginManager()
				.getPlugin("Multiverse-Core");
		
		if (core == null)
			plugin.getServer().getPluginManager().disablePlugin(plugin);
	}
	
	public enum Status
	{
		SUCCESS,
		FAILURE;
	}
	
	public Status createWorld()
	{
		if (core.getMVWorldManager().isMVWorld(worldName))
			return Status.SUCCESS; // world has already been created?
		
		if (core.getMVWorldManager()
				.addWorld(
						worldName, 
						Environment.NORMAL, 
						worldName, 
						WorldType.FLAT, 
						false, 
						"ClansAddon"
						))
			return Status.SUCCESS;
		
		return Status.FAILURE;
	}
	
	public MultiverseWorld getMVWorld()
	{
		return core.getMVWorldManager().getMVWorld(this.worldName);
	}
	
	public World getWorld()
	{
		return getMVWorld().getCBWorld();
	}
}
