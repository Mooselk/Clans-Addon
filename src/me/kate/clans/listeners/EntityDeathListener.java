package me.kate.clans.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

import me.kate.clans.ClansPlugin;
import me.kate.clans.SignParser;
import me.kate.clans.raids.WrappedFaction;
import me.kate.clans.utils.Cuboid;
import me.kate.clans.utils.Util;

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
		final Entity entity = event.getEntity();
		
		if (entity instanceof Player) return;
		
		if (!entity.getWorld().getName().equals(plugin.getClansWorld().getName()))
			return;
		
		final Location entityDeathLoc = event.getEntity().getLocation();
		final Faction faction = Board.getInstance().getFactionAt(new FLocation(entityDeathLoc));
		final WrappedFaction wrapped = plugin.getFactionManager().getByFaction(faction);
		final Cuboid cube = new Cuboid(faction.getCornerA().getLocation(), faction.getCornerB().getLocation());
		
		if (wrapped.getChunks() == null)
		{
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> wrapped.getChunks());
			Bukkit.getLogger().severe("Error loading Faction " + wrapped.getFaction().getTag() + " chunks, running task async to catch up!");
			return;
		}
		
		if (cube.isIn(entityDeathLoc)) 
		{
			if (this.addItemsToChest(event.getDrops(), this.getEmptyChestInventory(wrapped.getChunks())))
			{
				event.getDrops().clear();
			}
		}
	}
	
	private boolean isFull(Inventory inv)
	{
		int numItems = 0;
		
		for (int i = 0; i < inv.getContents().length; i++)
		{
			if (inv.getContents()[i] != null)
			{
				numItems++;
			}
		}
		
		if (numItems == inv.getSize())
			return true;
		
		return false;
	}
	
	private Inventory getEmptyChestInventory(List<Chunk> chunks)
	{	
		for (Chunk chunk : chunks)
		{
			for (int i = 0; i < chunk.getTileEntities().length; i++)
			{
				BlockState state = chunk.getTileEntities()[i];
				
				if (state instanceof Chest) 
				{
				    Chest chest = (Chest) state;
				    Inventory inventory = chest.getInventory();
				    
				    if (inventory instanceof DoubleChestInventory) 
				    {
				        DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
				        
				        if (isFull(doubleChest.getInventory()))
				        	continue;
				        
				        for (Block signBlock : Util.findAttachedSigns(state.getBlock()))
				        {
				        	final SignParser parser = new SignParser(signBlock);
				        	
				        	if (parser.isMobDrops())
				        	{
				        		return doubleChest.getInventory();
				        	}
				        }
				    }
				    
				    if (isFull(inventory))
			        	continue;
				    
				    for (Block signBlock : Util.findAttachedSigns(state.getBlock()))
			        {
			        	final SignParser parser = new SignParser(signBlock);
			        	
			        	if (parser.isMobDrops())
			        	{
			        		return inventory;
			        	}
			        }
				}
			}
		}
		return null;
	}
	
	private boolean addItemsToChest(List<ItemStack> items, Inventory chest)
	{
		final List<ItemStack> doubledItems = doubleItems(items);
		final ItemStack[] itemsArray = new ItemStack[doubledItems.size()];
	
		if (chest == null)
			return false;
		
		if (isFull(chest))
			return false;
		
		chest.addItem(doubledItems.toArray(itemsArray));
		
		return true;
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
}