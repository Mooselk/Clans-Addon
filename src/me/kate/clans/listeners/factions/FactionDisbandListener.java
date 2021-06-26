package me.kate.clans.listeners.factions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.kate.clans.ClansPlugin;

public class FactionDisbandListener implements Listener
{
	private ClansPlugin plugin;
	
	public FactionDisbandListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onFactionDisband(final FactionDisbandEvent event)
	{
		final Faction faction = event.getFaction();
		final String id = faction.getId();
		
		plugin.getDatabase().removeFaction(id);
		plugin.getFactionManager().remove(
		plugin.getFactionManager().getByFaction(faction));
		
		faction.getFPlayers().forEach(player -> player.getPlayer().teleport(plugin.getPluginConfig().getServerSpawn()));
		
		RegionManager rManager = plugin.getWGHook().getRegionManager(plugin.getClansWorld());
		
		rManager.getRegions().values().forEach(region -> deleteRegion(id, region, rManager));
	}
	
	private void deleteRegion(String factionId, ProtectedRegion region, RegionManager manager)
	{		
		if (region.getId().contains(factionId + "-build-"))
		{
			manager.removeRegion(region.getId());
		}
		else if (region.getId().equals(factionId + "-protect"))
		{
			manager.removeRegion(factionId + "-protect");
		}
	}
}