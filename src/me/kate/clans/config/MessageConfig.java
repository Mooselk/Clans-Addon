package me.kate.clans.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kate.clans.ClansPlugin;

public class MessageConfig 
{
	private ClansPlugin plugin;
	private File messagesFile;
	private FileConfiguration messagesConfiguration;
	
	public MessageConfig(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void create()
	{
		messagesFile = new File(plugin.getDataFolder(), "messages.yml");
			
		if (!messagesFile.exists()) 
		{
			messagesFile.getParentFile().mkdirs();
			plugin.saveResource("messages.yml", false);
			plugin.getLogger().info("Creating messages.yml...");
		}
			
		messagesConfiguration = new YamlConfiguration();
			
		try 
		{
			messagesConfiguration.load(messagesFile);
				
		} 
		catch (IOException | InvalidConfigurationException e) 
		{
			plugin.getLogger().severe("Failed to create config.yml!");
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() 
	{
		return messagesConfiguration;
	}
	
	public String getMessage(String msg)
	{
		return messagesConfiguration.getString("messages." + msg);
	}
}
