package net.mayateck.GameCities.OrgHandler;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class ProtectionHandler implements Listener {
	public HashMap<Player, String> protPlayers = new HashMap<Player, String>();
	
	Plugin plugin;
	
	public ProtectionHandler(Plugin p){
		plugin=p;
	}
	
	@EventHandler
	public void onPlayerClickEvent(PlayerInteractEvent e){
		
	}
}
