package net.mayateck.GameCities;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.mayateck.GameCities.OrgHandler.Organization;
import net.mayateck.GameCities.OrgHandler.ProtectionHandler;
import net.mayateck.GameCities.OrgHandler.Relation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandHandler implements CommandExecutor{
	public enum CommandOutput{
		SUCCESS, NULL, BAD_SENDER, BAD_ARG_COUNT, BAD_ARG,
		NO_PERMISSION, ALREADY_EXISTS, ALREADY_JOINED, MUST_LEAVE, NO_ORG_PERMISSION,
		BAD_ORG, NOT_IN_ORG, TODO
	}
	
	Plugin plugin = null;
	ProtectionHandler ph = null;
	List<String> helpList = Arrays.asList(
			"&e/org create <name> &r- &7Founds a new organization.",
			"&e/org disband &r- &7&cDisbands&7 your organization.",
			"&e/org set <obj> [args] &r- &7Sets organization properties.",
			"&e/org relations <cmd> [args] &r- &7Sets and views relation(s).",
			"&e/org list [page] &r- &7Lists all organizations.");
	
	public CommandHandler(Plugin p, ProtectionHandler ph){
		plugin=p;
		this.ph=ph;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		switch (parseCommand(s, cmd, label, args)){
		case NULL:
			plugin.getLogger().info("onCommand fired from "+s.getName()+" with null return.");
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Woah, a &cprogramming error&r occurred. &oPlease let a dev know!&r"));
			break;
		case SUCCESS:
			plugin.getLogger().info("onCommand fired from "+s.getName()+" with success.");
			//s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Command succeeded!"));
			break;
		case BAD_SENDER:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, this command cannot be run from this sender."));
			break;
		case BAD_ARG_COUNT:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Bad argument count. Try &7/org help &ffor syntax"));
			break;
		case BAD_ARG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Bad argument. Try &7/org help &ffor syntax."));
			break;
		case NO_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, you don't have the ability to do this."));
			break;
		case ALREADY_EXISTS:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, something you tried to make already exists."));
			break;
		case ALREADY_JOINED:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"That player has already joined."));
			break;
		case MUST_LEAVE:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"You can't do that while you're in an organization."));
			break;
		case NO_ORG_PERMISSION:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Sorry, your organization doesn't permit you to do that."));
			break;
		case BAD_ORG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"That organization doesn't exist."));
			break;
		case NOT_IN_ORG:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"You're not in an organization!"));
			break;
		case TODO:
			s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"&9TODO&r."));
			break;
		}
		return true;
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
				} else if (args[0].equalsIgnoreCase("list")){
					if (args.length == 2){
						int page = (Integer.parseInt(args[1])-1)*10;
						int fpage = page+10;
						Set<String> orgs = plugin.getConfig().getConfigurationSection("organizations").getKeys(false);
						int pages = (int) Math.ceil(orgs.size()/10);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.head));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Displaying page &e"+(page+1)+"&7 of &e"+(pages+1)+"&7."));
						for (int i = page; i < fpage; i++){
							if (orgs.size()<page){
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7There are no organizations to display."));
								return CommandOutput.SUCCESS;
							} else {
								String[] orgsa = orgs.toArray(new String[10]);
								try {
									String orgn = orgsa[i];
									Organization org = new Organization(plugin.getConfig(), orgn);
									if (org.isReal){
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', org.toString(true)));
									}
								} catch (ArrayIndexOutOfBoundsException e){
									// Just trying to avoid an error throw.
									plugin.getLogger().info("Reached end of bounds for array of organizations");
									return CommandOutput.SUCCESS;
								}
							} 
						} return CommandOutput.SUCCESS;
					} else if(args.length == 1) {
						String[] newargs = {"list", "1"};
						return parseCommand(s,cmd,label,newargs);
					} else {
						return CommandOutput.BAD_ARG_COUNT;
					}
				} else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("new")){
					if (isPlayer){
						if (args.length==2){
							if (s.hasPermission("organizations.create")){
								Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
								if (org.isReal==false){
									String orgName = args[1].toLowerCase();
									if (orgName.length()>plugin.getConfig().getInt("config.ORGNAME_MIN_LENGTH") && orgName.length()<plugin.getConfig().getInt("config.ORGNAME_MAX_LENGTH") && orgName.matches("^[A-Za-z_]{1,}$")){
										if (!plugin.getConfig().contains("organizations."+orgName)){
											plugin.getConfig().set("organizations."+orgName+".owner", p.getName());
											plugin.getConfig().set("organizations."+orgName+".groups.admin.players", Arrays.asList(p.getName()));
											plugin.getConfig().set("organizations."+orgName+".groups.moderator.players", Arrays.asList());
											plugin.getConfig().set("organizations."+orgName+".tag", orgName);
											plugin.getConfig().set("organizations."+orgName+".desc", "");
											plugin.getConfig().set("organizations."+orgName+".funds", 0.0);
											plugin.getConfig().set("organizations."+orgName+".relations", "");
											plugin.getConfig().set("organizations."+orgName+".invited", Arrays.asList());
											plugin.getConfig().set("organizations."+orgName+".players", Arrays.asList(p.getName()));
											plugin.saveConfig();
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
						Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
						if (args[1].equalsIgnoreCase("tag")){
							if (args.length==3){
								String pgroup = org.getPlayerGroup(p.getName());
								plugin.getLogger().info("Player is in group "+pgroup);
								if (org.isAdmin(p.getName()) || org.isMod(p.getName())){
									if (args[2].length()>plugin.getConfig().getInt("config.ORGNAME_MIN_LENGTH") && args[2].length()<plugin.getConfig().getInt("config.ORGNAME_MAX_LENGTH") && args[2].matches("^[A-Za-z_]{1,}$")){
										org.setTag(args[2]);
										org.writeDataToDisk(org.getName());
										plugin.saveConfig();
										return CommandOutput.SUCCESS;
									} else {
										return CommandOutput.BAD_ARG;
									}
								} else {
									return CommandOutput.NO_ORG_PERMISSION;
								}
							} else {
								return CommandOutput.BAD_ARG_COUNT;
							}
						} else if (args[1].equalsIgnoreCase("desc")){
							if (args.length > 2){
								if (org.isAdmin(p.getName()) || org.isMod(p.getName())){
									String desc = "";
									int i = 1;
									for (String arg : args){
										if (i>2){
											desc+=arg+" ";
										}
										i++;
									}
									desc.replaceAll("'", "''");
									desc.replaceAll("&", "_");
									org.setDesc(desc);
									org.writeDataToDisk(org.getName());
									plugin.saveConfig();
									return CommandOutput.SUCCESS;
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
				} else if (args[0].equalsIgnoreCase("relations")){
					Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
					if (org.isReal){
						if (args.length>1){
							if (args[1].equalsIgnoreCase("view")){
								if (args.length==4){
									if (args[2].equalsIgnoreCase("org") || args[2].equalsIgnoreCase("organization")){
										Organization reqorg = new Organization(plugin.getConfig(), args[3].toLowerCase());
										if (reqorg.isReal==true){
											Relation rel = org.getRelationsWith(args[3].toLowerCase());
											s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Your organiation is "+rel.toString()+" toward "+args[3]+"."));
											return CommandOutput.SUCCESS;
										} else {
											return CommandOutput.BAD_ORG;
										}
									} else if (args[2].equalsIgnoreCase("relation")){
										Relation rel = Relation.valueOf(args[3].toUpperCase());
										if (rel!=null || rel!=Relation.NULL){
											List<String> orgs = org.getOrganizationsWithRelation(rel);
											if (!orgs.contains(".nchk")){
												if (orgs.size()>0){
													s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Your organization's &f"+args[3].toUpperCase()+ "&7 relations are:"));
													for (String str : orgs){
														Organization strorg = new Organization(plugin.getConfig(), str);
														String echoString = "";
														if (strorg.isReal==true){
															if (strorg.getTag().equalsIgnoreCase("")){
																echoString += " &7- &e"+str+"&r";;
															} else {
																echoString += " &7- &e"+strorg.getTag()+" &r&o("+str+")&r";
															}
														} else {
															echoString += " &7- &n"+str+" - &r&o(Disbanded)&r";
														}
														s.sendMessage(ChatColor.translateAlternateColorCodes('&', echoString));
													}
												} else {
													s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Your organization has no &f"+args[3].toUpperCase()+ "&7 relations."));
												}
											} else {
												s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+orgs.get(0)));
												s.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+orgs.get(1)));
											}
											return CommandOutput.SUCCESS;
										} else {
											return CommandOutput.BAD_ARG;
										}
									} else {
										return CommandOutput.BAD_ARG;
									}
								} else {
									return CommandOutput.BAD_ARG_COUNT;
								}
							} else if (args[1].equalsIgnoreCase("set")){
								if (args.length==4){
									Relation rel = null;
									try {
										 rel = Relation.valueOf(args[3].toUpperCase());
									} catch (IllegalArgumentException e){
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', "The relation "+args[3].toUpperCase()+" is not valid."));
									}
									if (rel!=null){
										Organization corg = new Organization(plugin.getConfig(), args[2]);
										if (corg.isReal){
											if (org.isMod(p.getName()) || org.isAdmin(p.getName())){
												if (org.getName().equalsIgnoreCase(corg.getName())){
													org.setRelationsWith(corg.getName(), rel);
													p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+
															"Relation with &e"+corg.getName()+"&7 set to &f"+rel.toString().toUpperCase()+"&7."));
													org.writeDataToDisk(org.getName());
													plugin.saveConfig();
													return CommandOutput.SUCCESS;
												} else {
													return CommandOutput.BAD_ORG;
												}
											} else {
												return CommandOutput.NO_ORG_PERMISSION;
											}
										} else {
											return CommandOutput.BAD_ORG;
										}
									} else {
										return CommandOutput.BAD_ARG;
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
					} else {
						return CommandOutput.NOT_IN_ORG;
					}
				} else if (args[0].equalsIgnoreCase("fortify")) {
					Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
					if (org.isReal){
						if (args.length<3){
							String group = "";
							if (args.length==2){
								if (org.groupExists(args[1])){
									group=args[1];
								} else {
									return CommandOutput.BAD_ARG;
								}
							} else {
								group = org.getName();
							}
							if (org.isAdmin(p.getName()) || org.isMod(p.getName())){
								if (ph.protPlayers.containsKey(p.getName())){
									ph.protPlayers.remove(p.getName());
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"You are no longer in fortify mode."));
								} else {
									ph.protPlayers.put(p.getName(), group.toLowerCase());
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Now fortifying for group '"+group+"'."));
								}
								return CommandOutput.SUCCESS;
							} else {
								return CommandOutput.NO_ORG_PERMISSION;
							}
						} else {
							return CommandOutput.BAD_ARG_COUNT;
						}
					} else {
						return CommandOutput.NOT_IN_ORG;
					}
				} else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("disband")) {
					Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
					if (org.isReal){
						if (org.isOwner(p.getName())){
							plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe organization of "+org.getTag()+"("+org.getName()+") was disbanded."));
							String oname = org.getName();
							plugin.getConfig().set("organizations."+oname, null);
							plugin.saveConfig();
							return CommandOutput.SUCCESS;
						} else {
							return CommandOutput.NO_ORG_PERMISSION;
						}
					} else {
						return CommandOutput.NOT_IN_ORG;
					}
				} else if (args[0].equalsIgnoreCase("invite")){
					if (args.length==2){
						Organization org = Organization.getOrganizationByPlayer(plugin.getConfig(), p.getName(), plugin);
						if (org.isReal){
							if (org.isAdmin(p.getName()) || org.isMod(p.getName())){
								String inv = args[1];
								if (!org.getPlayers().contains(inv) && !org.getInvited().contains(inv)){
									org.addInvited(inv);
									p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Successfully invited &e"+inv+"&7."));
									return CommandOutput.SUCCESS;
								} else {
									if (org.getInvited().contains(inv)){
										org.delInvited(inv);
										p.sendMessage(ChatColor.translateAlternateColorCodes('&', GameCities.tag+"Revoked &e"+inv+"&7's invite."));
										return CommandOutput.SUCCESS;
									}
										return CommandOutput.ALREADY_JOINED;
								}
							} else {
								return CommandOutput.NO_PERMISSION;
							}
						} else {
							return CommandOutput.NOT_IN_ORG;
						}
					} else {
						return CommandOutput.BAD_ARG_COUNT;
					}
				} else if (args[0].equalsIgnoreCase("join")){
					return CommandOutput.TODO;
				} else {
					return CommandOutput.BAD_ARG;
				}
			} else {
				String[] newarg = {"help"};
				return parseCommand(s, cmd, label, newarg);
			}
		}
		return CommandOutput.NULL;
	}
	
}
