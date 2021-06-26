package me.kate.clans.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.kate.clans.ClansPlugin;
import me.kate.clans.calc.PointCalculation;
import me.kate.clans.calc.PointCallback;
import me.kate.clans.objects.PageReader;
import me.kate.clans.objects.TopClan;
import net.md_5.bungee.api.ChatColor;

public class TopCommand implements CommandExecutor
{
	private ClansPlugin plugin;
	private PageReader reader;
	
	public TopCommand(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.reader = new PageReader(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) 
	{
		if (plugin.getClansTop().isUpdating())
		{
			sender.sendMessage("Clans top is currently updating, check again in a few seconds!");
			return true;
		}
		
		final List<TopClan> clans = plugin.getClansTop().getTopClans();
		
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPluginConfig().getConfig().getString("settings.clanstop.title")));
		sender.sendMessage(" ");
		
		if (args.length < 1)
		{
			reader.showPage("1", clans, clans.size() < 10, sender);
		}
		else if (args.length >= 1)
		{
			if (args[0].equalsIgnoreCase("forceupdate"))
			{
				sender.sendMessage("Starting point calculation");
				
				final PointCalculation calc = new PointCalculation(plugin);
				
				calc.start(new PointCallback() 
				{
					@Override
					public void onComplete(List<TopClan> clans) 
					{
						Bukkit.broadcastMessage("Finished updating clanstop!");
					}
				});
				
				return true;
			}
			
			reader.showPage(args[0], clans, clans.size() < 10, sender);
		}
		
		return true;
	}
}
