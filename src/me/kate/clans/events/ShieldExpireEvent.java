package me.kate.clans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Faction;

public class ShieldExpireEvent extends Event 
{
	private static final HandlerList handlers = new HandlerList();

	private Faction faction;
	
	public ShieldExpireEvent(final Faction faction)
	{
		this.faction = faction;
	}
	
	public Faction getFaction()
	{
		return this.faction;
	}
	
	public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList() 
    {
        return handlers;
    }

}
