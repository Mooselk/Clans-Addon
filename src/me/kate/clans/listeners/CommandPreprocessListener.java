package me.kate.clans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import me.kate.clans.raids.WrappedFaction;

public class CommandPreprocessListener implements Listener
{
	private ClansPlugin plugin;
	
	public CommandPreprocessListener(final ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event)
	{
		FPlayer fplayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
		WrappedFaction wrappedFaction = plugin.getFactionManager().getByFaction(fplayer.getFaction());
		
		if (wrappedFaction == null) return;
		
		if (wrappedFaction.isRaiding() && wrappedFaction.getRaid().getParticipants().contains(fplayer))
		{
			if (plugin.getPluginConfig().getBlockedCommands().contains(event.getMessage()))
			{
				event.setCancelled(true);
			}
		}
	}
}