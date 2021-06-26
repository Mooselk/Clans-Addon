package me.kate.clans.timer;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import me.kate.clans.ClansPlugin;
import me.kate.clans.events.ShieldExpireEvent;

public class ShieldTimer 
{
	private ClansPlugin plugin;
	
	public ShieldTimer(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}

	public long getShieldExpire(int hours)
	{
		final Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(Date.from(Instant.now()));
	    calendar.add(Calendar.HOUR_OF_DAY, hours);
	    
	    return calendar.getTimeInMillis();
	}
	
	public void startShieldCheckTask()
	{
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () ->
		{
			for (Faction fac : Factions.getInstance().getAllFactions())
			{
				if (fac.getShieldExpire() == 0)
					continue;
				
				if (fac.getShieldExpire() > System.currentTimeMillis())
				{
					Bukkit.getScheduler().runTask(plugin, () -> 
					Bukkit.getServer().getPluginManager().callEvent(new ShieldExpireEvent(fac)));
				}
			}
		}, 0, 5);
	}
}
