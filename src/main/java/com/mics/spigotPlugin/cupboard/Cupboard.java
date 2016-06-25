package com.mics.spigotPlugin.cupboard;

import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mics.spigotPlugin.cupboard.command.ReloadCommand;
import com.mics.spigotPlugin.cupboard.config.Config;
import com.mics.spigotPlugin.cupboard.config.Drops;
import com.mics.spigotPlugin.cupboard.config.Locales;
import com.mics.spigotPlugin.cupboard.data.CupboardsData;
import com.mics.spigotPlugin.cupboard.entity.PackageEntity;
import com.mics.spigotPlugin.cupboard.command.AirdropCommand;
import com.mics.spigotPlugin.cupboard.command.KillCommand;
import com.mics.spigotPlugin.cupboard.listener.CupboardEntityProtectListener;
import com.mics.spigotPlugin.cupboard.listener.CupboardExplosionProtectListener;
import com.mics.spigotPlugin.cupboard.listener.AirdropInteractListener;
import com.mics.spigotPlugin.cupboard.listener.CupboardBlockProtectListener;
import com.mics.spigotPlugin.cupboard.listener.CupboardUseProtectListener;
import com.mics.spigotPlugin.cupboard.listener.GoldBlockListener;
import com.mics.spigotPlugin.cupboard.listener.MyListener;
import com.mics.spigotPlugin.cupboard.listener.RespawnListener;
import com.mics.spigotPlugin.cupboard.listener.SuicideListener;
import com.mics.spigotPlugin.cupboard.listener.TNTCraftListener;
import com.mics.spigotPlugin.cupboard.listener.TNTExplosionListener;
import com.mics.spigotPlugin.cupboard.listener.WorldProtectListener;
import com.mics.spigotPlugin.cupboard.schedule.AirDrop;
import com.mics.spigotPlugin.cupboard.schedule.WorldBorder;


public class Cupboard extends JavaPlugin implements Listener {
	public CupboardsData cupboards;
    public Drops drops;
    private static Cupboard INSTANCE;
    private ArrayList<Object> registedObject;
	
	@Override
	public void onEnable() {
		
		INSTANCE = this;
		registedObject = new ArrayList<Object>();
        //load config
        Config.load();
        this.logDebug("Loaded Config!");
        Locales.load();
        this.logDebug("Loaded Locales!");
        
        //load Airdrops
        drops = new Drops();
        
        //load cupboards
        cupboards = new CupboardsData(getDataFolder(),this);
        this.logDebug("Loaded Cupboards data!");

        this.getCommand("kill").setExecutor(new KillCommand(this));
        this.getCommand("cupboardreload").setExecutor(new ReloadCommand(this));
        this.getCommand("airdrop").setExecutor(new AirdropCommand(this));
        
        registerObject();
    }
	private void registerObject(){
		//register listener
        registedObject.add(new CupboardEntityProtectListener(this));
        registedObject.add(new CupboardExplosionProtectListener(this));
        registedObject.add(new CupboardBlockProtectListener(this));
        registedObject.add(new CupboardUseProtectListener(this));
        registedObject.add(new GoldBlockListener(this));
        registedObject.add(new WorldProtectListener(this));
        registedObject.add(new RespawnListener(this));
        registedObject.add(new SuicideListener(this));
        registedObject.add(new AirdropInteractListener(this));
        
        //rewrite TNT Receipts Listener
        if(Config.TNT_SP_ENABLE.getBoolean()){
            registedObject.add(new TNTExplosionListener(this));
        	registedObject.add(new TNTCraftListener(this));
        }

        if(Config.WB_ENABLE.getBoolean()){
        	registedObject.add(new WorldBorder(this));
        }
        
        if(Config.AIR_DROP_ENABLE.getBoolean()){
        	registedObject.add(new AirDrop(this));
        }
	}
	
	public void reload(){
		this.logDebug("");
		this.logDebug("============================================");
		this.logDebug("Unregister Object");
		this.logDebug("============================================");
		for(Object l : registedObject){
			if(l instanceof MyListener){
				((MyListener)l).unregisterListener();
			} else if(l instanceof WorldBorder){
				((WorldBorder)l).removeRunnable();
			} else {
				this.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				this.log("[ERROR] Object " + l.getClass().getName() + " Can't unreigster");
				this.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}

		this.logDebug("");
		this.logDebug("============================================");
		this.logDebug("Unregister ALL Listener and Schedule tasks");
		this.logDebug("============================================");
		//force unregister again
        this.logDebug("Unregister Listener!");
		HandlerList.unregisterAll();
        this.logDebug("Unregister Schedule tasks!");
		this.getServer().getScheduler().cancelAllTasks();
		
		//new registerdListeners
		registedObject = new ArrayList<Object>();
        //load config
		this.logDebug("");
		this.logDebug("============================================");
		this.logDebug("Reloading Config / Locales");
		this.logDebug("============================================");
        Config.load();
        this.logDebug("Loaded Config!");
        Locales.load();
        this.logDebug("Loaded Locales!");

		this.logDebug("");
		this.logDebug("============================================");
		this.logDebug("Register Listener");
		this.logDebug("============================================");
		registerObject();
		this.logDebug("");
	}
    @Override
    public void onDisable() {
    	PackageEntity.removeAll(); //remove all airdrop
    }
    
    public boolean isOP(Player p){
    	if(!Config.OP_BYPASS.getBoolean())return false;
    	if(p.isOp() && p.getGameMode() == GameMode.CREATIVE){
    		p.sendMessage(Locales.OP_BYPASS.getString());
    		return true;
    	}
		return false;
    }

	public void logDebug(String str, Object... args)
	{
		if(Config.DEBUG.getBoolean()) {
		String message = String.format(str, args);
			getLogger().info("(DEBUG) " + message);
		}
	}
	public void log(String str, Object... args)
	{
		String message = String.format(str, args);
		getLogger().info(message);
	}

	public static Cupboard getInstance() {
		return INSTANCE;
	}
    
    
}
