package me.kate.clans.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import me.kate.clans.ClansPlugin;
import me.kate.clans.raids.RaidBossBar;
import me.kate.clans.utils.Cuboid;
import me.kate.clans.utils.Util;

public class DebugCommand implements CommandExecutor
{
	private ClansPlugin plugin;
	private FPlayers fpla;
	
	public DebugCommand(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.fpla = FPlayers.getInstance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args)
	{
		final Player player = (Player) sender;
		
		final FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
		
		if (args[0].equalsIgnoreCase("testreader"))
		{
//			new PageReader().showPageL(args[1], Arrays.asList("List1", "List2", "List3", "List4", "List5", "List6", "List7", "List8", "List9", "List10", "List11", "List12", "List13", "List14"), false, sender);
		}
		
		if (args[0].equalsIgnoreCase("chests"))
		{
			
			Faction faction = fplayer.getFaction();
			Cuboid cube = new Cuboid(faction.getCornerA().getLocation(), faction.getCornerB().getLocation());
			
			Set<Chest> chests = getBaseChests(cube);
			
			if (chests.isEmpty())
			{
				sender.sendMessage("Set was empty");
				return true;
			}
			else
			{
				chests.forEach(chest -> {
					sender.sendMessage(chest.toString());
				});
			}
			
			chests.forEach(chest -> 
			{
				Util.sendGlowingBlock(player, chest.getLocation(), 100);
			});
		}
		
		if (args[0].equalsIgnoreCase("testbar"))
		{
			Set<FPlayer> set = new HashSet<>();
			set.add(fpla.getByPlayer(player));
			
			RaidBossBar bossBar = new RaidBossBar(plugin, set);
			
			bossBar.create(Integer.valueOf(args[1]), "Grace period:");
			player.sendMessage("Showing bossbar");
		}
		
		return false;
	}
	
	private Set<Chest> getBaseChests(Cuboid cube)
	{
		final Set<Chest> chest = new HashSet<>();
		
//		String dataString = "minecraft:chest[waterlogged=false]";
//		BlockData data = Bukkit.createBlockData(dataString);
		
		for (Chunk chunk : cube.getChunks())
			for (int i = 0; i < chunk.getTileEntities().length; i++)
			{
				BlockState state = chunk.getTileEntities()[i];
				
				if (state.getBlock().getType().equals(Material.CHEST))
				{
					chest.add((Chest) state);
				}
			}
		
		return chest;
	}
}
