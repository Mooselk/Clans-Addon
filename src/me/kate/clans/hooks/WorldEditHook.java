package me.kate.clans.hooks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import me.kate.clans.ClansPlugin;

public class WorldEditHook
{
	private ClansPlugin plugin;
	
	public WorldEditHook(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public Plugin getHook()
	{	
		final Plugin plug = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		
		if (plug == null) 
		{
			plugin.getLogger().severe("WorldEdit not present, disabling!");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
		
		return plug;
	}

	public WorldEdit getPlugin() 
	{
		return WorldEdit.getInstance();
	}
	
	public BlockVector3[] paste(File file, Location location)
	{
		Clipboard clipboard = null;
		BlockVector3[] clipArray = new BlockVector3[2];
		
		if (file == null)
		{
			plugin.getLogger().warning("No schematic to paste! Missing file.");
			return null;
		}
		
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) 
		{
		    clipboard = reader.read();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		clipArray[0] = clipboard.getMinimumPoint();
	    clipArray[1] = clipboard.getMaximumPoint();
		
		try (EditSession editSession = new EditSessionBuilder(BukkitAdapter.adapt(location.getWorld())).build())
		{
		    Operation operation = new ClipboardHolder(clipboard)
		            .createPaste(editSession)
		            .to(BlockVector3.at(location
		            		.getX(), location
		            		.getY(), location
		            		.getZ()))
		            .ignoreAirBlocks(true)
		            .build();
				Operations.complete(operation);
		} 
		catch (WorldEditException e) 
		{
			e.printStackTrace();
		}
		
		Bukkit.getLogger().info("Pasting at: " + location);
		
		return clipArray;
	}
}
