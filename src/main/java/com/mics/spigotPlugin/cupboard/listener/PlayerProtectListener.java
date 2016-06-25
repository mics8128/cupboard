package com.mics.spigotPlugin.cupboard.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.config.Config;
import com.mics.spigotPlugin.cupboard.config.Locales;

public class PlayerProtectListener extends MyListener {
	private Map<String, List<ItemStack>> saveinv;
	public PlayerProtectListener(Cupboard instance)
	{
		super(instance);
		saveinv = new HashMap<String, List<ItemStack>>();
	}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
    	Player p = event.getEntity();
    	List<ItemStack> keepInv = new ArrayList<ItemStack>();
    	if(Config.PP_PLAYER_INVENTORY_RECOVERY_PERCENT.getDouble() != 0){
	    	for(ItemStack i: Arrays.asList(event.getEntity().getInventory().getContents())){
	    		if(i == null) continue;
	    		if(new Random().nextDouble() < Config.PP_PLAYER_INVENTORY_RECOVERY_PERCENT.getDouble() ){
	    			keepInv.add(i);
	    			event.getDrops().remove(i);
	    		}
	    	}
	    	saveinv.put(p.getUniqueId().toString(), keepInv);
    	}
    }
    
    @EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Location l = event.getRespawnLocation();
		Player p = event.getPlayer();
		
		//return your inventory
		if(saveinv.containsKey(p.getUniqueId().toString())){
			for(ItemStack i: saveinv.get(p.getUniqueId().toString())){
				event.getPlayer().getInventory().addItem(i);
			}
			saveinv.remove(p.getUniqueId().toString());
		}
		
		//Spawn location check
		if(event.isBedSpawn()){
			if(this.plugin.cupboards.checkIsLimit(l, p)){
				p.setBedSpawnLocation(null);
				p.sendMessage(Locales.SPAWN_WITHOUT_ACCESS.getString());
			}
		}
		WorldBorder border = l.getWorld().getWorldBorder();
		int max_x = border.getCenter().add(border.getSize()/2, 0, 0).getBlockX();
		int min_x = border.getCenter().add(-border.getSize()/2, 0, 0).getBlockX();
		int max_z = border.getCenter().add(0, 0, border.getSize()/2).getBlockZ();
		int min_z = border.getCenter().add(0, 0, -border.getSize()/2).getBlockZ();
		if(
				l.getBlockX() > max_x ||
				l.getBlockX() < min_x ||
				l.getBlockZ() > max_z ||
				l.getBlockZ() < min_z
			){
			p.setBedSpawnLocation(null);
			p.sendMessage(Locales.SPAWN_OUTSIDE_BORDER.getString());
		}
	}

}
