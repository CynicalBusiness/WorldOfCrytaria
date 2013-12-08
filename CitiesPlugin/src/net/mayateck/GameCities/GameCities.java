package net.mayateck.GameCities;

import org.bukkit.plugin.java.JavaPlugin;

public class GameCities extends JavaPlugin {
	@Override
	public void onEnable(){
		CommandHandler cmdh = new CommandHandler(this);
		getCommand("city").setExecutor(cmdh);
		
		getLogger().info("Enabled Cities.");
	}
	
	@Override
	public void onDisable(){
		
		getLogger().info("Disabled Cities");
	}
}
