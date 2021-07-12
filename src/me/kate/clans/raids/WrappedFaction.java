package me.kate.clans.raids;

import java.util.List;

import org.bukkit.Chunk;

import com.massivecraft.factions.Faction;

import me.kate.clans.utils.Cuboid;

public class WrappedFaction 
{
	private Faction faction;
	private List<Chunk> chunk;
	private String id;
	private Raid raid;
	
	public WrappedFaction(Faction faction)
	{
		this.faction = faction;
		this.id = faction.getId();
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setChunks(Cuboid cube)
	{
		this.chunk = cube.getChunks();
	}
	
	public List<Chunk> getChunks()
	{
		return this.chunk;
	}
	
	public Faction getFaction()
	{
		return this.faction;
	}
	
	public void setRaid(Raid raid)
	{
		this.raid = raid;
	}
	
	public boolean isRaiding()
	{
		return this.raid != null;
	}
	
	public Raid getRaid()
	{
		return this.raid;
	}
}
