package net.mayateck.GameCities;

import org.bukkit.plugin.java.JavaPlugin;

public class GameCities extends JavaPlugin {
	public static final String head = "&8&l||============|[&eCrytarian Cities&8&l]|============||&r";
	public static final String tag = "&8&l[&eWoC Cities&8&l]&r ";
	public static String version = "";
	
	@Override
	public void onEnable(){
		CommandHandler cmdh = new CommandHandler(this);
		version = this.getDescription().getVersion();
		saveDefaultConfig();
		getCommand("city").setExecutor(cmdh);
		
		getLogger().info("Enabled Cities.");
	}
	
	@Override
	public void onDisable(){
		
		getLogger().info("Disabled Cities");
	}
}
