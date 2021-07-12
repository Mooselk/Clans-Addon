package me.kate.clans.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import me.kate.clans.ClansPlugin;
import me.kate.clans.SignParser;
import me.kate.clans.raids.WrappedFaction;
import me.kate.clans.raids.WrappedFactionManager;
import me.kate.clans.utils.Util;

public class PlayerInteractListener implements Listener
{
	private WrappedFactionManager manager;
	private FPlayers inst;
	
	public PlayerInteractListener(ClansPlugin plugin)
	{
		this.manager = plugin.getFactionManager();
		this.inst = FPlayers.getInstance();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		FPlayer fplayer = inst.getByPlayer(player);
		
		if (!fplayer.hasFaction()) 
			return;
		
		if (block == null)
			return;
		
		if (block.getType() != Material.CHEST) 
			return;
		
		WrappedFaction faction = manager.getByFaction(fplayer.getFaction());
		
		if (faction.getRaid() != null && faction.getRaid().getParticipants().isDefendingPlayer(fplayer))
		{
			player.sendMessage("You can't access your items while being raided!");
			event.setCancelled(true);
		}	
		
		Util.findAttachedSigns(event.getClickedBlock()).forEach(b ->
		{
			if (!(b.getBlockData() instanceof WallSign))
				return;
			
			SignParser parser = new SignParser(b);
				
			if (parser.isValid())
			{
				Bukkit.getLogger().info("Valid sign");
				Bukkit.getLogger().info("Length: " + parser.getSign().getLines().length);
				
				if (faction.isRaiding() && faction.getRaid().getParticipants().isDefendingPlayer(fplayer))
				{
					player.sendMessage("You can't open your chests while being raided!");
					event.setCancelled(true);
				}
				else return;
				
				if (parser.signHasName() && !parser.getSign().getLine(1).equals(player.getName()))
				{
					player.sendMessage("This isn't your chest to open!");
					event.setCancelled(true);
				}
			}
			
			Bukkit.getLogger().info(parser.getSign().getLines().toString());
		});
	}
}
