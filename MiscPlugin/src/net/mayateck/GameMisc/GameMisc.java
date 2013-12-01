package net.mayateck.GameMisc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GameMisc extends JavaPlugin{
	public FileConfiguration pList = null;
	public File pListFile = null;
	public Plugin plugin = this;
	
	@Override
	public void onEnable(){
		DataEventHandler eh = new DataEventHandler(this);
		getServer().getPluginManager().registerEvents(eh, this);
		saveDefaultPList();
		saveDefaultConfig();
		getLogger().info("Enabled GameMisc data.");
	}
	
	@Override
	public void onDisable(){
		
		getLogger().info("Disabled GameMisc data.");
	}
	
	public void reloadPList(){
		if (pListFile==null){
			pListFile = new File(getDataFolder(), "players.yml");
		}
		pList = YamlConfiguration.loadConfiguration(pListFile);
		
		InputStream defStream = this.getResource("players.yml");
		if (defStream != null){
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defStream);
			pList.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getPList(){
		if (pList==null){
			reloadPList();
		}
		return pList;
	}
	
	public void savePList(){
		if (pList==null || pListFile==null){
			return;
		}
		try {
			getPList().save(pListFile);
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Could not save config to " + pListFile, e);
		}
	}
	
	public void saveDefaultPList(){
		if (pListFile == null){
			pListFile = new File(getDataFolder(), "players.yml");
		}
		if (!pListFile.exists()){
			plugin.saveResource("players.yml", false);
		}
	}
}
