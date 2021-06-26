package me.kate.clans.objects;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import net.md_5.bungee.api.ChatColor;

public class PageReader 
{	
	private ClansPlugin plugin;
	private Formatter formatter;
	
	public PageReader(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.formatter = new Formatter(plugin);
	}
	
	public void showPage(String pageStr, List<TopClan> lines, boolean onePage, CommandSender sender)
	{
		int page = 1;
       
		if (pageStr != null) 
		{
        	try 
        	{
                page = Integer.parseInt(pageStr);
            } 
        	catch (final NumberFormatException ignored) 
        	{
            }
            if (page < 1) 
            {
                page = 1;
            }
            
        }

        final int start = onePage ? 0 : (page - 1) * 10;
        
        int end;
        
        for (end = 0; end < lines.size(); end++)
        {
            final String line = lines.get(end).toString();
           
            if (line.startsWith("#")) 
            {
                break;
            }
        }

        final int pages = end / 10 + (end % 10 > 0 ? 1 : 0);
        
        if (page > pages)
        {
            sender.sendMessage("infoUnknownChapter");
            return;
        }
        
        for (int i = start; i < end && i < start + (onePage ? 20 : 10); i++) 
        {
            sender.sendMessage(formatter.formatRankings(lines.get(i)));
        }
        
        if (!onePage && page < pages)
        {
        	
        	if (sender instanceof Player)
    		{
    			FPlayer fp = FPlayers.getInstance().getByPlayer((Player) sender);
    			
    			if (fp.hasFaction())
    			{
    				sender.sendMessage(" ");
    				
    				for (TopClan clan : lines)
    				{
    					if (clan.getId().equals(fp.getFaction().getId()))
    					{
    						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
    								plugin.getPluginConfig().getConfig()
    								.getString("settings.clanstop.clan")
    								.replaceAll("%ranking%", clan.getRanking() + "")
    								.replaceAll("%clan_name%", clan.getTag())
    								.replaceAll("%points%", clan.getPoints() + "")));
    						break;
    					}
    				}
    			}
    		}
        	
        	sender.sendMessage(" ");
        	sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
        			plugin.getPluginConfig().getConfig()
        			.getString("settings.clanstop.pages")
        			.replaceAll("%page%", page + "")
        			.replaceAll("%pages%", pages + "")));
        }
        
        sender.sendMessage(" ");
    }
	
}
