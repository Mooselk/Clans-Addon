package me.kate.clans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClanRaidStartEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList() 
    {
        return handlers;
    }

}
