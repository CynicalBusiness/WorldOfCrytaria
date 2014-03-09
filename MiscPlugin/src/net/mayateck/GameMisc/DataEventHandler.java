package net.mayateck.GameMisc;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DataEventHandler implements Listener{
	public GameMisc plugin;
	public static int delay = 0;
	
	public DataEventHandler(GameMisc p){
		plugin=p;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e){
		Player p = e.getPlayer();
		String n = p.getName();
		List<String> altBanned = plugin.getConfig().getStringList("config.alt_banned_players");
		List<String> banned = plugin.getConfig().getStringList("config.banned_players");
		List<String> whitelist = plugin.getConfig().getStringList("config.whitelisted_players");
		if (altBanned.contains(n)){
			e.setKickMessage(ChatColor.translateAlternateColorCodes('&',
					"&rThis account has been &c&osuspended &rdue to suspended alternate accounts. If this is incorrect, please email &3matt@capit.me&r."));
			e.setResult(Result.KICK_BANNED);
		} else if (banned.contains(n)) {
			e.setKickMessage(ChatColor.translateAlternateColorCodes('&',
					"&rThis accound has been &c&osuspended &rdue to violation of the &oInternational Code of Conduct&r."));
			e.setResult(Result.KICK_BANNED);
			
		} else if (plugin.getConfig().getBoolean("config.whitelist_enabled") && !whitelist.contains(n)) {
			e.setKickMessage(ChatColor.translateAlternateColorCodes('&',
					"&r&lWe're sorry! &rThe server is currently under &cmaintenance&r and should return again &3soon&r!"));
			e.setResult(Result.KICK_WHITELIST);
			
		} else {
			if (delay==0){
				delay=1;
				new BukkitRunnable(){

					@Override
					public void run() {
						delay = 0;
					}
					
				}.runTaskLater(plugin, 40L);
				if (whitelist.contains(n)){
					e.setResult(Result.ALLOWED);
				} else {
					if (plugin.getServer().getOnlinePlayers().length<plugin.getServer().getMaxPlayers()){
						e.setResult(Result.ALLOWED);
					} else {
						// TODO Setup login que.
						e.setKickMessage("The server has currently reached capacity. Please wait a while and try again.");
						e.setResult(Result.KICK_FULL);
					}
				}
			} else {
				e.setKickMessage("Too many simultanious join attempts. Wait a few seconds and try again.");
				e.setResult(Result.KICK_OTHER);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLogon(final PlayerJoinEvent e){
		new BukkitRunnable(){

			@Override
			public void run() {
				plugin.getLogger().info("Player joined. Checking hasJoined data...");
				Player p = e.getPlayer();
				List<String> plist = plugin.getPList().getStringList("players");
				if (!plist.contains(p.getName())){
					plugin.getLogger().info("Player is new, creating new random location...");
					//newPlayerStuff(p);
		
					Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,255,0), true);
					plugin.getLogger().info("Created "+spawn+" for location.");
					p.teleport(spawn);
					plugin.getLogger().info("Teleported player and applied effect.");
					p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(4, 6), true);
					
					plist.add(p.getName());
					plugin.getPList().set("players", plist);
					plugin.savePList();
				}
			}
		}.runTaskLater(plugin, 1L);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		e.setDeathMessage(ChatColor.DARK_RED+p.getName()+" was slain.");
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e){
		new BukkitRunnable(){

			@Override
			public void run() {
				plugin.getLogger().info("Player died. Checking bed data...");
				Player p = e.getPlayer();
				if (e.isBedSpawn()==false){
					plugin.getLogger().info("Player has no bed, creating new random location...");
					Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,255,0), true);
					plugin.getLogger().info("Created "+spawn+" for location.");
					p.teleport(spawn);
					plugin.getLogger().info("Teleported player and applied effect.");
					p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(4, 6), true);
				}
			}
			
		}.runTaskLater(plugin, 1L);
			
	}
	
	public Location getRandomSpawnLocation(Player p, Location loc, boolean doOverride){
		if (doOverride){
			plugin.getLogger().info("Override true. New coordinates generated.");
			double randX = (Math.random()*12000)-6000;
			double randZ = (Math.random()*12000)-6000;
			loc.setX(randX);
			loc.setZ(randZ);
		}
		if (loc.getBlock().getType().isSolid()){
			plugin.getLogger().info("Got a good spawn location for "+p.getName()+"!");
			loc.setY(loc.getY()+2);
			if (loc.getBlock().getType()==Material.WATER || loc.getBlock().getType()==Material.LAVA){
				return getRandomSpawnLocation(p, loc, true);
			}
			return loc;
		} else {
			//plugin.getLogger().info("Got bad spawn location for "+p.getName()+": Non-Solid");
			loc.setY(loc.getY()-1);
			return getRandomSpawnLocation(p, loc, false);
		}
	}
}
