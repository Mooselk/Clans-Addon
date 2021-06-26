package me.kate.clans.raids;

import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class RaidParticipants 
{
	private final Faction defenders, attacking;
	private final WrappedFaction wrappedDefender, wrappedAttacking;
	private Set<FPlayer> raiders;
	
	public RaidParticipants(WrappedFaction defenders, WrappedFaction attacking)
	{
		this.wrappedDefender = defenders;
		this.defenders = defenders.getFaction();
		
		this.wrappedAttacking = attacking;
		this.attacking = attacking.getFaction();
		
		this.raiders = new HashSet<>();
		
		raiders.addAll(defenders.getFaction().getFPlayers());
		raiders.addAll(attacking.getFaction().getFPlayers());
	}
	
	public WrappedFaction getDefendingWrapper()
	{
		return this.wrappedDefender;
	}
	
	public Faction getDefendingFaction()
	{
		return this.defenders;
	}
	
	public WrappedFaction getAttackingWrapper()
	{
		return this.wrappedAttacking;
	}
	
	public Faction getAttackingFaction()
	{
		return this.attacking;
	}
	
	public Set<FPlayer> getDefendingPlayers()
	{
		return defenders.getFPlayers();
	}
	
	public Set<FPlayer> getAttackingPlayers()
	{
		return attacking.getFPlayers();
	}
	
	public Set<FPlayer> getAllPlayers()
	{
		return raiders;
	}
	
	public void setRaiding(Raid raid)
	{
		this.wrappedDefender.setRaid(raid);
		this.wrappedAttacking.setRaid(raid);
	}
	
	public boolean isDefendingPlayer(FPlayer player)
	{
		return this.getDefendingPlayers().contains(player);
	}
	
	public boolean isAttackingPlayer(FPlayer player)
	{
		return this.getAttackingPlayers().contains(player);
	}
	
	public boolean contains(FPlayer player)
	{
		return raiders.contains(player);
	}
	
	public void sendAllMessage(String message)
	{
		raiders.forEach(raider -> raider.sendMessage(message));
	}
	
	public void sendAllTitle(String line1, String line2)
	{
		raiders.forEach(raider -> raider.getPlayer().sendTitle(line1, line2, 0, 60, 0));
	}
}
