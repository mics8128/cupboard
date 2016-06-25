package com.mics.spigotPlugin.cupboard.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import com.mics.spigotPlugin.cupboard.Cupboard;


public class FallingPackageEntity extends PackageEntity {
	
	World world;
	Location startLoc;
	Material material;
	FallingBlock blocky = null;
	
	public FallingPackageEntity(Location loc, Material m, int offset){
		super();
		summon(applyOffset(loc, offset), m);
	}
	
	public FallingPackageEntity(Location loc, Material m){
		super();
		summon(loc, m);
	}
	
	@SuppressWarnings("deprecation")
	protected void summon(Location loc, Material m) {
		
		startLoc = loc;
		startLoc.getChunk().load();
		
		world = startLoc.getWorld();
		material = m;
		
		blocky = world.spawnFallingBlock(startLoc, material, (byte) 0);
		blocky.setDropItem(false);
		summonSpawnFireworks();
		
		tick();
	}

	@SuppressWarnings("deprecation")
	protected void tick(){
		if(world.getBlockAt(blocky.getLocation().clone().add(0, -1, 0)).getType() == Material.AIR){
			counter++;
			world.spawnParticle(Particle.SMOKE_NORMAL, blocky.getLocation(), 50, 0.1, 0.1, 0.1, 0.1);
			
			if (blocky.isDead()){
				Location oldLoc = blocky.getLocation();
				Vector oldVelocity = blocky.getVelocity();
				
				blocky = world.spawnFallingBlock(oldLoc, material, (byte) 0);
				blocky.setVelocity(oldVelocity);
			}
			
			summonUpdateFireworks();
			
			
			retick();
		} else {
			turnLandedPackage();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		blocky.remove();
	}
	
	public void turnLandedPackage() {
		blocky.remove();
		new LandedPackageEntity(blocky.getLocation(), blocky.getMaterial());
	}
	
	private void summonUpdateFireworks(){
		if ( true ){ //fireworks_on_fall
			final Firework fw = (Firework) world.spawnEntity(blocky.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
			
            fwm.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.RED).withColor(Color.WHITE).build());
            fw.setFireworkMeta(fwm);
            
            Cupboard.getInstance().getServer().getScheduler().runTaskLater( Cupboard.getInstance(), new Runnable() {

				public void run() {
					fw.detonate();
				}
            	
            }, 1L);
            
		}
	}
	
	private void summonSpawnFireworks(){
		if ( true ){ //firework
            final Firework fw = (Firework) world.spawnEntity(blocky.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
			
            fwm.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
            fw.setFireworkMeta(fwm);
            
            Cupboard.getInstance().getServer().getScheduler().runTaskLater(Cupboard.getInstance(), new Runnable() {

				public void run() {
					fw.detonate();
				}
            	
            }, 1L);
            
		}
	}
	
	private Location applyOffset(Location loc, int bounds){
		
		if ( bounds < 1 ){
			Cupboard.getInstance().logDebug("WTF");
			return loc;
		}
		
		int xOff;
		int zOff;
		int rand = new Random().nextInt(bounds * 2);
		int rand2 = new Random().nextInt(bounds * 2);
		
		zOff = rand - bounds;
		xOff = rand2 - bounds;

		loc.add(xOff, 0, zOff);
		return loc;
	}

	public static List<FallingPackageEntity> getFallingPackageEntities(){
		List<FallingPackageEntity> entities = new ArrayList<FallingPackageEntity>();
		if(allPackageEntities != null){
			for(PackageEntity e : PackageEntity.allPackageEntities){
				if(e instanceof FallingPackageEntity){
					entities.add((FallingPackageEntity) e);
				}
			}
		}
		return entities;
	}
	
	public Location getLocation(){
		return startLoc.clone();
	}
	

}
