package me.kate.clans;

import org.bukkit.Location;
import org.bukkit.Material;

public class WorldTracker 
{	
	private ClansPlugin plugin;
	
	public WorldTracker(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public Location nextLocation(final Location last) 
	{    
		final int distance = 500;
        
        alignToDistance(last, distance);
        
        int x = last.getBlockX();
        int z = last.getBlockZ();
        
        if (x < z) 
        {
            if (-1 * x < z) 
                x += distance;
            else 
                z += distance;
        } 
        else if (x > z)
        {
            if (-1 * x >= z)
                x -= distance;
            else
                z -= distance;
        }
        else 
        {
            if (x <= 0)
                z += distance;
            else 
                z -= distance;
        }
        
        last.setX(x);
        last.setZ(z);
        
        return last;
    }
	
	private Location alignToDistance(Location loc, int distance) 
	{
		if (loc == null) // Just skip everthing if its null 
			return null;
		
		int x = (int) (Math.round(loc.getX() / distance) * distance);
		int z = (int) (Math.round(loc.getZ() / distance) * distance);
		
		loc.setX(x);
		loc.setY(72);
		loc.setZ(z);
		
		return loc;
	}
	
	public interface Callback
	{
		public void onComplete(Location location);
	}
	
	public void calculatePoints(Callback callback)
	{
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
		{	
			Location search = null;  // Set once location is found
			Location last = new Location(plugin.getClansWorld(), 0, 72, 0);  // Starting location
			
			while (search == null)  // Kills loop once location is found
			{	
				Location next = plugin.getWorldTracker().nextLocation(last); // Get the next location, starting at 0,0		
				last = next; // Set the location we just checked
				
				if (next.getBlock().getType().equals(Material.AIR))
				{
					search = next; // Kill the loop
					plugin.getServer().getScheduler().runTask(plugin, () -> callback.onComplete(next)); // Do callback with location we found
				}
			}
		});
	}
}
