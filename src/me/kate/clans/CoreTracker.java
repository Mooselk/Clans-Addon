package me.kate.clans;

import java.util.HashMap;
import java.util.Map;

import me.kate.clans.objects.CoreBlock;

public final class CoreTracker
{ 
	private final Map<String, CoreBlock> blocks = new HashMap<>();
	
	public void add(CoreBlock block)
	{
		blocks.put(block.getFactionId(), block);
	}
	
	public CoreBlock remove(CoreBlock block)
	{
		return blocks.remove(block.getFactionId());
	}
	
	public CoreBlock remove(String id)
	{
		return blocks.remove(id);
	}
	
	public CoreBlock get(String id)
	{
		return blocks.get(id);
	}
	
	public Map<String, CoreBlock> getBlocks()
	{
		return this.blocks;
	}
}
