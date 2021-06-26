package me.kate.clans.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboid {

	private final int xMin;
	private final int xMax;
	private final int yMin;
	private final int yMax;
	private final int zMin;
	private final int zMax;
	private final World world;

	public Cuboid(final Location point1, final Location point2) {
		this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
		this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
		this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
		this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
		this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
		this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
		this.world = point1.getWorld();
	}

	// Changed from iterator, will probably throw an error
	public ArrayList<Block> blockList() 
	{
		final ArrayList<Block> bL = new ArrayList<>(this.getTotalBlockSize());
		for (int x = this.xMin; x <= this.xMax; ++x) 
		{
			for (int y = this.yMin; y <= this.yMax; ++y) 
			{
				for (int z = this.zMin; z <= this.zMax; ++z)
				{
					final Block block = this.world.getBlockAt(x, y, z);
					bL.add(block);
				}
			}
		}
		return bL;
	}

	public List<Chunk> getChunks()
	{
		ArrayList<Block> blocks = blockList();
		List<Chunk> chunks = new ArrayList<>();
		
		for (Block block : blocks)
		{
			Chunk chunk = block.getChunk();
			
			if (chunks.contains(chunk))
				continue;
			
			chunks.add(chunk);
		}
		
		return chunks;
	}
	
	public Location getCenter() 
	{
		return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin,
				(this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
	}

	public int getHeight()
	{
		return this.yMax - this.yMin + 1;
	}

	public int getTotalBlockSize() 
	{
		return this.getHeight() * this.getXWidth() * this.getZWidth();
	}

	public int getXWidth() 
	{
		return this.xMax - this.xMin + 1;
	}

	public int getZWidth()
	{
		return this.zMax - this.zMin + 1;
	}
}