package net.mayateck.GameMisc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class PhysicsHandler implements Listener{
public Plugin plugin;
	
	public PhysicsHandler(Plugin p){
		plugin=p;
	}
	
	@EventHandler
	public void onBlockPhysicsUpdate(BlockPhysicsEvent e){
		update(e.getBlock(), true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		update(e.getBlock(), true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		update(e.getBlock(), false);
	}
	
	public void update(Block blk, boolean doSelf){
		Location l = blk.getLocation();
		List<Block> blocks = Arrays.asList(blk,
				(new Location(l.getWorld(), l.getX()+1, l.getY(), l.getZ())).getBlock(),
				(new Location(l.getWorld(), l.getX()-1, l.getY(), l.getZ())).getBlock(),
				(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()+1)).getBlock(),
				(new Location(l.getWorld(), l.getX(), l.getY(), l.getZ()-1)).getBlock(),
				(new Location(l.getWorld(), l.getX(), l.getY()+1, l.getZ())).getBlock(),
				(new Location(l.getWorld(), l.getX(), l.getY()-1, l.getZ())).getBlock());
		for (Block b : blocks){
			if (!(b.isEmpty() || b.isLiquid())){
				try {
					checkToFall(b);
				} catch (BlockFloatingException e) {
					fellBlock(b);
				}
			}
		}
	}
	public void update(List<Block> bks, boolean doSelf){
		for (Block b : bks){
			update(b, doSelf);
		}
	}
	
	public void checkToFall(Block b) throws BlockFloatingException{
		
	}
	
	public void fellBlock(Block b){
		Location loc = b.getLocation();
		Material type = b.getType();
		b.setType(Material.AIR);
		b.getWorld().spawnFallingBlock(loc, type, (byte) 0);
	}
}
