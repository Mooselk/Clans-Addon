package me.kate.clans.listeners.factions;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.util.LazyLocation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.kate.clans.ClansPlugin;
import me.kate.clans.config.PluginConfig;
import me.kate.clans.config.WorldTracker;
import me.kate.clans.config.WorldTracker.Callback;
import me.kate.clans.objects.Boundry;
import me.kate.clans.objects.CoreBlock;
import me.kate.clans.objects.DoubleBound;
import me.kate.clans.raids.WrappedFaction;
import me.kate.clans.utils.Cuboid;
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
		
		plugin.getDatabase().addFaction(facId);
		
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
		
		track.calculatePoints(new Callback()
		{
			@Override
			public void onComplete(Location next) 
			{
				int index = 1;
				
				faction.setCenter(new LazyLocation(next));
				player.sendMessage(ChatColor.GRAY + "Generating clans base...");
				
				BlockVector3[] vecArray = plugin.getWEHook().paste(plugin.getPluginConfig().getSchematic(), next);
				
				for (DoubleBound bound : config.getBoundries())
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
				
				ProtectedRegion region = new ProtectedCuboidRegion(facId + "-protect", false, 
						Boundry.translateVector3(
								vecArray[0], 
								next.getBlockX(),
								0,
								next.getBlockZ()),
						Boundry.translateVector3(
								vecArray[1], 
								next.getBlockX(),
								256,
								next.getBlockZ()));
				
				region.setPriority(1000);
				region.setFlag(Flags.TNT, StateFlag.State.DENY);
				region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
				region.setFlag(Flags.BUILD, StateFlag.State.DENY);
				region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
				
				final World clansWorld = plugin.getClansWorld();
				
				final Cuboid cube = new Cuboid(
						new Location(clansWorld, 
								region.getMinimumPoint().getX(),
								region.getMinimumPoint().getY(), 
								region.getMinimumPoint().getZ()), 
						new Location(clansWorld, 
								region.getMaximumPoint().getX(), 
								region.getMaximumPoint().getY(), 
								region.getMaximumPoint().getZ()));
				
				cube.getChunks().forEach(chunk -> fplayer.attemptClaim(
						faction, 
						new Location(
						clansWorld, 
						chunk.getX() * 16, // multiply by 16 to get corner of the chunk
						72, 
						chunk.getZ() * 16), // multiply by 16 to get corner of the chunk
						false));
				
				block.setCoreBlock(plugin.getPluginConfig().getSpawnFor("coreBlock", next.clone()));
				
				Location home = plugin.getPluginConfig().getSpawnFor("home", next.clone());
				home.add(next.getBlockX() >= -1 ? 0.5 : -0.5, 0.0, next.getBlockZ() >= -1 ? 0.5 : -0.5);
				
				faction.setHome(home); // needs to be in a claim owned by the faction
				
				if (next.getBlock() == null)
					next.getBlock().setType(Material.STONE);
				
				plugin.getWGHook().getRegionManager(next.getWorld()).addRegion(region);
				plugin.getServer().getScheduler().runTaskLater(plugin,
				() ->
				{
					player.sendMessage(ChatColor.GRAY + "Teleporting you to your new home...");
					player.teleport(home);
				}, 15);
			}
		});
	}
}
