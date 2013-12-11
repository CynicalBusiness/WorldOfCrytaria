package net.mayateck.GameCities.OrgHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Organization {
	String name;
	String desc;
	String tag;
	
	double funds;
	
	public boolean isReal;
	
	List<String> relations;
	List<String> players;
	
	HashMap<String,ConfigurationSection> groups;
	
	FileConfiguration cfg;
	
	/*
	 * Static class fetcher for getting city by player.
	 */
	public static Organization getOrganizationByPlayer(FileConfiguration cfg, String player){
		for (String key : cfg.getConfigurationSection("organizations").getKeys(false)){
			if (cfg.getStringList("organizations."+key+".players").contains(player)){
				return new Organization(cfg, key);
			}
		}
		return null;
	}
	
	public Organization(FileConfiguration cfg, String name){
		if (name!=null){
			this.name=name;
			isReal=true;
			pullDataFromDisk();
		} else {
			this.name=name;
			desc="";
			tag="";
			funds=0.0;
			relations=Arrays.asList();
			players=Arrays.asList();
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
		String playerGroup = "";
		for (String key : groups.keySet()){
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
	
	public boolean addGroupPermission(String group, String permission){
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
			if (groups.get(group).getStringList("perms").contains(permission)){
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
	}

	public Relation getRelationsWith(String orgname) {
		String keyString = "";
		for (String key : relations){
			String nameChk = key.substring(0, key.indexOf(":")-1);
			if (nameChk==orgname){keyString=key;}
		}
		if (keyString!=""){
			String strr = keyString.substring(keyString.indexOf(":")+1);
			if (Relation.valueOf(strr)!=null){return Relation.valueOf(strr);} else {return Relation.NULL;}
		} else {
			return Relation.NEUTRAL;
		}
	}

	public void setRelationsWith(String orgName, Relation relation) {
		String keyString = "";
		for (String key : relations){
			String nameChk = key.substring(0, key.indexOf(":")-1);
			if (nameChk==orgName){keyString=key;}
		}
		if (keyString==""){
			String strr = orgName+":"+relation.toString();
			relations.add(strr);
		} else {
			relations.remove(keyString);
			setRelationsWith(orgName, relation);
		}
	}
	
	public List<String> getOrganizationsWithRelation(Relation relation){
		List<String> organizations = Arrays.asList();
		if (relation==Relation.NEUTRAL){
			organizations.add("You are automatically neutral with non-related nations.");
		} else {
			for (String key : relations){
				if (key.substring(key.indexOf(":")+1).equalsIgnoreCase(relations.toString())){
					organizations.add(key.substring(0, key.indexOf(":")-1));
				}
			}
		}
		return organizations;
	}

	public List<String> getPlayers() {
		return players;
	}
	
	public void delPlayer(String player){
		players.remove(player);
	}

	public void addPlayer(String player){
		players.add(player);
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
		
		for (String key : groups.keySet()){
			cfg.set("organizations."+name+".groups."+key, groups.get(key));
		}
		
	} public void writeDataToDisk(){writeDataToDisk(name);}
	
	public void pullDataFromDisk(String name){
		this.name=name;
		
		desc = cfg.getString("organizations."+name+".desc");
		tag = cfg.getString("organizations."+name+".tag");
		funds = cfg.getDouble("organizations."+name+".funds");
		relations = cfg.getStringList("organizations."+name+".relations");
		players = cfg.getStringList("organizations."+name+".players");
		
		groups.clear();
		for (String key : cfg.getConfigurationSection("organizations."+name+".groups").getKeys(false)){
			groups.put(key, cfg.getConfigurationSection("organizations."+name+".groups."+key));
		}
	} public void pullDataFromDisk(){pullDataFromDisk(name);}
}
