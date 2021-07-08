package me.kate.clans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.util.LazyLocation;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.kate.clans.ClansPlugin;
import me.kate.clans.objects.Boundry;
import me.kate.clans.raids.WrappedFaction;

public class PlayerJoinListener implements Listener
{
	private ClansPlugin plugin;
	
	public PlayerJoinListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event)
	{
		final FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
		
		if (!player.hasFaction()) return;
		
		final WrappedFaction faction = plugin.getFactionManager().getByFaction(player.getFaction());
		
		if (faction == null) return;
		
		if (faction.isRaiding())
		{
			faction.getRaid().getRaidBar().getBossBar().addPlayer(event.getPlayer());
		}
		
		String id = player.getFactionId();
		RegionManager rm = plugin.getRegionManager();
		ProtectedRegion region = rm.getRegion(id + "-protect");
		
		if (region != null)
		{			
			player.getFaction().setCorners(
					new LazyLocation(Boundry.getLocation(region.getMinimumPoint())), 
					new LazyLocation(Boundry.getLocation(region.getMaximumPoint())));
		}
		else
		{
			plugin.getLogger().info("Unable to get region for faction + " + player.getFaction().getTag());
		}
	}
}
