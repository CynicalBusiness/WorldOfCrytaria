package net.mayateck.GameCities.OrgHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Organization {
	String name;
	String desc;
	String tag;
	
	double funds;
	
	public boolean isReal;
	
	List<String> relations;
	List<String> players;
	List<String> invited;
	
	HashMap<String,ConfigurationSection> groups;
	
	FileConfiguration cfg;
	
	/*
	 * Static class fetcher for getting city by player.
	 */
	public static Organization getOrganizationByPlayer(FileConfiguration cfg, String player, Plugin plug){
		Organization org = null;
		for (String key : cfg.getConfigurationSection("organizations").getKeys(false)){
			System.out.println("Tried to match "+player+" to organization "+key+".");
			if (cfg.getStringList("organizations."+key+".players").contains(player)){
				System.out.println("Match found! ("+key+")");
				org = new Organization(cfg, key);
				plug.saveConfig();
			} else {
				org = new Organization(cfg, null);
				System.out.println("Ignored this lookup, no match.");
			}
		}
		return org;
	}
	
	public Organization(FileConfiguration cfg, String name){
		if (name!=null){
			this.name=name;
			isReal=true;
			this.setCfg(cfg);
			pullDataFromDisk(name);
		} else {
			this.name="nil";
			this.setCfg(cfg);
			desc="";
			tag="";
			funds=0.0;
			relations=new ArrayList<String>();
			players=new ArrayList<String>();
			groups=new HashMap<String, ConfigurationSection>();
			isReal=false;
		}
	}
	
	public Organization(FileConfiguration cfg){
		this(cfg, null);
	}

	public String getName() {
		return name;
	}
	
	public void setConfig(FileConfiguration cfg){
		this.cfg=cfg;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public double getFunds() {
		return funds;
	}

	public void setFunds(double funds) {
		this.funds = funds;
	}

	public HashMap<String,ConfigurationSection> getGroups() {
		return groups;
	}
	
	@Override
	public String toString(){
		return toString(false);
	}
	
	public String toString(boolean inColor){
		if (inColor){
			return "&e["+getTag()+"]&o("+getName()+")&r - &7"+getDesc();
		} else {
			return "["+getTag()+"]("+getName()+") - "+getDesc();
		}
	}
	
	public boolean groupExists(String gname){
		if (groups.containsKey("organizations."+name+".groups."+gname)){
			return true;
		} else {
			return false;
		}
	}
	
	public void addGroup(String gname) {
		groups.put(gname, null);
	}
	
	public boolean deleteGroup(String gname) {
		if (groupExists(gname)){
			groups.remove(gname);
			return true;
		} else {
			return false;
		}
	}
	
	public String getPlayerGroup(String player){
		String playerGroup = "default";
		pullDataFromDisk(this.name);
		System.out.println("Found " + groups.size() + " groups.");
		for (String key : groups.keySet()){
			System.out.println("Checking group "+key);
			List<String> players = groups.get(key).getStringList("players");
			if (players.contains(player)){
				playerGroup = key;
			}
		}
		return playerGroup;
	}
	
	public boolean setPlayerGroup(String group, String player){
		if (group=="default"){
			String gname = getPlayerGroup(player);
			if (gname!=""){
				List<String> players = groups.get(gname).getStringList("players");
				players.remove(player);
				ConfigurationSection groupCfg = groups.get(gname);
				groupCfg.set("players", players);
				groups.put(gname, groupCfg);
				return true;
			} else {
				return false;
			}
		} else {
			setPlayerGroup(player, "default");
			if (groups.containsKey(group)){
				List<String> players = groups.get(group).getStringList("players");
				players.add(player);
				ConfigurationSection groupCfg = groups.get(group);
				groupCfg.set("players", players);
				groups.put(group, groupCfg);
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean isMod(String pname){
		List<String> players = groups.get("moderator").getStringList("players");
		if (players != null){
			if (players.contains(pname)){
				return true;
			}
		} 
		return false;
	}
	
	public boolean isAdmin(String pname){
		List<String> players = groups.get("admin").getStringList("players");
		if (players != null){
			if (players.contains(pname)){
				return true;
			}
		} 
		return false;
	}
	
	public boolean isOwner(String pname){
		String owner = cfg.getString("organizations."+getName()+".owner");
		if (owner.equalsIgnoreCase(pname)){
			return true;
		} else {
			return false;
		}
	}
	
	/*public boolean addGroupPermission(String group, String permission){
		if (groupExists(group)){
			List<String> perms = groups.get(group).getStringList("permissions");
			ConfigurationSection groupCfg = groups.get(group);
			perms.add(permission);
			groupCfg.set("permissions", perms);
			groups.put(group, groupCfg);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delGroupPermission(String group, String permission){
		if (groupExists(group)){
			List<String> perms = groups.get(group).getStringList("permissions");
			ConfigurationSection groupCfg = groups.get(group);
			perms.remove(permission);
			groupCfg.set("permissions", perms);
			groups.put(group, groupCfg);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean groupHasPermission(String group, String permission){
		if (groupExists(group)){
			// DEBUG
			List<String> gperms = groups.get(group).getStringList("perms");
			for (String s : gperms){
				GameCities.logger.info("Checked "+permission+" to "+s);
			}
			if (gperms.contains(permission)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean groupHasPermission(String group, List<String> permission, boolean isAll){
		boolean hasOne = false;
		boolean hasAll = true;
		permission.add("'*'");
		for (String val : permission){
			boolean hasPerm = groupHasPermission(group, val);
			if (hasPerm==true){
				hasOne=true;
			} else {
				hasAll=false;
			}
		}
		if (isAll){
			return hasAll;
		} else {
			return hasOne;
		}
	}*/

	public Relation getRelationsWith(String orgname) {
		String keyString = "";
		for (String key : relations){
			String nameChk = key.substring(0, key.indexOf("|")-1);
			if (nameChk==orgname){keyString=key;}
		}
		if (keyString!=""){
			String strr = keyString.substring(keyString.indexOf("|")+1);
			if (Relation.valueOf(strr)!=null){return Relation.valueOf(strr);} else {return Relation.NULL;}
		} else {
			return Relation.NEUTRAL;
		}
	}

	public void setRelationsWith(String orgName, Relation relation) {
		String keyString = "";
		for (String key : relations){
			String nameChk = key.substring(0, key.indexOf("|"));
			if (nameChk.equalsIgnoreCase(orgName)){keyString=key;}
		}
		if (keyString==""){
			String strr = orgName+"|"+relation.toString();
			relations.add(strr);
			writeDataToDisk(getName());
		} else {
			relations.remove(keyString);
			writeDataToDisk(getName());
			setRelationsWith(orgName, relation);
		}
	}
	
	public List<String> getOrganizationsWithRelation(Relation relation){
		List<String> organizations = new ArrayList<String>();
		if (relation==Relation.NEUTRAL){
			organizations.add("Neutral is the default relation!");
			organizations.add("Looking for a list? Try /org list!");
			organizations.add(".nchk"); // Helps the CommandHandler check for this outcome.
		} else {
			pullDataFromDisk(getName());
			for (String key : relations){
				if (key.substring(key.indexOf("|")+1).equalsIgnoreCase(relation.toString().toUpperCase())){
					organizations.add(key.substring(0, key.indexOf("|")));
				}
			}
		}
		return organizations;
	}

	public List<String> getPlayers() {
		return players;
	}
	
	public List<String> getInvited() {
		return players;
	}
	
	public void delPlayer(String player){
		players.remove(player);
		writeDataToDisk(getName());
	}

	public void addPlayer(String player){
		players.add(player);
		writeDataToDisk(getName());
	}
	
	public void addInvited(String player){
		players.add(player);
		writeDataToDisk(getName());
	}
	
	public void delInvited(String player){
		players.remove(player);
		writeDataToDisk(getName());
	}
	
	public void setCfg(FileConfiguration cfg) {
		this.cfg = cfg;
	}
	
	public void writeDataToDisk(String name){
		cfg.set("organizations."+name+".desc", desc);
		cfg.set("organizations."+name+".tag", tag);
		cfg.set("organizations."+name+".funds", funds);
		cfg.set("organizations."+name+".relations", relations);
		cfg.set("organizations."+name+".players", players);
		cfg.set("organizations."+name+".invited", invited);
		
		for (String key : groups.keySet()){
			cfg.set("organizations."+name+".groups."+key, groups.get(key));
		}
		
	}
	
	public void pullDataFromDisk(String name){
		if (cfg.contains("organizations."+name)){
			desc = cfg.getString("organizations."+name+".desc");
			tag = cfg.getString("organizations."+name+".tag");
			funds = cfg.getDouble("organizations."+name+".funds");
			relations = cfg.getStringList("organizations."+name+".relations");
			players = cfg.getStringList("organizations."+name+".players");
			invited = cfg.getStringList("organizations."+name+".invited");
			
			if (groups!=null){
				groups=null;
			}
			groups = new HashMap<String, ConfigurationSection>();
			for (String key : cfg.getConfigurationSection("organizations."+name+".groups").getKeys(false)){
				groups.put(key, cfg.getConfigurationSection("organizations."+name+".groups."+key));
				System.out.println("Put organizations."+name+".groups."+key);
			}
		} else {
			System.out.println("Failed to find "+name+" in config.");
			isReal=false;
		}
	}
}
