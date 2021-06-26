package me.kate.clans.listeners.factions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

import me.kate.clans.config.Messages;
import me.kate.clans.events.ShieldExpireEvent;

public class ShieldExpireListener implements Listener
{
	@EventHandler
	public void onExpire(final ShieldExpireEvent event)
	{
		final Faction faction = event.getFaction();
		
		faction.setShieldExpire(0);
		
		for (FPlayer player : faction.getFPlayers())
		{
			if (player.isOffline())
				continue;
			
			player.sendMessage(Messages.SHIELD_EXPIRED);
		}
	}
}
