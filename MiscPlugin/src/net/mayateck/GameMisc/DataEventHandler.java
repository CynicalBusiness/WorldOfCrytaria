package net.mayateck.GameMisc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

public class DataEventHandler implements Listener{
	public Plugin plugin;
	
	public DataEventHandler(Plugin p){
		plugin=p;
	}

	@EventHandler
	public void onPlayerLogon(PlayerJoinEvent e){
		plugin.getLogger().info("Player joined. Checking hasJoined data...");
		Player p = e.getPlayer();
		List<String> plist = plugin.getConfig().getStringList("players");
		if (!plist.contains(p.getName())){
			plugin.getLogger().info("Player is new, creating new random location...");
			//newPlayerStuff(p);
			teleportToRandom(p);
			plist.add(p.getName());
			plugin.getConfig().set("players", plist);
			plugin.saveConfig();
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		Player p = e.getEntity();
		e.setDeathMessage(ChatColor.DARK_RED+p.getName()+" was slain.");
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		plugin.getLogger().info("Player died. Checking bed data...");
		Player p = e.getPlayer();
		if (e.isBedSpawn()==false){
			plugin.getLogger().info("Player has no bed, creating new random location...");
			teleportToRandom(p, e);
		}
	}
	
	public void teleportToRandom(Player p){
		Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,64,0), true);
		plugin.getLogger().info("Created "+spawn+" for location.");
		p.teleport(spawn);
	}
	
	public void teleportToRandom(Player p, PlayerRespawnEvent e){
		Location spawn = getRandomSpawnLocation(p, new Location(p.getWorld(),0,64,0), true);
		plugin.getLogger().info("Created "+spawn+" for location.");
		e.setRespawnLocation(spawn);
	}
	
	public Location getRandomSpawnLocation(Player p, Location loc, boolean doOverride){
		if (doOverride){
			plugin.getLogger().info("Override true. New coordinates generated.");
			double randX = (Math.random()*20000)-10000;
			double randZ = (Math.random()*20000)-10000;
			loc.setX(randX);
			loc.setZ(randZ);
		}
		if (!loc.getBlock().getType().isSolid()){
			plugin.getLogger().info("Got bad spawn location for "+p.getName()+": Non-solid.");
			return getRandomSpawnLocation(p, loc, true);
		} else if (loc.getBlock().getType().equals(Material.AIR)){
			plugin.getLogger().info("Got bad spawn location for "+p.getName()+": Material.AIR");
			loc.setY(loc.getY()-1);
			return getRandomSpawnLocation(p, loc, false);
		} else {
			Location above = loc.clone();
			above.setY(above.getY()+1);
			if (above.getBlock().getType().equals(Material.AIR)){
				plugin.getLogger().info("Got a good spawn location for "+p.getName()+"!");
				return loc;
			} else {
				plugin.getLogger().info("Got bad spawn location for "+p.getName()+": Underground");
				return getRandomSpawnLocation(p, above, false);
			}
		}
	}
	
	public void newPlayerStuff(Player p){
		ItemStack is = new ItemStack(Material.BOOK, 1);
		BookMeta bm = (BookMeta) is.getItemMeta();
		
		bm.setTitle("World of Cyrtrador Guidebook");
		bm.setAuthor("Wehttam664");
		
		List<String> pages = Arrays.asList(
			"§lWelcome to Cyrtaria!@"
			+ "§l#-----------------#@"
			+ "Cyrtaria is unlike@"
			+ "standard Minecraft or@"
			+ "Tekkit servers. Mobs@"
			+ "are all but disabled@"
			+ "and player spawns are@"
			+ "completly random with@"
			+ "No player-to-player@"
			+ "teleportation. To do@"
			+ "well, one must work@"
			+ "together with others@"
			+ "to create a",
			"civilization and@"
			+ "develop a well-oiled@"
			+ "government. In order@"
			+ "to gain new tech,@"
			+ "you must research@"
			+ "for your civilization@"
			+ "toward war, science,@"
			+ "exploration, and@"
			+ "more!@"
			+ "Good Luck!",
			" Authors: Wehttam664,@"
			+ "willynillyskin,@"
			+ "and ikenna798.@"
			+ "@"
			+ "For more info, visit@"
			+ "woc.mayateck.net"
			);
		bm.setPages(pages);
		p.getInventory().addItem(is);
	}
}
