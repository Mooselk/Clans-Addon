package me.kate.clans.calc;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;

import me.kate.clans.ClansPlugin;
import me.kate.clans.objects.TopClan;

public class PointCalculation 
{
	private ClansPlugin plugin;
	
	public PointCalculation(ClansPlugin plugin)			
	{
		this.plugin = plugin;
	}
	
	public void start(PointCallback callback)
	{	
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			plugin.getClansTop().setUpdating(true);
			
			final List<TopClan> dbresults = plugin.getDatabase().getTopClans();

			Collections.sort(dbresults, (d1, d2) -> 
			{
				return d2.getPoints() - d1.getPoints();
			});
			
			for (int i = 0; i < dbresults.size(); i++)
			{
				dbresults.get(i).setRanking(i + 1);
			}
			
			plugin.getClansTop().setTopClans(dbresults);
			
			Bukkit.getScheduler().runTask(plugin, () -> callback.onComplete(dbresults));
			
			plugin.getClansTop().setUpdating(false);
		});
	}
}
