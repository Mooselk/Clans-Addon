package me.kate.clans.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import me.kate.clans.ClansPlugin;
import me.kate.clans.config.Messages;
import me.kate.clans.raids.Raid;
import me.kate.clans.raids.WrappedFaction;
import me.kate.clans.raids.WrappedFactionManager;

public class SilkSpawnersSpawnerBreakListener implements Listener
{
	private WrappedFactionManager manager;
	private FPlayers inst;
	
	public SilkSpawnersSpawnerBreakListener(ClansPlugin plugin)
	{
		this.inst = FPlayers.getInstance();
		this.manager = plugin.getFactionManager();
	}
	
	@EventHandler
	public void onSpawnerBreak(final SilkSpawnersSpawnerBreakEvent event)
	{
		final FPlayer player = inst.getByPlayer(event.getPlayer());
		
		if (!player.hasFaction())
		{
			player.sendMessage(Messages.SPAWNER_BREAK_NO_FACTION);
			event.setCancelled(true);
		}
		
		final WrappedFaction wrapper = manager.getByFaction(player.getFaction());
		
		if (wrapper == null) return;
		
		if (wrapper.getRaid() == null) return;
		
		if (wrapper.getRaid().getParticipants().isDefendingPlayer(player)) 
		{
			player.sendMessage(Messages.SPAWNER_BREAK_RAIDED);
			event.setCancelled(true);
			return;
		}
		
		final Raid raid = wrapper.getRaid();
		int broken = raid.getSpawnersBroken();
		
		if (broken >= raid.getMaxSpawnersBroken())
		{
			player.sendMessage(Messages.SPAWNER_BREAK_MAX.replaceAll("%broken%", broken+"").replaceAll("%max%", raid.getMaxSpawnersBroken()+""));
			event.setCancelled(true);
			return;
		}
		
		broken++;
		raid.setSpawnersBroken(broken);
		player.sendMessage(Messages.SPAWNER_BROKEN.replaceAll("%broken%", broken+"").replaceAll("%max%", raid.getMaxSpawnersBroken()+""));
	}
}
