package net.mayateck.GameMisc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor{
	public enum CommandOutput{
		SUCCESS, NULL, BAD_SENDER, BAD_ARG_COUNT, BAD_ARG,
		NO_PERMISSION, ALREADY_EXISTS, MUST_LEAVE, NO_ORG_PERMISSION,
		BAD_ORG, NOT_IN_ORG, TODO
	}
	
	Plugin plugin = null;
	List<String> helpList = Arrays.asList(
			"&e/org create [name] &r- &7Founds a new organization.");
	
	public CommandHandler(Plugin p){
		plugin=p;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		switch (parseCommand(s, cmd, label, args)){
		case NULL:
			plugin.getLogger().info("onCommand fired from "+s.getName()+" with null return.");
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Woah, a &cprogramming error&r occurred. &oPlease let a dev know!&r"));
			break;
		case SUCCESS:
			plugin.getLogger().info("onCommand fired from "+s.getName()+" with success.");
			//s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Command succeeded!"));
			break;
		case BAD_SENDER:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Sorry, this command cannot be run from this sender."));
			break;
		case BAD_ARG_COUNT:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Bad argument count. Try &7/org help &ffor syntax"));
			break;
		case BAD_ARG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Bad argument. Try &7/org help &ffor syntax."));
			break;
		case NO_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Sorry, you don't have the ability to do this."));
			break;
		case ALREADY_EXISTS:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Sorry, something you tried to make already exists."));
			break;
		case MUST_LEAVE:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"You can't do that while you're in an organization."));
			break;
		case NO_ORG_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"Sorry, your organization doesn't permit you to do that."));
			break;
		case BAD_ORG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"That organization doesn't exist."));
			break;
		case NOT_IN_ORG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"You're not in an organization!"));
			break;
		case TODO:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameMisc.tag+"&9TODO&r."));
			break;
		}
		return true;
	}
	
	public CommandOutput parseCommand(CommandSender s, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("announce")){
			if (args.length!=0){
				Player[] players = plugin.getServer().getOnlinePlayers();
				String msg = "";
				for (String arg : args){
					msg += " "+arg;
				}
				for (Player pl : players){
					pl.sendMessage(ChatColor.YELLOW+"[Announcement]: "+msg);
				}
				return CommandOutput.SUCCESS;
			} else {
				return CommandOutput.BAD_ARG_COUNT;
			}
		}
		return CommandOutput.NULL;
	}
	
}
