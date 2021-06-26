package me.kate.clans.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Role;

import me.kate.clans.ClansPlugin;
import me.kate.clans.SignParser;
import me.kate.clans.raids.Raid;
import net.md_5.bungee.api.ChatColor;

public class BlockBreakListener implements Listener
{
	private final ClansPlugin plugin;
	
	public BlockBreakListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event)
	{
		FPlayer player = FPlayers.getInstance().getByPlayer(event.getPlayer());
		
		this.handleCoreBreak(event, player);
		
		this.handleSignBreak(event, player);
	}
	
	private void handleCoreBreak(final BlockBreakEvent event, final FPlayer player)
	{
		final Block block = event.getBlock();
		
		if (block.getType() != Material.BEACON) return;
		
		TileState state = (TileState) event.getBlock().getState();
		PersistentDataContainer container = state.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "clans-core");
		
		if (!container.has(key, PersistentDataType.STRING)) return;
		
		if (!plugin.getFactionManager().getByFaction(player.getFaction()).isRaiding() && 
				container.has(key, PersistentDataType.STRING)) 
		{
			player.sendMessage(ChatColor.RED + "You can't break your own core!");
			event.setCancelled(true);
			return;
		}
		
		if (player.getFaction().getId().equals(container.get(key, PersistentDataType.STRING)))
		{
			player.sendMessage(ChatColor.RED + "You can't break your own core!");
			event.setCancelled(true);
			return;
		}
		
		final Raid raid = plugin.getRaidManager().getByFPlayer(player);
		
		if (raid == null) return;
		
		raid.getParticipants().getAttackingPlayers().forEach(players -> players.sendMessage("Your faction has been awarded %points% points for breaking the enemies core!"));
		raid.getParticipants().getDefendingPlayers().forEach(players -> players.sendMessage("Your faction has lost %points% points for losing your core!"));
	}
	
	private void handleSignBreak(final BlockBreakEvent event, FPlayer player)
	{
		Block block = event.getBlock();
		
		if (block.getBlockData() instanceof WallSign)
		{
			SignParser parser = new SignParser(block);
			
			if (!parser.isValid())
				return;
			
			if (parser.signHasRole())
			{
				if (parser.getSignRole().isAtLeast(player.getRole()))
					return;
				
				if (!player.hasFaction())
				{
					player.sendMessage("This sign requires admin or higher to break!");
					event.setCancelled(true);
				}
				
				if (!player.getRole().isAtLeast(Role.ADMIN))
				{
					player.sendMessage("This sign requires admin or higher to break!");
					event.setCancelled(true);
				}
			}
			else if (parser.signHasName())
			{
				if (!parser.getSignName().equalsIgnoreCase(player.getName()))
				{
					
					if (player.getRole().isAtLeast(Role.ADMIN))
						return;
					
					player.sendMessage("This sign can only be broken by " + parser.getSignName() + " or a faction admin!");
					event.setCancelled(true);
				}
			}
		}
	}
	
//	private void handleSpawnerBreak(final BlockBreakEvent event, final FPlayer player)
//	{
//		if (!player.hasFaction()) return;
//		
//		final WrappedFaction faction = plugin.getFactionManager().getByFaction(player.getFaction());
//		
//		if (faction == null) return;
//		if (!faction.isRaiding()) return;
//		
//		final Raid raid = faction.getRaid();
//		final RaidParticipants parts = raid.getParticipants();
//		
//		int broken = raid.getSpawnersBroken();
//		int max = raid.getMaxSpawnersBroken();
//		
//		if (parts.isDefendingPlayer(player))
//		{
//			player.sendMessage(Messages.SPAWNER_BREAK_RAIDED);
//			event.setCancelled(true);
//		} 
//		else if (parts.isAttackingPlayer(player))
//		{
//			if (broken == max)
//			{
//				player.sendMessage(Messages.SPAWNER_BREAK_MAX
//						.replace("%broken%", broken + "")
//						.replace("%max%", max + ""));
//				event.setCancelled(true);
//				return;
//			}
//			
//			raid.setSpawnersBroken(broken++);
//			
//			player.sendMessage(Messages.SPAWNER_BROKEN
//					.replace("%broken%", broken + "")
//					.replace("%max%", max + ""));
//		}
//	}
}
