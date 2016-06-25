package com.mics.spigotPlugin.cupboard.listener;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.config.Config;
import com.mics.spigotPlugin.cupboard.config.Drops;
import com.mics.spigotPlugin.cupboard.entity.FallingPackageEntity;

public class AirdropInteractListener extends MyListener {
	public AirdropInteractListener(Cupboard instance) {
		super(instance);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)  || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			
			if (event.getClickedBlock().hasMetadata("isPackage") && event.getClickedBlock().getMetadata("isPackage").get(0).asBoolean() == true){
				
				Block clickedBlock = event.getClickedBlock();
				
				clickedBlock.setType(Material.AIR);
				clickedBlock.removeMetadata("isPackage", plugin);
				
				summonBreakFirework(clickedBlock.getLocation());
				
				event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_IRONGOLEM_STEP, 1, 1);
				
				//ITEM HADLING SECTION
				for (ItemStack item : getDrops()){
					clickedBlock.getWorld().dropItem(clickedBlock.getLocation(), item);
				}
			}
		}
	}
	
	private List<ItemStack> getDrops(){
		return Drops.getDrops(Config.AIR_DROP_ITEM_AMOUNT.getInt());
	}
	
	private void summonBreakFirework(Location loc){
		//Firework stuff 
		final Firework fw = (Firework) loc.getWorld().spawnEntity(loc.clone().add(0,1,0), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder().with(Type.BURST).withColor(Color.YELLOW).withColor(Color.ORANGE).withFlicker().build());
		fw.setFireworkMeta(fwm);
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				fw.detonate();
			}
		}, 2);
		
	}
	
	@EventHandler
	private void onChunkUnload(ChunkUnloadEvent e){
		for(FallingPackageEntity entity : FallingPackageEntity.getFallingPackageEntities()){
			if(e.getChunk().equals(entity.getLocation().getChunk()))
				e.setCancelled(true);
		}
	}
	
	//let dropping airdrop chunk loaded
	
}
