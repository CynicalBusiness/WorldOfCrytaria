package net.mayateck.GameMisc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import net.mayateck.GameMisc.biomes.CropGrowthHandler;
import net.mayateck.GameMisc.chat.ChatDataHandler;

import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GameMisc extends JavaPlugin{
	public static final String tag = "&8&l[&3MISC&8&l]&r ";
	
	public FileConfiguration pList = null;
	public File pListFile = null;
	public Plugin plugin = this;
	
	public static final HashMap<String, List<Biome>> bialias = new HashMap<String, List<Biome>>();
	
	@Override
	public void onEnable(){
		DataEventHandler eh = new DataEventHandler(this);
		ChatDataHandler cd = new ChatDataHandler(this);
		PhysicsHandler ph = new PhysicsHandler(this);
		CommandHandler cmdh = new CommandHandler(this);
		CropGrowthHandler cgh = new CropGrowthHandler(this);
		
		getServer().getPluginManager().registerEvents(eh, this);
		getServer().getPluginManager().registerEvents(cd, this);
		getServer().getPluginManager().registerEvents(ph, this);
		getServer().getPluginManager().registerEvents(cgh, this);
		
		getCommand("announce").setExecutor(cmdh);
		saveDefaultPList();
		saveDefaultConfig();
		getLogger().info("Enabled GameMisc data.");
		
		// Put biome data.
		bialias.put("DESERT", Arrays.asList(Biome.DESERT, Biome.DESERT_HILLS, Biome.DESERT_MOUNTAINS));
		bialias.put("PLAINS", Arrays.asList(Biome.PLAINS, Biome.SAVANNA, Biome.SAVANNA_MOUNTAINS, Biome.SAVANNA_PLATEAU, Biome.SAVANNA_PLATEAU_MOUNTAINS,
				Biome.SUNFLOWER_PLAINS, Biome.SMALL_MOUNTAINS));
		bialias.put("OCEAN", Arrays.asList(Biome.OCEAN, Biome.DEEP_OCEAN));
		bialias.put("FOREST", Arrays.asList(Biome.FLOWER_FOREST, Biome.FOREST, Biome.FOREST_HILLS, Biome.BIRCH_FOREST, Biome.BIRCH_FOREST_HILLS,
				Biome.BIRCH_FOREST_HILLS_MOUNTAINS, Biome.BIRCH_FOREST_MOUNTAINS, Biome.ROOFED_FOREST, Biome.ROOFED_FOREST_MOUNTAINS));
		bialias.put("COLD", Arrays.asList(Biome.TAIGA, Biome.TAIGA_HILLS, Biome.TAIGA_MOUNTAINS, Biome.MEGA_SPRUCE_TAIGA, Biome.MEGA_SPRUCE_TAIGA_HILLS,
				Biome.COLD_TAIGA, Biome.COLD_TAIGA_HILLS, Biome.COLD_BEACH, Biome.FROZEN_OCEAN, Biome.FROZEN_RIVER,
				Biome.ICE_PLAINS, Biome.ICE_PLAINS_SPIKES));
		bialias.put("RIVER", Arrays.asList(Biome.RIVER, Biome.BEACH));
		bialias.put("WET", Arrays.asList(Biome.SWAMPLAND, Biome.SWAMPLAND_MOUNTAINS));
		bialias.put("JUNGLE", Arrays.asList(Biome.JUNGLE, Biome.JUNGLE_EDGE, Biome.JUNGLE_EDGE_MOUNTAINS, Biome.JUNGLE_HILLS, Biome.JUNGLE_MOUNTAINS));
		bialias.put("MUSHROOM", Arrays.asList(Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_SHORE));
		bialias.put("HIGH", Arrays.asList(Biome.MESA, Biome.MESA_BRYCE, Biome.MESA_PLATEAU, Biome.MESA_PLATEAU_FOREST, Biome.MESA_PLATEAU_FOREST_MOUNTAINS,
					Biome.MESA_PLATEAU_MOUNTAINS, Biome.COLD_TAIGA_MOUNTAINS, Biome.ICE_MOUNTAINS));
		
		bialias.put("HELL", Arrays.asList(Biome.HELL));
		bialias.put("SKY", Arrays.asList(Biome.SKY));
	
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
	
	public boolean isOfBiomeType(String type, Biome biome){
		if (bialias.get(type).contains(biome)){
			return true;
		} else {
			return false;
		}
	}
}
