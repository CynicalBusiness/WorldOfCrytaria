package net.mayateck.GameCities;

import java.util.Arrays;
import java.util.List;

import net.mayateck.GameCities.OrgHandler.Organization;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor{
	public enum CommandOutput{
		SUCCESS, NULL, BAD_SENDER, BAD_ARG_COUNT, BAD_ARG,
		NO_PERMISSION, ALREADY_EXISTS, MUST_LEAVE, NO_ORG_PERMISSION
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
		case SUCCESS:
			plugin.getLogger().info("onCommand fired from "+s.getName()+" with success.");
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Command succeeded!"));
		case BAD_SENDER:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, this command cannot be run from this sender."));
		case BAD_ARG_COUNT:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Bad argument count. Try &7/org help &ffor syntax"));
		case BAD_ARG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Bad argument. Try &7/org help &ffor syntax."));
		case NO_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, you don't have the ability to do this."));
		case ALREADY_EXISTS:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, something you tried to make already exists."));
		case MUST_LEAVE:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"You can't do that while you're in an organization."));
		case NO_ORG_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, your organization doesn't permit you to do that."));
		}
		return false;
	}
	
	public CommandOutput parseCommand(CommandSender s, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("organization")){
			if (args.length!=0){
				boolean isPlayer = false;
				Player p = null;
				if (s instanceof Player){
					p = plugin.getServer().getPlayer(s.getName());
					isPlayer = true;
				}
				if (args[0].equalsIgnoreCase("help")){
					if (isPlayer){
						s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.head));
						for (String msg : helpList){
							s.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
						}
						return CommandOutput.SUCCESS;
					} else {
						return CommandOutput.BAD_SENDER;
					}
				} else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("new")){
					if (isPlayer){
						if (args.length==2){
							if (s.hasPermission("organizations.create")){
								Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName());
								if (org.isReal==false){
									String orgName = args[1].toLowerCase();
									if (orgName.length()>plugin.getConfig().getInt("config.ORGNAME_MIN_LENGTH") && orgName.length()<plugin.getConfig().getInt("config.ORGNAME_MAX_LENGTH") && orgName.matches("^[A-Za-z_]{1,}$")){
										if (!plugin.getConfig().contains("data.organizations."+orgName)){
											plugin.getConfig().set("organizations."+orgName+".owner", p.getName());
											plugin.getConfig().set("organizations."+orgName+".groups.admin.players", Arrays.asList(p.getName()));
											plugin.getConfig().set("organizations."+orgName+".groups.admin.permissions", Arrays.asList("edit.*", "ranks.*", "econ.*", "research.*"));
											plugin.getConfig().set("organizations."+orgName+".groups.moderator.players", Arrays.asList());
											plugin.getConfig().set("organizations."+orgName+".groups.moderator.permissions", Arrays.asList("edit.kitmod", "ranks.kitmod", "econ.kitmod", "research.*"));
											plugin.getConfig().set("organizations."+orgName+".groups.default.permissions", Arrays.asList("econ.kitcitizen", "research.kitcitizen"));
											plugin.getConfig().set("organizations."+orgName+".tag", orgName);
											plugin.getConfig().set("organizations."+orgName+".desc", "");
											plugin.getConfig().set("organizations."+orgName+".funds", 0.0);
											plugin.getConfig().set("organizations."+orgName+".relations", "");
											return CommandOutput.SUCCESS;
										} else {
											return CommandOutput.ALREADY_EXISTS;
										}
									} else {
										return CommandOutput.BAD_ARG;
									}
								} else {
									return CommandOutput.MUST_LEAVE;
								}
							} else {
								return CommandOutput.NO_PERMISSION;
							}
						} else {
							return CommandOutput.BAD_ARG_COUNT;
						}
					} else {
						return CommandOutput.BAD_SENDER;
					}
				} else if (args[0].equalsIgnoreCase("set")){
					if (args.length>1){
						Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName());
						if (args[1].equalsIgnoreCase("tag")){
							if (args.length==3){
								List<String> perms = Arrays.asList("edit.tag", "edit.*", "edit.kitmod");
								if (org.groupHasPermission(org.getPlayerGroup(p.getName()), perms, false)){
									if (args[2].length()>plugin.getConfig().getInt("config.ORGNAME_MIN_LENGTH") && args[2].length()<plugin.getConfig().getInt("config.ORGNAME_MAX_LENGTH") && args[2].matches("^[A-Za-z_]{1,}$")){
										org.setTag(args[2]);
										org.writeDataToDisk();
										plugin.saveConfig();
									} else {
										return CommandOutput.BAD_ARG;
									}
								} else {
									return CommandOutput.NO_ORG_PERMISSION;
								}
							} else {
								return CommandOutput.BAD_ARG_COUNT;
							}
						} else {
							return CommandOutput.BAD_ARG;
						}
					} else {
						return CommandOutput.BAD_ARG_COUNT;
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					
				} else {
					return CommandOutput.BAD_ARG;
				}
			} else {
				args[0]="help";
				onCommand(s, cmd, label, args);
				return CommandOutput.NULL;
			}
		}
		return CommandOutput.NULL;
	}
	
}
