package me.kate.clans.listeners;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import me.kate.clans.SignParser;
import me.kate.clans.raids.WrappedFaction;
import me.kate.clans.raids.WrappedFactionManager;

public class PlayerInteractListener implements Listener
{
//	private ClansPlugin plugin;
	private WrappedFactionManager manager;
	private FPlayers inst;
	
	public PlayerInteractListener(ClansPlugin plugin)
	{
//		this.plugin = plugin;
		this.manager = plugin.getFactionManager();
		this.inst = FPlayers.getInstance();
	}
	
	
	private static final BlockFace[] SIGN_ATTACHMENT_FACES = 
	{ 
		BlockFace.NORTH, 
		BlockFace.EAST,
        BlockFace.SOUTH, 
        BlockFace.WEST, 
        BlockFace.UP 
	};
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		FPlayer fplayer = inst.getByPlayer(player);
		
		if (!fplayer.hasFaction()) 
			return;
		
		if (block == null)
			return;
		
		if (block.getType() != Material.CHEST) 
			return;
		
		WrappedFaction faction = manager.getByFaction(fplayer.getFaction());
		
		if (faction.getRaid() != null && faction.getRaid().getParticipants().isDefendingPlayer(fplayer))
		{
			player.sendMessage("You can't access your items while being raided!");
			event.setCancelled(true);
		}	
		
		findAttachedSigns(event.getClickedBlock()).forEach(b ->
		{
			if (!(b.getBlockData() instanceof WallSign))
				return;
			
			SignParser parser = new SignParser(b);
				
			if (parser.isValid())
			{
				Bukkit.getLogger().info("Valid sign");
				Bukkit.getLogger().info("Length: " + parser.getSign().getLines().length);
				
				if (faction.isRaiding() && faction.getRaid().getParticipants().isDefendingPlayer(fplayer))
				{
					player.sendMessage("You can't open your chests while being raided!");
					event.setCancelled(true);
				}
				else return;
				
				if (parser.signHasName() && !parser.getSign().getLine(1).equals(player.getName()))
				{
					player.sendMessage("This isn't your chest to open!");
					event.setCancelled(true);
				}
			}
			
			Bukkit.getLogger().info(parser.getSign().getLines().toString());
		});
	}
	
	public Collection<Block> findAttachedSigns(Block block) 
	{
        Collection<Block> signs = new ArrayList<>();
        Block neighbor = getNeighboringChestBlock(block);
        
        for (BlockFace face : SIGN_ATTACHMENT_FACES) 
        {
            Block atPositionA = block.getRelative(face);
            Block atPositionB = null;
            
            if (neighbor != null)
            	atPositionB = neighbor.getRelative(face);
            
            if (isAttachedSign(atPositionA, block)) 
            {
            	if (!atPositionA.getType().equals(Material.AIR))
            		signs.add(atPositionA);
            }
            
            if (atPositionB == null)
            	continue;
            	
            if (isAttachedSign(atPositionB, neighbor))
            {
            	if (!atPositionB.getType().equals(Material.AIR))
            		signs.add(atPositionB);
            }
        }
        
        return signs;
    }
	
	public Block getNeighboringChestBlock(Block block)
	{
		Location loc = getNeighboringChestLocation(block);
		
		if (loc != null)
			return loc.getBlock();
		
		return null;
	}
	
	public Location getNeighboringChestLocation(Block block)
	{
		Location loc = block.getLocation();
		BlockFace face = getNeighboringChestBlockFace(
				(Chest) block
				.getState()
				.getBlockData());
		if (face == null)
			return null;
		
		int x = face.getModX();
		int y = face.getModY();
		int z = face.getModZ();
		
		return loc.add(x, y, z);
	}
	
	private boolean isAttachedSign(Block signBlock, Block attachedTo) 
	{
        BlockFace requiredFace = signBlock.getFace(attachedTo);
        BlockData materialData = signBlock.getBlockData();
        BlockFace actualFace = BlockFace.DOWN;
        
        if (materialData instanceof WallSign) 
            actualFace = ((WallSign) materialData).getFacing().getOppositeFace(); 
        
        return (actualFace == requiredFace);
    }
	
	public BlockFace getNeighboringChestBlockFace(Chest chest) 
	{
        if (chest.getType() == Chest.Type.SINGLE)
            return null;
        
        switch (chest.getFacing())
        {
            case NORTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.EAST : BlockFace.WEST;
            case SOUTH:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.WEST : BlockFace.EAST;
            case EAST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.SOUTH : BlockFace.NORTH;
            case WEST:
                return chest.getType() == Chest.Type.LEFT ? BlockFace.NORTH : BlockFace.SOUTH;
            default:
                return null;
        }
    }
}
