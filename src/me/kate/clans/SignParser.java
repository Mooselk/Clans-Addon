package me.kate.clans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Role;

public class SignParser 
{
	private Sign sign;
	private String[] lines;
	
	public SignParser(Block block)
	{
		this.sign = (Sign) block.getState();
		this.lines = sign.getLines();
	}
	
	public Sign getSign()
	{
		return this.sign;
	}
	
	public String[] getLines()
	{
		return this.lines;
	}
	
	public String getLine(int i)
	{
		return this.sign.getLine(i);
	}
	
	public boolean isValid()
	{
		if (!lines[0].equalsIgnoreCase("[Subclaim]"))
		{
			return false;
		}
		if (lines[0].equalsIgnoreCase("[Subclaim]") && signHasRole())
		{
			return true;
		}
		if (lines[0].equalsIgnoreCase("[Subclaim]") && signHasName())
		{
			return true;
		}
		return false;
	}
	
	public boolean canOpenChest(Player player)
	{
		FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);	
		
		if (signHasRole())
		{
			Role role = Role.fromString(lines[1].toUpperCase());
			
			if (fplayer.getRole().isAtLeast(role))
			{
				return true;
			}
		}
		else if (signHasName())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean signHasRole()
	{
		for (Role role : Role.values())
			for (int i = 0; i < lines.length; i++)
				if (role != null)
					return true;
		
		return false;
	}
	
	public Role getSignRole()
	{	
		for (int i = 0; i < lines.length; i++)
		{
			Role role = Role.valueOf(lines[i]);
			
			if (role != null)
			{
				return role;
			}
		}
		
		return null;
	}
	
	public String getSignName()
	{
		for (int i = 0; i < lines.length; i++)
		{
			if (!lines[i].equals(""))
			{
				return lines[i];
			}
		}
		return "";
	}
	
	public boolean signHasName()
	{
		for (OfflinePlayer player : Bukkit.getOfflinePlayers())
		{
			for (int i = 0; i < lines.length; i++)
			{
				if (lines[i].equalsIgnoreCase(player.getName()))
				{
					return true;
				}
			}
		}
		
		for (Player player1 : Bukkit.getOnlinePlayers())
		{
			for (int i = 0; i < lines.length; i++)
			{
				if (lines[i].equalsIgnoreCase(player1.getName()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isMobDrops()
	{
		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];
			
			if (line.equalsIgnoreCase("[MobDrops]") || line.contains("[MobDrops]"))
			{
				sign.setLine(i, ChatColor.DARK_BLUE + line);
				sign.update();
				return true;
			}
		}
		
		return false;
	}
}
