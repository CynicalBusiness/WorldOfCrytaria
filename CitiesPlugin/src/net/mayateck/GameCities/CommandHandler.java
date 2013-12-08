package net.mayateck.GameCities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor{
	Plugin plugin = null;
	
	public CommandHandler(Plugin p){
		plugin=p;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("city")){
			if (args.length!=0){
				
			} else {
				args[0]="help";
				onCommand(s, cmd, label, args);
			}
		}
		return false;
	}
}
