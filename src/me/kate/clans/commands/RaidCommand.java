package me.kate.clans.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import me.kate.clans.ClansPlugin;
import me.kate.clans.config.Messages;
import me.kate.clans.raids.Raid;
import me.kate.clans.raids.RaidManager;
import me.kate.clans.raids.RaidParticipants;
import me.kate.clans.raids.WrappedFaction;
import net.md_5.bungee.api.ChatColor;

public class RaidCommand implements CommandExecutor
{
	private ClansPlugin plugin;
	private Factions inst;
	private FPlayers fpla;
	private RaidManager manager;
	
	public RaidCommand(ClansPlugin plugin)
	{
		this.plugin = plugin;
		this.inst = Factions.getInstance();
		this.fpla = FPlayers.getInstance();
		this.manager = plugin.getRaidManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage("This command is for ingame use only!");
			return true;
		}
		
		final Player player = (Player) sender;
		final FPlayer fPlayer = fpla.getByPlayer(player);
		
		if (!fPlayer.hasFaction())
		{
			player.sendMessage(Messages.NO_FACTION);
			return true;
		}
		
		if (plugin.getRaidManager().getByFPlayer(fPlayer) != null)
		{
			sender.sendMessage(ChatColor.RED + "You can't start another raid, you're already in one.");
			return false;
		}
		
		final WrappedFaction attackingFaction = plugin
				.getFactionManager()
				.getByFaction(fPlayer.getFaction());
		
		final List<Faction> factions = getRaidableFactions(fPlayer.getFaction());
		
		if (factions.isEmpty())
		{
			player.sendMessage(Messages.NO_FACTIONS_FOUND);
			return true;
		}
		
		final WrappedFaction defendingFaction = plugin
				.getFactionManager()
				.getByFaction(factions
						.get(new Random().nextInt(factions.size())));
		
		player.sendMessage(Messages.SELECTED_FACTION.replaceAll("%selected%", defendingFaction.getFaction().getTag()));
		
		final Raid raid = new Raid(plugin, new RaidParticipants(defendingFaction, attackingFaction));
		
		manager.add(raid);
		
		raid.start();
		
		return false;
	}
	
	private List<Faction> getRaidableFactions(Faction raider)
	{
		List<Faction> onlineFactions = new ArrayList<>();
		for (Faction faction : inst.getAllFactions())
		{
			if (faction == raider)
				continue;
			
			if (faction.getOnlinePlayers().isEmpty() || faction.hasShield())
				continue;
			
			if (raider.getRelationTo(faction).isAlly() || raider.getRelationTo(faction).isTruce())
				continue;
			
			onlineFactions.add(faction);
		}
		return onlineFactions;
	}
}