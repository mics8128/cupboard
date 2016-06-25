package com.mics.spigotPlugin.cupboard.entity;

import java.util.ArrayList;
import java.util.List;

import com.mics.spigotPlugin.cupboard.Cupboard;

public class PackageEntity {
	
	protected int counter = 0;
	static List<PackageEntity> allPackageEntities;
	
	protected PackageEntity(){
		if(allPackageEntities == null){
			allPackageEntities = new ArrayList<PackageEntity>();
		}
		allPackageEntities.add(this);
	}
	
	protected void summon(){
		
	}
	
	public void remove(){
		allPackageEntities.remove(this);
	}
	
	protected void tick(){
		
	}
	
	protected void retick(){
		if(counter > 1200) remove(); //如果超過五分鐘就刪除 (1200) TODO make this configable
		Cupboard.getInstance().getServer().getScheduler().runTaskLater(Cupboard.getInstance(), new Runnable() {
			
			public void run() {
				tick();
			}
			
		}, 5L);
	}
	

	public static void removeAll() {
		if(allPackageEntities == null) return;
		for(PackageEntity e : allPackageEntities){
			e.remove();
		}
	}
}
