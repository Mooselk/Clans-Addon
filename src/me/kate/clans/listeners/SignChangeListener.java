package me.kate.clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.kate.clans.ClansPlugin;
import me.kate.clans.SignParser;
import me.kate.clans.config.Messages;
import net.md_5.bungee.api.ChatColor;

public class SignChangeListener implements Listener
{
	private ClansPlugin plugin;
	
	public SignChangeListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(final SignChangeEvent event)
	{
		final Block block = event.getBlock();
		final Player player = event.getPlayer();
		
		Bukkit.getScheduler().runTaskLater(plugin, () ->
		{
			final SignParser parser = new SignParser(block);
			
			if (!parser.isValid())
			{
				player.sendMessage(Messages.INVALID_SIGN_ERROR.replaceAll("%error%", parser.getSign().getLines()[1]));
			}
			else
			{
				Sign sign = parser.getSign();
				
				sign.setLine(0, ChatColor.DARK_BLUE + "[Subclaim]");
				sign.update();
				
				for (int i = 0; i < sign.getLines().length; i++)
				{
					String line = sign.getLines()[i];
					
					if (line.equalsIgnoreCase("[MobDrops]") || line.contains("[MobDrops]"))
					{
						sign.setLine(i, ChatColor.GREEN + "[MobDrops]");
						sign.update();
					}
				}
			}
		}, 2);
	}
}