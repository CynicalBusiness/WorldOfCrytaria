package net.mayateck.GameCities.OrgHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.mayateck.GameCities.GameCities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ProtectionHandler implements Listener {
	public HashMap<String, String> protPlayers = new HashMap<String, String>();
		// PlayerName, Group
	public List<Player> overridePlayers = Arrays.asList();
	
	public HashMap<Block,String> blockData = new HashMap<Block,String>();
		// PunchedBlock, FortificationString
	public HashMap<String,Integer> fs = new HashMap<String,Integer>();
		// FortificationKey, ForitifcationValue
	Plugin plugin;
	
	public ProtectionHandler(Plugin p){
		plugin=p;
		saveBlockDataToDisk();
		fs.put("STONE", 8);
		fs.put("LEATHER", 16);
		fs.put("IRON_INGOT", 32);
		fs.put("DIAMOND", 64);
		fs.put("OBSIDIAN", 128);
	}
	
	@EventHandler
	public void onPlayerClickEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		if (protPlayers.containsKey(p.getName())){
			if (!blockData.containsKey(b)){
				Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName());
					// Organization has already been checked to ensure it exists.
				String imStr = e.getItem().getType().toString();
				if (fs.containsKey(imStr)){
					int fortVal = fs.get(imStr);
					// Permissions and such are already checked.
					String fortInfo = org.getPlayerGroup(p.getName())+":"+org.getName()+"-"+imStr+"|"+fortVal;
					blockData.put(b,fortInfo);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"The material "+e.getItem().getType().toString()+"is not valid for fortification."));
					protPlayers.remove(p);
				}
			} else {
				String fortInfo = blockData.get(b);
				int dashLoc = fortInfo.indexOf('-');
				int pipeLoc = fortInfo.indexOf('|');
				int colonLoc = fortInfo.indexOf(':');
				String group = fortInfo.substring(0, colonLoc-1);
				String orgname = fortInfo.substring(colonLoc+1, dashLoc-1);
				String mat = fortInfo.substring(dashLoc+1,pipeLoc-1);
				int fortNum = Integer.parseInt(fortInfo.substring(pipeLoc+1));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"This block is already fortified with "+mat+"(x"+fortNum+") by "+group+":"+orgname+"."));
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if (blockData.containsKey(b)){
			String bData = blockData.get(b);
			int dashLoc = bData.indexOf('-');
			int pipeLoc = bData.indexOf('|');
			int colonLoc = bData.indexOf(':');
			if (overridePlayers.contains(p)){
				Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName());
				String orgname = bData.substring(colonLoc+1, dashLoc-1);
				String mat = bData.substring(dashLoc+1,pipeLoc-1);
				if (org.getName().equalsIgnoreCase(orgname)){
					b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.getMaterial(mat), 1));
					return;
				}
			}
			int fortNum = Integer.parseInt(bData.substring(pipeLoc+1));
			fortNum--;
			if (fortNum>0){
				e.setCancelled(true);
			} else {
				blockData.remove(b);
			}
		}
	}
	
	public void saveBlockDataToDisk(){
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(plugin.getDataFolder()+File.separator+"blocks.bin"));
			oos.writeObject(blockData);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e1) {
			plugin.getLogger().log(Level.WARNING, "The blockdata file was missing. Attempting to create...");
			new File(plugin.getDataFolder()+File.separator+"blocks.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadBlockDataFromDisk(){
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plugin.getDataFolder()+File.separator+"blocks.bin"));
			Object result = ois.readObject();
			ois.close();
			blockData = (HashMap<Block,String>)result;
		} catch (FileNotFoundException e1) { 
			plugin.getLogger().log(Level.WARNING, "The blockdata file was missing. Calling data save...");
			saveBlockDataToDisk();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
