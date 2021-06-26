package me.kate.clans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import me.kate.clans.raids.WrappedFaction;

public class PlayerJoinListener implements Listener
{
	private ClansPlugin plugin;
	
	public PlayerJoinListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event)
	{
		final FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
		
		if (!player.hasFaction()) return;
		
		final WrappedFaction faction = plugin.getFactionManager().getByFaction(player.getFaction());
		
		if (faction == null) return;
		
		if (faction.isRaiding())
		{
			faction.getRaid().getRaidBar().getBossBar().addPlayer(event.getPlayer());
		}
	}
}
