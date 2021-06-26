package me.kate.clans;

import org.bukkit.Bukkit;
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
		if (!lines[0].equals("[Subclaim]"))
		{
			return false;
		}
		if (lines[0].equals("[Subclaim]") && signHasRole())
		{
			return true;
		}
		if (lines[0].equals("[Subclaim]") && signHasName())
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
		{
			if (lines[1].equalsIgnoreCase(role.toString()))
			{
				return true;
			}
		}
		return false;
	}
	
	public Role getSignRole()
	{
		return Role.valueOf(sign.getLine(1).toUpperCase());
	}
	
	public String getSignName()
	{
		return this.sign.getLine(1);
	}
	
	public boolean signHasName()
	{
		for (OfflinePlayer player : Bukkit.getOfflinePlayers())
			if (player.getName().equals(sign.getLine(1)))
				return true;
		return false;
	}
}
