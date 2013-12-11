package net.mayateck.GameCities;

import net.mayateck.GameCities.OrgHandler.DataFetch;
import net.mayateck.GameCities.OrgHandler.ProtectionHandler;

import org.bukkit.plugin.java.JavaPlugin;

public class GameCities extends JavaPlugin {
	public static final String head = "&8&l||============|[&eCrytarian Organizations&8&l]|============||&r";
	public static final String tag = "&8&l[&eWoC Orgs&8&l]&r ";
	public static String version = "";
	
	public DataFetch fetcher = null;
	public ProtectionHandler protEventHandler = null;
	
	@Override
	public void onEnable(){
		fetcher = new DataFetch(this);
		protEventHandler = new ProtectionHandler(this);
		CommandHandler cmdh = new CommandHandler(this, protEventHandler);
		getServer().getPluginManager().registerEvents(protEventHandler, this);
		version = this.getDescription().getVersion();
		saveDefaultConfig();
		getCommand("city").setExecutor(cmdh);
		
		getLogger().info("Enabled Cities.");
	}
	
	@Override
	public void onDisable(){
		
		saveConfig();
		getLogger().info("Disabled Cities");
	}
	
	public ProtectionHandler getProtHandler(){
		return protEventHandler;
	}
}
