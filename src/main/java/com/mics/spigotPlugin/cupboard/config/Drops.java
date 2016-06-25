package com.mics.spigotPlugin.cupboard.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mics.spigotPlugin.cupboard.Cupboard;

public class Drops{
  private ArrayList<Drop> drops = null;
  private YamlConfiguration drops_cfg;
  private double chance_count;
  private static final File file = new File(Cupboard.getInstance().getDataFolder(), "drops.yml");
  static Drops instance;
  public Drops(){
      ConfigurationSerialization.registerClass(Drop.class);
      instance = this;
      load();
  }
  
  //取得掉落物品
	public static List<ItemStack> getDrops(int number)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();
		for(int i=0; i < number; i++){
			items.add(getRandomItemStack());
		}
	    return items;
	}
  
  	private static ItemStack getRandomItemStack(){
	    ArrayList<Drop> drops = instance.drops;
	    double random = (new Random().nextDouble()) * instance.chance_count;
		for(Drop drop : drops){
			random -= drop.drop_chance;
			if(random < 0){
				return drop.item;
			}
		}
		return null;
  	}
  
  private void save()
  {  
	  try {
		  drops_cfg.set("Drops", drops.toArray());
		  drops_cfg.save(file);
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
  }
  private void updateChanceCount(){
	  chance_count = 0.0;
	  for(Drop d : drops){
		  chance_count += d.drop_chance;
	  }
  }
  
  private void load()
  {
	  //TODO add check if not exist copy from resource
	  drops_cfg = YamlConfiguration.loadConfiguration(file);
	  this.drops = new ArrayList<Drop>();
	  chance_count = 0.0;
	  for(Object o : drops_cfg.getList("Drops")){
		  Drop d = (Drop) o;
		  chance_count += d.drop_chance;
		  drops.add((Drop) o);
	  }
  }
  
  //============== Below is for TESTING ===================
  	public void makeTestDrops(){
	  Drop diamondDrop = new Drop();
	  
	  ItemStack diamond = new ItemStack(Material.DIAMOND, 5);
	  ItemMeta diamondMeta = diamond.getItemMeta();
	
	  diamondMeta.setDisplayName("GOD DIAMOND");
	  diamondMeta.setLore(Arrays.asList(new String[] { "YoyoTV", "HAHAHA" }));
	  diamond.setItemMeta(diamondMeta);
	    
	  diamondDrop.item = diamond;
	  diamondDrop.drop_chance = 5;
	
	  Drop appleDrop = new Drop();
	  ItemStack apple = new ItemStack(Material.APPLE, 5);
	  appleDrop.item = apple;
	  appleDrop.drop_chance = 5;
	
	  if(drops_cfg == null)drops_cfg = new YamlConfiguration();
	  this.drops = new ArrayList<Drop>();
	  drops.add(diamondDrop);
	  drops.add(appleDrop);
  	}

  	public static void addDrops(ItemStack item, double chance) {
  		Drop newDrop = new Drop(item, chance);
  		instance.drops.add(newDrop);
  		instance.updateChanceCount();
  		instance.save();
  	}

	public static void reload() {
		instance.load();
	}
}