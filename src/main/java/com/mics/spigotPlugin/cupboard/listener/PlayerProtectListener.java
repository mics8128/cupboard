package com.mics.spigotPlugin.cupboard.listener;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.config.Locales;


public class PlayerProtectListener extends MyListener {
	public PlayerProtectListener(Cupboard instance)
	{
		super(instance);
	}
	
    @EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Location l = event.getRespawnLocation();
		Player p = event.getPlayer();
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
