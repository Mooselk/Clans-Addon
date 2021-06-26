package me.kate.clans.objects;

import java.util.Comparator;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class TopClan implements Comparator<TopClan>
{
	private final String id;
	private int ranking;
	private final int points;
	
	public TopClan(String id, int points)
	{
		this.id = id;
		this.points = points;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setRanking(int ranking)
	{
		this.ranking = ranking;
	}
	
	public int getRanking()
	{
		return this.ranking;
	}
	
	public int getPoints()
	{
		return this.points;
	}
	
	public Faction getFaction()
	{
		return Factions.getInstance().getFactionById(id);
	}

	public String getTag()
	{
		if (getFaction() == null)
		{
			return "ERROR_NAME";
		}
		
		return getFaction().getTag();
	}
	
	@Override
	public String toString()
	{
		if (getFaction() == null)
		{
			return ranking + ". " +  "ERRORNAME " + getPoints();
		}
		
		return ranking + ". " + getFaction().getTag() + " " + getPoints();
	}
	
	@Override
	public int compare(TopClan clanA, TopClan clanB)
	{
		return clanA.getPoints() - clanB.getPoints();
	}
}
