package me.kate.clans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import me.kate.clans.config.Messages;
import me.kate.clans.raids.Raid;

public class InventoryClickListener implements Listener
{
	private ClansPlugin plugin;
	private FPlayers players;
	private final int maxTaken;
	private int taken = 0;
	
	public InventoryClickListener(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.players = FPlayers.getInstance();
		this.maxTaken =  plugin.getPluginConfig().getMaxTakeable();
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent event)
	{
		final Player player = (Player) event.getWhoClicked();
		final FPlayer fPlayer = players.getByPlayer(player);
		
		Inventory source = event.getClickedInventory();
		InventoryView playerInv = player.getOpenInventory();
		
		if (source == null) return;
		
		InventoryType type = source.getType();
		
		if (!playerInv.getType().equals(InventoryType.CHEST)) // Not in chest inventory
			return;

		if (!plugin.getRaidManager().contains(fPlayer))
			return;
		
		Raid raid = plugin.getRaidManager().getByFPlayer(fPlayer);
		
		this.taken = raid.getItemsTaken();
		
		if (handleClick(event, type))
		{	
			if (taken != maxTaken)
			{
				if (type.equals(InventoryType.PLAYER))
					return;
				
				if (source.getItem(event.getSlot()) == null)
					return;
				
				if (event.getCurrentItem() == null)
					return;
				
				taken++;
				raid.setItemsTaken(taken);
				player.sendMessage(Messages.ITEM_TAKEN.replaceAll("%remaining%", (maxTaken - taken) + ""));
			}
			else
			{
				player.sendMessage(Messages.MAX_ITEMS_TAKEN.replaceAll("%max%", maxTaken + ""));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDrag(final InventoryDragEvent event)
	{
		final Player player = (Player) event.getWhoClicked();
		final FPlayer fPlayer = players.getByPlayer(player);
		
		Inventory source = event.getInventory();
		InventoryView playerInv = player.getOpenInventory();
		
		if (source == null) return;
		
//		if (!plugin.getRaidManager().contains(fPlayer)) // Player is not raiding
		if (plugin.getRaidManager().contains(fPlayer))
			return;
		
		if (playerInv.getType().equals(InventoryType.CHEST))
			event.setCancelled(true);
	}
	
	public boolean handleClick(InventoryClickEvent event, InventoryType type)
	{	
		switch (event.getAction())
		{
		case HOTBAR_MOVE_AND_READD:
			event.setCancelled(true);
			return false;
		
		case HOTBAR_SWAP:
			event.setCancelled(true);
			return false;
		
		case MOVE_TO_OTHER_INVENTORY:
			if (type.equals(InventoryType.PLAYER))
			{
				event.setCancelled(true);
				return false;
			}
			return true;
		
		case PLACE_ALL:
			if (type.equals(InventoryType.CHEST))
			{
				event.setCancelled(true);
				return false;
			}
			return true;
		
		case PLACE_ONE:
			if (type.equals(InventoryType.CHEST))
			{
				event.setCancelled(true);
				return false;
			}
			return true;
			
		case PLACE_SOME:
			if (type.equals(InventoryType.CHEST))
			{
				event.setCancelled(true);
				return false;
			}
			return true;
			
		case SWAP_WITH_CURSOR:
			if (type.equals(InventoryType.PLAYER))
			{
				event.setCancelled(true);
				return false;
			}
			return true;
			
		default:
			return true;
		}
	}
}
