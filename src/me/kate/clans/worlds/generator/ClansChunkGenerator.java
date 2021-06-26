package me.kate.clans.worlds.generator;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

public class ClansChunkGenerator extends ChunkGenerator
{
    private BlockData[] layerBlock;
    private int[] layerHeight;

    public ClansChunkGenerator()
    {
        this("");
    }

    public ClansChunkGenerator(String id) 
    {
        layerBlock = new BlockData[0];
        layerHeight = new int[0];
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) 
    {
    	ChunkData chunk = createChunkData(world);

        int y = 0;
        
        for (int i = 0; i < layerBlock.length; i++)
        {
            chunk.setRegion(0, y, 0, 16, y + layerHeight[i], 16, layerBlock[i]);
            y += layerHeight[i];
        }

        return chunk;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) 
    {
        if (!world.isChunkLoaded(0, 0))
        {
            world.loadChunk(0, 0);
        }

        int highestBlock = world.getHighestBlockYAt(0, 0);

        if ((highestBlock <= 0) && (world.getBlockAt(0, 0, 0).getType() == Material.AIR)) // SPACE!
        {
            return new Location(world, 0, 72, 0); 
        }

        return new Location(world, 0, highestBlock, 0);
    }
}