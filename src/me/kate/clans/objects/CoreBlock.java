package me.kate.clans.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.kate.clans.ClansPlugin;

public class CoreBlock 
{
	private Block block;
	private String factionOwner;
	private ClansPlugin plugin;
	
	public CoreBlock(ClansPlugin plugin, String factionOwner)
	{
		this.factionOwner = factionOwner;
		this.plugin = plugin;
	}
	
	public CoreBlock(ClansPlugin plugin, Block block, String factionOwner)
	{
		this.block = block;
		this.factionOwner = factionOwner;
		this.plugin = plugin;
	}
	
	public Block getBlock()
	{
		return this.block;
	}
	
	public String getFactionId()
	{
		return this.factionOwner;
	}
	
	public void setCoreBlock(Location loc)
	{
		Block b = loc.getBlock();
		b.setType(Material.BEACON);
		
		TileState state = (TileState) b.getState();
		PersistentDataContainer container = state.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "clans-core");
		
		container.set(key, PersistentDataType.STRING, factionOwner);
		state.update();
		
		Bukkit.broadcastMessage("x: " + loc.getX() + "Y: " + loc.getY() + "z: " + loc.getZ());
	}
}