package me.kate.clans.raids;

import org.bukkit.Location;
import org.bukkit.Material;

import com.massivecraft.factions.util.LazyLocation;

import me.kate.clans.ClansPlugin;
import me.kate.clans.config.Messages;
import me.kate.clans.config.PluginConfig;
import me.kate.clans.database.DatabaseManager.Mode;
import me.kate.clans.objects.CoreBlock;
import me.kate.clans.timer.Timer;
import me.kate.clans.timer.TimerCallback;

public class Raid 
{
	private ClansPlugin plugin;
	private RaidParticipants parts;
	private int itemsTaken, spawnersTaken, gracePeriod, raidTime;
	private RaidBossBar raidBar;
	private PluginConfig config;
	private Raid raid;
	private boolean broken;
	
	public Raid(ClansPlugin plugin, RaidParticipants parts)
	{
		this.plugin = plugin;
		this.parts = parts;
		this.itemsTaken = 0;
		this.config = plugin.getPluginConfig();
		this.raidTime = config.getRaidTime();
		this.gracePeriod = config.getPrepTime();
		parts.setRaiding(this);
		this.raid = this;
	}
	
	public RaidParticipants getParticipants()
	{
		return this.parts;
	}

	public int getMaxSpawnersBroken()
	{
		return plugin.getPluginConfig().getMaxBreakable();
	}
	
	public void setSpawnersBroken(int taken)
	{
		this.spawnersTaken = taken;
	}
	
	public int getSpawnersBroken()
	{
		return this.spawnersTaken;
	}
	
	public void setItemsTaken(int taken)
	{
		this.itemsTaken = taken;
	}
	
	public int getItemsTaken()
	{
		return this.itemsTaken;
	}
	
	public RaidBossBar getRaidBar()
	{
		return this.raidBar;
	}
	
	public void setCoreBroken(boolean broken)
	{
		this.broken = broken;
	}
	
	public boolean hasBrokenCore()
	{
		return this.broken;
	}
	
	public void start()
	{
		final Timer graceTimer = new Timer(plugin, gracePeriod);
		
		parts.getDefendingPlayers().forEach(fplayer -> fplayer.getPlayer().sendTitle(
				Messages.GRACE_TITLE_TOP_START, 
				Messages.GRACE_TITLE_BOTTOM_START,
				0, 60, 0));
		
		raidBar = new RaidBossBar(plugin, parts.getAllPlayers()).create(gracePeriod, Messages.BOSSBAR_TIMER_GRACE);
		
		graceTimer.start(new TimerCallback() 
		{

			@Override
			public void onComplete() 
			{
				startRaidTimer();
				parts.sendAllTitle(Messages.GRACE_TITLE_TOP_END, Messages.GRACE_TITLE_BOTTOM_END);
				
				LazyLocation location = parts.getDefendingFaction().getCenter();
				
				parts.getAttackingPlayers().forEach(player -> player.getPlayer().teleport(
								plugin.getPluginConfig().getSpawnFor("raiders", new Location(
										plugin.getClansWorld(),
										location.getX(), 
										location.getY(), 
										location.getZ()))));
			}
			
		});
	}
	
	private void startRaidTimer()
	{
		final Timer raidTimer = new Timer(plugin, raidTime);
		
		raidBar = new RaidBossBar(plugin, parts.getAllPlayers()).create(raidTime, Messages.BOSSBAR_TIMER_RAID);
		
		parts.getAttackingPlayers().forEach(players -> players.getPlayer().teleport(players.getFaction().getHome()));
		
		raidTimer.start(new TimerCallback() 
		{

			@Override
			public void onComplete() 
			{	
				WrappedFaction defender = parts.getDefendingWrapper();
				CoreBlock block = new CoreBlock(plugin, defender.getId());
				LazyLocation location = defender.getFaction().getCenter();
				Location coreLocation = plugin.getPluginConfig().getSpawnFor("coreBlock", location.getLocation());
				
				int expire = plugin.getPluginConfig().getShieldExpireTime();
				long longExpire = plugin.getTimer().getShieldExpire(expire);
				
				if (coreLocation.getBlock().getType() != Material.AIR)
				{
					block.setCoreBlock(coreLocation);
				}
				else
				{
					// this might be backwards
					plugin.getDatabase().adjustPercentagePoints( 	  //
							parts.getDefendingFaction().getId(), 	  // Get defenders number of points
							parts.getAttackingFaction().getId(), 	  // Set attackers points based on percentage of defenders
							config.getPointPercent(), Mode.SUBTRACT); // Set percentage and mode

					plugin.getDatabase().adjustPercentagePoints( //
							parts.getAttackingFaction().getId(), // Get attackers number of points
							parts.getDefendingFaction().getId(), // Set defenders points based on percentage of defenders
							config.getPointPercent(), Mode.ADD); // Set percentage and mode
				}
				
				parts.setRaiding(null);
				plugin.getRaidManager().remove(raid);
				parts.getDefendingFaction().setShieldExpire(longExpire);
			}
		});
	}
	
	public void cancel()
	{
		raidBar.getTask().cancel();
		raidBar.getBossBar().removeAll();
	}
}
