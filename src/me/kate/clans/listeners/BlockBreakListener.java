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
import me.kate.clans.config.Messages;
import me.kate.clans.raids.Raid;

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
			player.sendMessage(Messages.CORE_BREAK_OWN);
			event.setCancelled(true);
			return;
		}
		
		if (player.getFaction().getId().equals(container.get(key, PersistentDataType.STRING)))
		{
			player.sendMessage(Messages.CORE_BREAK_OWN);
			event.setCancelled(true);
			return;
		}
		
		final Raid raid = plugin.getRaidManager().getByFPlayer(player);
		
		if (raid == null) return;
		/**
		 * TODO
		 */
		raid.getParticipants().getAttackingPlayers().forEach(players -> players.sendMessage("Your faction has been awarded %points% points for breaking the enemies core!"));
		raid.getParticipants().getDefendingPlayers().forEach(players -> players.sendMessage("Your faction has lost %points% points for losing your core!"));
	}
	
	private void handleSignBreak(final BlockBreakEvent event, FPlayer player)
	{
		Block block = event.getBlock();
		
		if (block.getBlockData() instanceof WallSign)
		{
			final SignParser parser = new SignParser(block);
			
			if (!parser.isValid())
				return;
			
			if (parser.signHasRole())
			{
				if (player.getRole().isAtLeast(parser.getSignRole()))
					return;
				
				if (!player.hasFaction())
				{
					player.sendMessage(Messages.SIGN_BREAK_NO_PERMISSION.replaceAll("%role%", parser.getSignName()));
					event.setCancelled(true);
				}
				
				if (!player.getRole().isAtLeast(parser.getSignRole()))
				{
					player.sendMessage(Messages.SIGN_BREAK_NO_PERMISSION.replaceAll("%role%", parser.getSignRole().toString()));
					event.setCancelled(true);
				}
			}
			else if (parser.signHasName())
			{
				if (!parser.getSignName().equalsIgnoreCase(player.getName()))
				{
					if (player.getRole().isAtLeast(Role.COLEADER))
						return;
					
					player.sendMessage(Messages.SIGN_BREAK_NO_PLAYER_PERMISSION.replaceAll("%name%", parser.getSignName()));
					event.setCancelled(true);
				}
			}
		}
	}
}
