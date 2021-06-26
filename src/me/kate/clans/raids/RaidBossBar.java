package me.kate.clans.raids;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitTask;

import com.massivecraft.factions.FPlayer;

import me.kate.clans.ClansPlugin;

public class RaidBossBar 
{
	private ClansPlugin plugin;
	private Set<FPlayer> show;
	private BukkitTask task;
	private BossBar bar;
	
	public RaidBossBar(ClansPlugin plugin, Set<FPlayer> show)
	{
		this.plugin = plugin;
		this.show = show;
		this.bar = plugin.getServer().createBossBar(
				"Raiding",
				BarColor.RED, 
				BarStyle.SOLID,
				BarFlag.DARKEN_SKY);
	}
	
	public RaidBossBar create(double seconds, String timerTitle)
	{
		int timeLeft[] = {(int) seconds};
		
		this.task = Bukkit.getScheduler().runTaskTimer(plugin, () ->
		{			
			if (timeLeft[0] == 0) 
			{
                task.cancel();
                bar.removeAll();
            }
			else
			{
				bar.setProgress(timeLeft[0] / seconds);
				bar.setTitle(timerTitle.replaceAll("%timer%", convertTime(timeLeft[0])));
			}
			
			timeLeft[0]--;
		}, 0, 20);
		
		bar.setVisible(true);
		show.forEach(player -> bar.addPlayer(player.getPlayer()));
		
		return this;
	}
	
	public String convertTime(int seconds)
	{
		int sec = seconds % 60;
        int hour = seconds / 60;
        int min = hour % 60;
        hour = hour / 60;
        
        if (seconds < 60)
        {
        	return sec + "s";
        }
        
        return min + "m " + sec + "s";
	}
	
	public BossBar getBossBar()
	{
		return this.bar;
	}
	
	public BukkitTask getTask()
	{
		return this.task;
	}
}
