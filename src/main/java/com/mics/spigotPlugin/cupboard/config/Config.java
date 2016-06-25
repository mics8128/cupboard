package com.mics.spigotPlugin.cupboard.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import com.mics.spigotPlugin.cupboard.Cupboard;
import com.mics.spigotPlugin.cupboard.utils.Util;

public enum Config {

	DEBUG("debug", false, "is plugin show debug message?"),
	CUPBOARD_PROTECT_DIST("cupboard.protect_dist", 9, "this is cupboard protect area size (ex 9 is 9+9+1 -> 19*19*19)"),
	CUPBOARD_BETWEEN_DIST("cupboard.between_dist", 18, "this is how many block between cupboard can put another cupboard"),
	ANTI_TNT_EXPLOSION("cupboard.anti-tnt-explosion", false, "is cupboard protect explosion from TNT?"),
	ANTI_OTHERS_EXPLOSION("cupboard.anti-creeper-explosion", true, "is cupboard protect explosion from CREEPER?"),
	OP_BYPASS("cupboard.is-op-creative-bypass", true, "is OP user can bypass block protect when in creative mode?"),
	
	WP_NETHER_DOOR_PROTECT_ENABLE("world-protect.nether-portal-protect.enable", true, "enable nether door protect (if this is false, below is non-use)"),
	WP_NETHER_SREACH_RADIUS("world-protect.nether-portal-protect.sreach-redius", 16, "change nether portal sreach radius"),
	WP_NETHER_REMOVE_BLOCK("world-protect.nether-portal-protect.remove-block", true, "remove block when nether door is blocked"),
	WP_ANTI_NETHER_DOOR_BLOCK("world-protect.nether-portal-protect.anti-nether-door-block", true, "let nether door can't block"),
	WP_ANTI_NETHER_DOOR_ENTITY_TELEPORT("world-protect.nether-portal-protect.anti-nether-door-entity-teleport", true, "let nether door can't teleport ant entity"),
	
	WP_PIGZOMBIE_DROP_NETHER_WART("world-protect.pigzombie-drop-nether-wart.enable", true, "let pig zombie drop nether-wart in nether"),
	WP_PIGZOMBIE_DROP_NETHER_WART_PERCENT("world-protect.pigzombie-drop-nether-wart.percent", 0.3, "0~1, this mean when drop gold nugget, how many percent will turn it to nether wart."),
	
	PP_PLAYER_INVENTORY_RECOVERY_PERCENT("player-protect.player-inventory-recovery-percent", 0.5, "recovery player inventory if they death (0 to disable)"),
	
	WB_ENABLE("world-border.enable", true, "enable world border system? (use this system please DO NOT modify time)"),
	WB_INIT_RADIUS("world-border.init-radius", 1200, "it mean -x ~ x "),
	WB_MIN_RADIUS("world-border.min-radius", 500, "it mean min -x ~ x "),
	WB_DEDUCT_AMOUNT("world-border.deduct-amount", 1, "how many border to deduct every period"),
	WB_DEDUCT_TIME("world-border.deduct-time", 3600, "deduct period (sec)"),
	WB_NETHER_SCALE("world-border.nether-scale", 4, "nether border scale"),
	WB_ENDER_SCALE("world-border.ender-scale", 4, "ender border scale"),

	AIR_DROP_ENABLE("air-drop.enable", true, "enable airdrop?"),
	AIR_DROP_MIN_TIME("air-drop.min-time", 40, "airdrop min period time (min)"),
	AIR_DROP_MAX_TIME("air-drop.max-time", 80, "airdrop max period time (min)"),
	AIR_NOTIFY_TIME("air-drop.notify-time", 5, "notify all player before this time (have to small than min-time)"),
	AIR_MIN_PLAYER("air-drop.min-player", 5, "have to more than this amount player to airdrop"),
	AIR_DROP_OFFSET("air-drop.offset", 20, "let airdrop not drop at show up location"),
	AIR_DROP_ITEM_AMOUNT("air-drop.item-amount", 5, "how many items will droped?"),
	
	TNT_SP_ENABLE("tnt.enable", true, "let TNT can desotry obsidian, water, lava, TNT Can put in protect area and TNT hard to craft."),
	TNT_EXPLOSION_RADIUS("tnt.radius", 10, "TNT power radius"),
	TNT_BREAK_RADIUS("tnt.break-radius", 3, "destory obsidian/water/lava check radius"),
	TNT_OBSIDIAN_BREAK_PROBABILITY("tnt.obsidian.break-probability", 0.5, "turn obsidian block probability (after explosion)"),
	TNT_OBSIDIAN_BREAK_TO("tnt.obsidian.break-to", "COBBLESTONE", "turn obsidian to which block"),
	TNT_WATER_BREAK_PROBABILITY("tnt.water.break-probability", 1, "turn water block to air probability (before explosion)"),
	TNT_LAVA_BREAK_PROBABILITY("tnt.lava.break-probability", 1, "turn lava to air probability (before explosion)"),
	LOCALE("locale", "en-EN", "language file name");
	
	private final Object value;
	private final String path;
	private final String description;
	private static YamlConfiguration cfg;
	private static final File f = new File(Cupboard.getInstance().getDataFolder(), "config.yml");
	
	private Config(String path, Object val, String description) {
	    this.path = path;
	    this.value = val;
	    this.description = description;
	}
	
	public String getPath() {
	    return path;
	}
	
	public String getDescription() {
	    return description;
	}
	
	public Object getDefaultValue() {
	    return value;
	}

	public boolean getBoolean() {
	    return cfg.getBoolean(path);
	}
	
	public int getInt() {
	    return cfg.getInt(path);
	}
	
	public double getDouble() {
	    return cfg.getDouble(path);
	}
	
	public String getString() {
	    return Util.replaceColors(cfg.getString(path));
	}
	
	public List<String> getStringList() {
	    return cfg.getStringList(path);
	}
	
	public static void load() {
		boolean save_flag = false;
		
        Cupboard.getInstance().getDataFolder().mkdirs();
        String header = "";
		cfg = YamlConfiguration.loadConfiguration(f);

        for (Config c : values()) {
        	header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            if (!cfg.contains(c.getPath())) {
            	save_flag = true;
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
        
        if(save_flag){
        	save();
    		cfg = YamlConfiguration.loadConfiguration(f);
        }
	}
	
	public static void save(){
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void set(Object value, boolean save) {
	    cfg.set(path, value);
	    if (save) {
            save();
	    }
	}
}
