package me.kate.clans.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import me.kate.clans.ClansPlugin;
import me.kate.clans.objects.TopClan;

public class DatabaseManager 
{
	private ClansPlugin plugin;
	
	public DatabaseManager(ClansPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public Connection connect() throws SQLException
	{
		final String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/points.db";
		return DriverManager.getConnection(url);
	}
	
	public void setupDatabase()
	{
        final String sql = "CREATE TABLE IF NOT EXISTS clanstop("
        		+ "`clanId` INT PRIMARY KEY, "
        		+ "`points` VARCHAR(255)"
        		+ ");";
        Connection connection = null;
        try 
        {  
        	connection = connect();
        	
        	if (connection != null)
            {
                DatabaseMetaData meta = connection.getMetaData();
                Statement statement = connection.createStatement();
                statement.execute(sql);
                statement.close();
                
                plugin.getLogger().info("Using driver " + meta.getDriverName());
                plugin.getLogger().info("Database " + "points" + " has been created.");
            }
        } 
        catch (SQLException e) 
        {
        	plugin.getLogger().severe(e.getMessage());
        }
        finally 
        {
        	try 
    		{
        		if (connection != null)
        			connection.close();
			}
    		catch (SQLException e) 
    		{
				e.printStackTrace();
			}
        }
    }
	
	public boolean execQuery(String query)
	{
		Connection conn = null;
		try 
		{
			conn = connect();
			
			Statement st = conn.createStatement();
			st.execute(query);
			st.close();
			return true;
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		finally 
		{
			try 
			{
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public enum Mode
	{
		ADD,
		SUBTRACT;
	}
	
	public void addFaction(String factionId, int points)
	{
		final String query = "INSERT INTO clanstop (clanId, points)" + " VALUES ('" + factionId + "', " + points + ");";
		
		if (!execQuery(query))
		{
			plugin.getLogger().severe("Failed to add Faction (" + factionId + ") to database!");
		}
	}
	
	public void removeFaction(String factionId)
	{
		final String query = "DELETE FROM clanstop WHERE clanId = " + factionId + ";";
		
		if (!execQuery(query))
		{
			plugin.getLogger().severe("Failed to remove Faction (" + factionId + ") from database!");
		}
		else
		{
			plugin.getLogger().info("Removed Faction (" + factionId + ") from database!");
		}
	}
	
	public List<TopClan> getTopClans()
	{
		List<TopClan> clans = new ArrayList<>();
		
		final String query = "SELECT * FROM clanstop";
		final ResultSet results = execRSQuery(query);
		
		try 
		{
			while (results.next()) 
			{
				String clanId = results.getString("clanId");
			    int points = results.getInt("points");
			    clans.add(new TopClan(clanId, points));
			}
		} 
		catch (SQLException e) { e.printStackTrace(); } 
		return clans;
	}
	
	public ResultSet execRSQuery(String query)
	{
		ResultSet rs = null;
		Connection conn = null;
		try 
		{
			conn = connect();
			PreparedStatement st = conn.prepareStatement(query);
			rs = st.executeQuery();
			st.close();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (conn != null)
					conn.close();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return rs;
	}
	
	public int getPoints(String factionId)
	{
		String query = "SELECT points FROM clanstop WHERE clanId = " + factionId + ";";
		try 
		{
			return execRSQuery(query).getInt("points");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean adjustPercentagePoints(String otherId, String factionId, int percentage, Mode mode)
	{
		int points = this.getPoints(otherId);
		String query = "UPDATE clanstop SET points = points @RDD# (" 
				+ points + " * "+ percentage 
				+ " / 100) WHERE clanId = " + factionId + ";";

		switch (mode)
		{
			case ADD:
				query = query.replace("@RDD#", "+");
				break;
				
			case SUBTRACT:
				query = query.replace("@RDD#", "-");
				break;
		}
		
		return execQuery(query);
	}
	
	public boolean adjustPoints(String factionId, int points, Mode mode)
	{
		String query = "UPDATE clanstop SET points = points @RDD# " + points + " WHERE clansId = " + factionId;

		switch (mode)
		{
			case ADD:
				query = query.replace("@RDD#", "+");
				break;
				
			case SUBTRACT:
				query = query.replace("@RDD#", "-");
				break;
		}
		
		return execQuery(query);
	}
}
