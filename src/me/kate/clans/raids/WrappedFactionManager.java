package me.kate.clans.raids;

import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.Faction;

public class WrappedFactionManager 
{
	private final Set<WrappedFaction> wrappedPlayers = new HashSet<>();
	
	public Set<WrappedFaction> getWrappedFactions()
	{
		return this.wrappedPlayers;
	}
	
	public WrappedFaction getByFaction(Faction faction)
	{
		for (WrappedFaction fac : this.wrappedPlayers)
			if (fac.getFaction().equals(faction))
				return fac;
		
		return null;
	}
	
	public void add(WrappedFaction wrapper)
	{
		this.wrappedPlayers.add(wrapper);
	}
	
	public boolean remove(WrappedFaction wrapper)
	{
		return this.wrappedPlayers.remove(wrapper);
	}
}
