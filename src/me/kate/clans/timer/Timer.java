package me.kate.clans.timer;

import org.bukkit.scheduler.BukkitTask;

import me.kate.clans.ClansPlugin;

public class Timer 
{
	private final ClansPlugin plugin;
	private final int seconds;
	private BukkitTask task;
	
	public Timer(final ClansPlugin plugin, final int seconds)
	{
		this.plugin = plugin;
		this.seconds = seconds;
	}
	
	public BukkitTask getTask() 
	{
		return this.task;
	}
	
	public void start(final TimerCallback callback)
	{	
		int countdown[] = {seconds+1};
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> 
		{
			countdown[0]--;
			
			if (countdown[0] == 1) 
			{
				plugin.getServer().getScheduler().runTaskLater(plugin, () -> callback.onComplete(), 20);
				task.cancel();
			}
		}, 5, 20);
	}
	
	public void start(final TimerCallback callback, final TimerCountCallback countCall)
	{	
		int countdown[] = {seconds+1};
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> 
		{
			countdown[0]--;
			
			if (countdown[0] != 0)
				plugin.getServer().getScheduler().runTask(plugin, () -> countCall.onCount(countdown[0]));
			
			if (countdown[0] == 1) 
			{
				plugin.getServer().getScheduler().runTaskLater(plugin, () -> callback.onComplete(), 20);
				task.cancel();
			}
		}, 5, 20);
	}
	
	public void stop()
	{
		task.cancel();
	}
}
