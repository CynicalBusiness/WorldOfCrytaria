package net.mayateck.GameMisc.chat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatDataHandler implements Listener{
	Plugin plugin;
	
	public ChatDataHandler(Plugin p){
		plugin=p;
	}
	
	public enum ObfChatLevel{
		NONE, LIGHT, HEAVY
	}
	
	@EventHandler
	public void onPlayerChatted(AsyncPlayerChatEvent e){
		Player plyr = e.getPlayer();
		Player[] allPlayers = plugin.getServer().getOnlinePlayers();
		String tag = "["+plyr.getName()+"]: ";
		String msg = e.getMessage();
		for (int i=0; i>allPlayers.length; i++){
			Player p = allPlayers[i];
			if (!p.getName().equalsIgnoreCase(plyr.getName())){
				Location pLoc = plyr.getLocation();
				Location loc = p.getLocation();
				double diff = Math.abs(pLoc.getX()-loc.getX()) + Math.abs(pLoc.getZ()-loc.getZ());
				if (diff<=800){
					p.sendMessage(tag+msg);
				} else if (diff<=1000){
					p.sendMessage(obfusicateString(tag, ObfChatLevel.LIGHT)+obfusicateString(msg, ObfChatLevel.LIGHT));
				} else if (diff<=1200){
					p.sendMessage(obfusicateString(tag, ObfChatLevel.HEAVY)+obfusicateString(msg, ObfChatLevel.HEAVY));
				}
			} else {
				p.sendMessage(tag+msg);
			}
		}
		e.setCancelled(true);
	}
	
	public String obfusicateString(String string, ObfChatLevel level){
		int charRand = 0;
		switch (level){
			case LIGHT: charRand=10;
			case HEAVY: charRand=30;
			default: charRand=0;
		}
		char[] chars = string.toCharArray();
		for (int i=0; i<chars.length; i++){
			double randBreak = Math.ceil(Math.random()*100);
			if (randBreak<=charRand){
				chars[i]='*';
			}
		}
		string=String.copyValueOf(chars);
		return string;
	}
}
