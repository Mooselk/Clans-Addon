package me.kate.clans.listeners.factions;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.SpiralTask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.kate.clans.ClansPlugin;
import me.kate.clans.WorldTracker;
import me.kate.clans.WorldTracker.Callback;
import me.kate.clans.config.PluginConfig;
import me.kate.clans.objects.Boundry;
import me.kate.clans.objects.CoreBlock;
import me.kate.clans.objects.DoubleBound;
import me.kate.clans.raids.WrappedFaction;
import net.md_5.bungee.api.ChatColor;

public class FactionCreateListener implements Listener
{
	private ClansPlugin plugin;
	private WorldTracker track;
	private PluginConfig config;
	private File schematic;
	
	public FactionCreateListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.track = plugin.getWorldTracker();
		this.config = plugin.getPluginConfig();
		this.schematic = config.getSchematic();
	}
	
	@EventHandler
	public void onFactionCreate(final FactionCreateEvent event)
	{
		final Faction faction = event.getFaction();
		final String facId = faction.getId();
		final Player player = event.getFPlayer().getPlayer();
		
		if (schematic == null)
		{
			player.sendMessage(ChatColor.RED + "A base schematic has not been set by the server owner!");
			player.sendMessage(ChatColor.RED + "You will have to disband and recreate your faction to rectify this error once fixed.");
			
			return;
		}
		
		plugin.getDatabase().addFaction(facId, 1000);
		
		plugin.getFactionManager().add(new WrappedFaction(event.getFaction()));
		
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.sendMessage(ChatColor.GRAY + "Starting base building process..."), 3);
		
		plugin.getServer().getScheduler().runTaskLater(plugin, () ->
		{
			player.sendMessage(ChatColor.GRAY + "Finding suitable location...");
			
			this.findSuitibleLocation(player, faction, facId);
		}, 30);
	}
	
	public void findSuitibleLocation(
			Player player, 
			Faction faction, 
			String facId
			)
	{
		final CoreBlock block = new CoreBlock(plugin, facId);
		final FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
		final int radius = plugin.getPluginConfig().getClaimRadius();
		
		track.calculatePoints(new Callback()
		{
			@Override
			public void onComplete(Location next) 
			{
				player.sendMessage(ChatColor.GRAY + "Generating clans base...");
				
				ProtectedRegion region = createProtectedRegion(facId, next);
				claimRadiusRegion(next, radius, fplayer, faction);
				createBuildRegions(facId, next);
				
				final Location home = getHomeLocation(next);
			
				faction.setHome(home);
				faction.setCenter(new LazyLocation(next));
				
				faction.setCorners(
						new LazyLocation(Boundry.getLocation(region.getMinimumPoint())), 
						new LazyLocation(Boundry.getLocation(region.getMaximumPoint())));
				
				block.setCoreBlock(plugin.getPluginConfig().getSpawnFor("coreBlock", next.clone()));
				
				if (next.getBlock() == null) next.getBlock().setType(Material.STONE);
				
				plugin.getServer().getScheduler().runTaskLater(plugin, () ->
				{
					player.sendMessage(ChatColor.GRAY + "Teleporting you to your new home...");
					player.teleport(home);
				}, 15);
			}
		});
	}
	
	private void claimRadiusRegion(final Location location, final int radius, final FPlayer fPlayer, final Faction faction)
	{
		new SpiralTask(new FLocation(location), radius) 
		{
            private int failCount = 0;
            private final int limit = FactionsPlugin.getInstance().conf().factions().claims().getRadiusClaimFailureLimit() - 1;

            @Override
            public boolean work() 
            {
                boolean success = fPlayer.attemptClansClaim(faction, new FLocation(this.currentLocation()), true, true);
                if (success) 
                {
                    failCount = 0;
                } 
                else if (failCount++ >= limit)
                {
                    this.stop();
                    return false;
                }
                return true;
            }
        };
	}
	
	private ProtectedRegion createProtectedRegion(String facId, Location next)
	{
		plugin.getWEHook().paste(plugin.getPluginConfig().getSchematic(), next);
		DoubleBound bound = plugin.getPluginConfig().getMainBound();
		
		ProtectedRegion region = new ProtectedCuboidRegion(facId + "-protect", false, 
				BlockVector3.at(
						bound.getBoundA().translate(
								next.getBlockX(), 
								next.getBlockZ()).getX(), 0, 
						
						bound.getBoundA().translate(
								next.getBlockX(), 
								next.getBlockZ()).getZ()),
				
				BlockVector3.at(
						bound.getBoundB().translate(
								next.getBlockX(), 
								next.getBlockZ()).getX(), 256, 
						
						bound.getBoundB().translate(
								next.getBlockX(), 
								next.getBlockZ()).getZ()));
		
		region.setPriority(1000);
		region.setFlag(Flags.TNT, StateFlag.State.DENY);
		region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
		region.setFlag(Flags.BUILD, StateFlag.State.DENY);
		region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
		
		plugin.getWGHook().getRegionManager(next.getWorld()).addRegion(region);
		
		return region;
	}
	
	public void createBuildRegions(String facId, Location next)
	{
		int index = 1;
		
		for (DoubleBound bound : config.getSubBoundries())
		{
			final String id = facId + "-build-" + index++;
			
			ProtectedRegion region = new ProtectedCuboidRegion(id, false, 
					BlockVector3.at(
							bound.getBoundA().translate(next.getBlockX(), next.getBlockZ()).getX(), 0, 
							bound.getBoundA().translate(next.getBlockX(), next.getBlockZ()).getZ()), 
					BlockVector3.at(
							bound.getBoundB().translate(next.getBlockX(), next.getBlockZ()).getX(), 256, 
							bound.getBoundB().translate(next.getBlockX(), next.getBlockZ()).getZ()));
			
			region.setPriority(10000);
			region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
			region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
			region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
			region.setFlag(Flags.TNT, StateFlag.State.DENY);
			
			plugin.getWGHook().getRegionManager(next.getWorld()).addRegion(region);
		}
	}
	
	private Location getHomeLocation(Location next)
	{
		final Location home = plugin.getPluginConfig().getSpawnFor("home", next.clone());
		
		return home.add(next.getBlockX() >= -1 ? 0.5 : -0.5, 0.0, next.getBlockZ() >= -1 ? 0.5 : -0.5);
	}
}
