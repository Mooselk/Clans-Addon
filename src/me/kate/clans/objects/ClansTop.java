package me.kate.clans.objects;

import java.util.List;

public class ClansTop 
{
	private boolean isUpdating = false;	
	private List<TopClan> clans;
	
	public ClansTop()
	{
	}
	
	public ClansTop(List<TopClan> clans)
	{
		this.clans = clans;
	}
	
	public List<TopClan> getTopClans()
	{
		return clans;
	}
	
	public void setTopClans(List<TopClan> clans)
	{
		this.clans = clans;
	}
	
	public void setUpdating(boolean updating)
	{
		isUpdating = updating;
	}
	
	public boolean isUpdating()
	{
		return isUpdating;
	}
}
