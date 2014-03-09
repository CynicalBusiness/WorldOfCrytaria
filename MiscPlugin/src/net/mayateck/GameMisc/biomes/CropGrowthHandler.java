package net.mayateck.GameMisc.biomes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

import net.mayateck.GameMisc.GameMisc;

public class CropGrowthHandler implements Listener{
	
	public GameMisc plugin;
	
	public CropGrowthHandler(GameMisc plugin){
		this.plugin = plugin;
	}
		
	
	@EventHandler
	public void onAnimalGrowth(CreatureSpawnEvent e){
		if (e.getSpawnReason()==SpawnReason.BREEDING){
			Entity en = e.getEntity();
			Block bl = en.getLocation().getBlock();
			Biome biome = bl.getBiome();
			if (bl.getLightFromSky()>4){
				double cp = 0;
				if (en.getType()==EntityType.PIG){
					if (plugin.isOfBiomeType("RIVER", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
				} else if (en.getType()==EntityType.COW){
					if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
				} else if (en.getType()==EntityType.SHEEP){
					if (plugin.isOfBiomeType("HIGH", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("COLD", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.5;}
				} else if (en.getType()==EntityType.MUSHROOM_COW){
					if (plugin.isOfBiomeType("WET", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.15;}
				} else if (en.getType()==EntityType.CHICKEN){
					if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.15;} 
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
				} else if (en.getType()==EntityType.HORSE){
					if (plugin.isOfBiomeType("PLAINS", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
				}
				double nc = Math.random();
				if (nc > cp){
					e.setCancelled(true);
				}
			} else {
				e.setCancelled(true);
			}
		} else if (e.getSpawnReason()==SpawnReason.EGG){
			if (e.getEntityType()==EntityType.CHICKEN){
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onTreeGrowth(StructureGrowEvent e){
		double chance = Math.random();
		Block bl = e.getLocation().getBlock();
		Biome biome = bl.getBiome();
		if (chance <= 0.25 && (bl.getLightFromSky()>=4 || isUnderGreenhouse(bl, true))){
			if (!isUnderGreenhouse(bl, true)){
				double cp = 0;
				if (bl.getType()==Material.SAPLING){
					if (bl.getData()==0){ // Oak
						if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.15;}
						else if (plugin.isOfBiomeType("WET", biome)){cp=0.25;}
						else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
						else if (plugin.isOfBiomeType("COLD", biome)){cp=0.15;}
						else if (plugin.isOfBiomeType("FOREST", biome)){cp=1.0;}
					} else if (bl.getData()==1){ // Pine
						if (plugin.isOfBiomeType("HIGH", biome)){cp=0.75;}
						else if (plugin.isOfBiomeType("COLD", biome)){cp=0.25;}
					} else if (bl.getData()==2){ // Birch
						if (plugin.isOfBiomeType("FOREST", biome)){cp=1.0;}
					} else if (bl.getData()==3){ // Jungle
						if (plugin.isOfBiomeType("JUNGLE", biome)){cp=1.0;}
					} else if (bl.getData()==4){ // Savanna
						if (plugin.isOfBiomeType("DESERT", biome)){cp=0.25;}
						else if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.75;}
					} else if (bl.getData()==5){ // Dark Oak
						if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.15;}
						else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.25;}
						else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.5;}
					}
				}
				double nc = Math.random();
				if (nc > cp){
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onCropGrowth(BlockGrowEvent e){
		double chance = Math.random();
		Block bl = e.getBlock();
		Biome biome = bl.getBiome();
		if (chance <= 0.25 && (bl.getLightFromSky()>=4 || isUnderGreenhouse(bl, false))){ // 25% base reduction and light/greenhouse checks.
			if (!isUnderGreenhouse(bl, false)){
				if (bl.getType()==Material.WHEAT){
					double cp = 0;
					if (plugin.isOfBiomeType("DESERT", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("PLAINS", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.SUGAR_CANE_BLOCK){
					double cp = 0;
					if (plugin.isOfBiomeType("OCEAN", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("DESERT", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.5;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.MELON_BLOCK){
					double cp = 0;
					if (plugin.isOfBiomeType("RIVER", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.5;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.PUMPKIN){
					double cp = 0;
					if (plugin.isOfBiomeType("HIGH", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("COLD", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=1.0;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.CACTUS){
					double cp = 0;
					if (plugin.isOfBiomeType("DESERT", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.25;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.CARROT){
					double cp = 0;
					if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.75;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.POTATO){
					double cp = 0;
					if (plugin.isOfBiomeType("DESERT", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("PLAINS", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("RIVER", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("COLD", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("SKY", biome)){cp=1.0;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.COCOA){
					double cp = 0;
					if (plugin.isOfBiomeType("JUNGLE", biome)){cp=1.0;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.BROWN_MUSHROOM){
					double cp = 0;
					if (plugin.isOfBiomeType("HIGH", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("WET", biome)){cp=0.75;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.15;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=1.0;}
					else if (plugin.isOfBiomeType("FOREST", biome)){cp=0.15;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.RED_MUSHROOM){
					double cp = 0;
					if (plugin.isOfBiomeType("WET", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("JUNGLE", biome)){cp=0.25;}
					else if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=1.0;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				} else if (bl.getType()==Material.NETHER_STALK){
					double cp = 0;
					if (plugin.isOfBiomeType("MUSHROOM", biome)){cp=0.5;}
					else if (plugin.isOfBiomeType("HELL", biome)){cp=1.0;}
					double nc = Math.random();
					if (nc > cp){
						e.setCancelled(true);
					}
				}
			}
		} else {
			e.setCancelled(true);
		}
	}
	
	public boolean isUnderGreenhouse(Block b, boolean isTree){
		boolean isUnder = false;
		if (isTree){
			Location lamploc = b.getLocation();
			Location px = new Location(lamploc.getWorld(), lamploc.getX()+2, lamploc.getY()-1, lamploc.getZ());
			Location nx = new Location(lamploc.getWorld(), lamploc.getX()-2, lamploc.getY()-1, lamploc.getZ());
			Location pz = new Location(lamploc.getWorld(), lamploc.getX(), lamploc.getY()-1, lamploc.getZ()+2);
			Location nz = new Location(lamploc.getWorld(), lamploc.getX(), lamploc.getY()-1, lamploc.getZ()-2);
			if (px.getBlock().getType()==Material.REDSTONE_LAMP_ON || nx.getBlock().getType()==Material.REDSTONE_LAMP_ON || 
					pz.getBlock().getType()==Material.REDSTONE_LAMP_ON || nz.getBlock().getType()==Material.REDSTONE_LAMP_ON){
				isUnder=true;
			}
		} else {
			Location lamploc = b.getLocation(); lamploc.setY(lamploc.getY()+2);
			if (lamploc.getBlock().getType()==Material.REDSTONE_LAMP_ON){
				isUnder = true;
			} else {
				Location px = new Location(lamploc.getWorld(), lamploc.getX()+1, lamploc.getY(), lamploc.getZ());
				Location nx = new Location(lamploc.getWorld(), lamploc.getX()-1, lamploc.getY(), lamploc.getZ());
				Location pz = new Location(lamploc.getWorld(), lamploc.getX(), lamploc.getY(), lamploc.getZ()+1);
				Location nz = new Location(lamploc.getWorld(), lamploc.getX(), lamploc.getY(), lamploc.getZ()-1);
				if (px.getBlock().getType()==Material.REDSTONE_LAMP_ON || nx.getBlock().getType()==Material.REDSTONE_LAMP_ON || 
						pz.getBlock().getType()==Material.REDSTONE_LAMP_ON || nz.getBlock().getType()==Material.REDSTONE_LAMP_ON){
					isUnder = true;
				}
			}
		}
		double nc = Math.random();
		if (nc>0.5){
			return isUnder;
		} else {
			return false;
		}
	}
	
	@EventHandler
	public void onBonemeal(PlayerInteractEvent e){
		if (e.getItem().getType()==Material.INK_SACK && e.getAction()==Action.RIGHT_CLICK_BLOCK){
			e.setCancelled(true);
		}
	}
}
