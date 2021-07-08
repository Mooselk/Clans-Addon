package me.kate.clans.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import me.kate.clans.ClansPlugin;
import me.kate.clans.utils.Cuboid;

public class EntityDeathListener implements Listener
{
	private ClansPlugin plugin;
	
	public EntityDeathListener(final ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player) return;
		
//		if (!event.getEntity().getLocation().getWorld().getName().equals(plugin.getClansWorld().getName()))
//			return;
		
		final Location entityDeathLoc = event.getEntity().getLocation();
		final Faction faction = Board.getInstance().getFactionAt(new FLocation(entityDeathLoc));
		
		if (faction == null) 
		{
			Bukkit.broadcastMessage("FACTION IS NULL");
			return;
		}
		
		if (faction.getCornerA() == null)
		{
			Bukkit.broadcastMessage("Null corner");
			return;
		}
		
		Bukkit.broadcastMessage(faction.getCornerA().toString() + " " + faction.getCornerB().toString());
		
		final Cuboid cube = new Cuboid(faction.getCornerA().getLocation(), faction.getCornerB().getLocation());
		
		if (cube.isIn(entityDeathLoc)) 
		{
			Bukkit.broadcastMessage("entity died in fac base " + faction.toString());
			
			Set<Chest> chests = this.getBaseChests(cube);
			
			this.addItems(doubleItems(event.getDrops()), getChestInventories(chests));
		}
	}
	
	
	private Set<Chest> getBaseChests(Cuboid cube)
	{
		final Set<Chest> chest = new HashSet<>();
		final Set<Chunk> chestChunks = new HashSet<>();
		
		String dataString = "minecraft:chest[waterlogged=false]";
		BlockData data = Bukkit.createBlockData(dataString);
		
		for (Chunk chunk : cube.getChunks())
			if (chunk.contains(data))
				chestChunks.add(chunk);
		
		for (Chunk chunk : chestChunks)
			for (int i = 0; i < chunk.getTileEntities().length; i++)
			{
				BlockState state = chunk.getTileEntities()[i];
				
				if (state.getBlock().getType().equals(Material.CHEST))
				{
					chest.add((Chest) state);
				}
			}
		
		return chest;
	}
	
	private List<Inventory> getChestInventories(Set<Chest> chests)
	{
		List<Inventory> inventories = new ArrayList<>();
		
		for (Chest chest : chests)
		{
			inventories.add(chest.getBlockInventory());
		}
		
		return inventories;
	}
	
	/**
	 * while our list still has items
	 * loop through each item
	 * if inv contains item type
	 * get ammount of item in inv
	 * if ammount is less than 64
	 * subtract ammount from item in modified array
	 * if ammount removed leaves 0, remove from modified array
	 * else leave for next iteration
	 * finally add items to inventory
	 * if we have remaining items, repeat with next inventory in list
	 * 
	 */
	
	private void addItems(List<ItemStack> items, List<Inventory> inv)
	{
		ItemStack[] modified = new ItemStack[items.size()];
		modified = items.toArray(modified);
		
		Inventory[] invs = new Inventory[inv.size()];
		invs = inv.toArray(invs);
		
		int iter = 0;
		
		while (modified.length != 0 && iter != invs.length)
		{
			for (int i = 0; i < modified.length; i++)
			{
				ItemStack item = modified[i];
				
				if (invs[iter].contains(item.getType()))
				{
					for (int j = 0; j < invs[iter].getContents().length; j++)
					{
						ItemStack matchedStack = invs[iter].getContents()[j];
						int matchedAmmount = matchedStack.getAmount();
						
						if (matchedStack.getType().equals(item.getType())
								&& matchedAmmount < 64)
						{
							int remaining = (item.getAmount() + matchedAmmount) - 64;
							
							if (remaining >= 1)
							{
								modified[i].setAmount(remaining);
								invs[iter].getContents()[j].setAmount(64);
							}
							else
							{
								invs[iter].getContents()[j].setAmount(
										item.getAmount() + matchedAmmount
										);
								modified = removeElement(modified, i);
							}
						}
					}
				}
			}
			
			iter++;
			
		}
	}
	
	private List<ItemStack> doubleItems(List<ItemStack> items)
	{
		List<ItemStack> doubled = new ArrayList<>();
		
		for (ItemStack item : items)
		{
			item.setAmount(item.getAmount() * 2);
			doubled.add(item);
		}
		
		return doubled;
	}
	
	private ItemStack[] removeElement(ItemStack[] arr, int index)
	{
		if (arr == null || index < 0 || index >= arr.length) 
		{

			return arr;
		}

		ItemStack[] anotherArray = new ItemStack[arr.length - 1];

		for (int i = 0, k = 0; i < arr.length; i++) {

			if (i == index)
				continue;
			
			anotherArray[k++] = arr[i];
		}

		return anotherArray;
	}
}