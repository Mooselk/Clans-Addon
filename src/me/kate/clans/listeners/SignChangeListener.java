package me.kate.clans.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import me.kate.clans.SignParser;
import me.kate.clans.config.Messages;

public class SignChangeListener implements Listener
{
	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		SignParser parser = new SignParser(block);
		
		if (!parser.isValid())
		{
			player.sendMessage(Messages.INVALID_SIGN_ERROR.replaceAll("%error%", parser.getLine(1)));
		}
	}
}

