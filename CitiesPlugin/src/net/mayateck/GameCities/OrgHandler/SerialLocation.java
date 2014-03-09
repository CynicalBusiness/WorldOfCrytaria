package net.mayateck.GameCities.OrgHandler;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class SerialLocation implements Serializable{
	private static final long serialVersionUID = 4200472796408840628L;
	private double x,y,z;
	private String world;
	
	public void setLocation(Location loc){
		x=loc.getX();
		y=loc.getY();
		z=loc.getZ();
		world=loc.getWorld().getName();
	}
	
	public Location getLocation(Plugin plugin){
		World nworld = plugin.getServer().getWorld(world);
		return new Location(nworld,x,y,z);
	}

}
