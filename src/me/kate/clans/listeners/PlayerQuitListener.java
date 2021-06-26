package me.kate.clans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.kate.clans.ClansPlugin;

public class PlayerQuitListener implements Listener
{
//	private ClansPlugin plugin;
	
	public PlayerQuitListener(ClansPlugin plugin)
	{
//		this.plugin = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		
	}
}
