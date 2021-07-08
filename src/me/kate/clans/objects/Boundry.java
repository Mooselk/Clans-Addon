package me.kate.clans.objects;

import org.bukkit.Location;

import com.sk89q.worldedit.math.BlockVector3;

import me.kate.clans.ClansPlugin;

public class Boundry 
{
	private int x, y, z;

	public Boundry(int x, int z)
	{
		this.x = x;
		this.z = z;
	}
	
	public Boundry(String x, String z)
	{
		this.x = Integer.valueOf(x);
		this.z = Integer.valueOf(z);
	}
	
	public Boundry(String x, String y, String z)
	{
		this.x = Integer.valueOf(x);
		this.y = Integer.valueOf(y);
		this.z = Integer.valueOf(z);
	}
	
	public Boundry(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public int getZ()
	{
		return this.z;
	}
	
	public Boundry translate(int dx, int dy, int dz)
	{
		if (isNegitive(dx))
		{
			dx = dx - this.x;
		}
		else
		{
			dx = dx + this.x;
		}
		
		dy = dy + this.y;
		
		if (isNegitive(dz))
		{
			dz = dz - this.z;
		}
		else
		{
			dz = dz + this.z;
		}
		
		return new Boundry(dx, dy, dz);
	}
	
	public Boundry translate(int dx, int dz)
	{
		if (isNegitive(dx))
		{
			dx = dx - this.x;
		}
		else
		{
			dx = dx + this.x;
		}
		
		if (isNegitive(dz))
		{
			dz = dz - this.z;
		}
		else
		{
			dz = dz + this.z;
		}
		
		return new Boundry(dx, dz);
	}
	
	private static boolean isNegitive(int number)
	{
		if (number < 0)
        {
			return true;
        }
		return false;
	}
	
	public static BlockVector3 translateVector3(BlockVector3 vector, int dx, int y, int dz)
	{
		if (isNegitive(dx))
		{
			dx = dx - vector.getBlockX();
		}
		else
		{
			dx = dx + vector.getBlockX();
		}
		
		if (isNegitive(dz))
		{
			dz = dz - vector.getBlockZ();
		}
		else
		{
			dz = dz + vector.getBlockZ();
		}
		return BlockVector3.at(dx, y, dz);
	}
	
	public static Location translateLocation(int x, int y, int z, Location loc)
	{
		Location cloned = loc.clone();
		
		if (isNegitive(cloned.getBlockZ()))
		{
			cloned.add(0, 0, z);
		}
		else
		{
			cloned.subtract(0, 0, z);
		}
		
		cloned.add(0, y, 0);
		
		if (isNegitive(cloned.getBlockX()))
		{
			cloned.add(x, 0, 0);
		}
		else
		{
			cloned.subtract(x, 0, 0);
		}
		
		return cloned;
	}
	
	public static BlockVector3 correct(BlockVector3 vector)
	{
		if (isNegitive(vector.getBlockZ()))
		{
			vector.add(0, 0, 2);
		}
		else
		{
			vector.subtract(0, 0, 2);
		}
		if (isNegitive(vector.getBlockX()))
		{
			vector.add(2, 0, 0);
		}
		else
		{
			vector.subtract(2, 0, 0);
		}
		
		return vector;
	}
	
	public static BlockVector3 setVectorY(BlockVector3 vector, int y)
	{
		return BlockVector3.at(vector.getBlockX(), y, vector.getBlockY());
	}
	
	public static Location getLocation(BlockVector3 vector) 
	{
		return new Location(ClansPlugin.getPlugin(ClansPlugin.class)
				.getClansWorld(), 
				vector.getBlockX(), 
				vector.getBlockY(), 
				vector.getBlockZ());
	}
	
	@Override
	public String toString()
	{
		return this.x + " " + this.z;
	}
}