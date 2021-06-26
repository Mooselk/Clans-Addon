package me.kate.clans.objects;

import me.kate.clans.ClansPlugin;
import net.md_5.bungee.api.ChatColor;

public class Formatter 
{
	private ClansPlugin plugin;
	
	public Formatter(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public String formatRankings(TopClan top)
	{
		return ChatColor.translateAlternateColorCodes('&', 
				plugin.getPluginConfig().getConfig()
				.getString("settings.clanstop.format")
				.replaceAll("%ranking%", top.getRanking() + "")
				.replaceAll("%clan_name%", top.getTag())
				.replaceAll("%points%", top.getPoints() + ""));
	}
}
