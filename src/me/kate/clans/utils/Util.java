package me.kate.clans.utils;

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
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import me.kate.clans.ClansPlugin;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EntityShulker;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class Util 
{
	public static String trans(String toColor)
	{
		return ChatColor.translateAlternateColorCodes('&', toColor);
	}
	
	public static void sendGlowingBlock(Player p, Location loc, long lifetime){
        Bukkit.getScheduler().scheduleSyncDelayedTask(ClansPlugin.getPlugin(ClansPlugin.class), () -> 
        {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

            EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld) loc.getWorld()).getHandle());
            shulker.setLocation(loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5, 0, 0);
            shulker.setInvisible(true);
            shulker.setInvulnerable(true);
//            shulker.glowing = true;
            shulker.i(true);
//            shulker.setFlag(6, true); //Glow

            PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(shulker);
            connection.sendPacket(spawnPacket);

            Bukkit.getScheduler().scheduleSyncDelayedTask(ClansPlugin.getPlugin(ClansPlugin.class), () -> 
            {
                PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(shulker.getId());
                connection.sendPacket(destroyPacket);
            }, lifetime + (long) ((Math.random() + 1) * 100));
            
        }, (long) ((Math.random() + 1) * 40));
    }
	
	private static final BlockFace[] SIGN_ATTACHMENT_FACES = 
		{ 
			BlockFace.NORTH, 
			BlockFace.EAST,
	        BlockFace.SOUTH, 
	        BlockFace.WEST, 
	        BlockFace.UP 
		};
	
	public static Collection<Block> findAttachedSigns(Block block) 
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
	
	public static Block getNeighboringChestBlock(Block block)
	{
		Location loc = getNeighboringChestLocation(block);
		
		if (loc != null)
			return loc.getBlock();
		
		return null;
	}
	
	public static Location getNeighboringChestLocation(Block block)
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
	
	private static boolean isAttachedSign(Block signBlock, Block attachedTo) 
	{
        BlockFace requiredFace = signBlock.getFace(attachedTo);
        BlockData materialData = signBlock.getBlockData();
        BlockFace actualFace = BlockFace.DOWN;
        
        if (materialData instanceof WallSign) 
            actualFace = ((WallSign) materialData).getFacing().getOppositeFace(); 
        
        return (actualFace == requiredFace);
    }
	
	public static BlockFace getNeighboringChestBlockFace(Chest chest) 
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
