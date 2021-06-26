package me.kate.clans.raids;

import com.massivecraft.factions.Faction;

public class WrappedFaction 
{
	private Faction faction;
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
