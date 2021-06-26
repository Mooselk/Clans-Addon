package me.kate.clans.raids;

import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.FPlayer;

public class RaidManager 
{
	private final Set<Raid> activeRaids = new HashSet<>();
	
	public Set<Raid> getActiveRaids()
	{
		return this.activeRaids;
	}
	
	public boolean add(Raid raid)
	{
		return this.activeRaids.add(raid);
	}
	
	public boolean remove(Raid raid)
	{
		return this.activeRaids.remove(raid);
	}
	
	public boolean contains(FPlayer player)
	{
		Set<FPlayer> raiders = new HashSet<>();
		boolean containsPlayer = false;
		
		for (Raid raid : activeRaids)
		{
			raiders.addAll(raid.getParticipants().getDefendingPlayers());
			raiders.addAll(raid.getParticipants().getAttackingPlayers());
			
			if (raiders.contains(player))
			{
				containsPlayer = true;
				raiders.clear();
				break;
			}
		}
		
		return containsPlayer;
	}
	
	public Raid getByFPlayer(FPlayer player)
	{
		for (Raid raid : activeRaids)
		{
			if (raid.getParticipants().getAttackingPlayers().contains(player))
			{
				return raid;
			}
		}
		
		return null;
	}
}
