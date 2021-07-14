package me.kate.clans;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Factions;
import com.sk89q.worldguard.protection.managers.RegionManager;

import me.kate.clans.calc.PointCalculation;
import me.kate.clans.calc.PointCallback;
import me.kate.clans.commands.DebugCommand;
import me.kate.clans.commands.RaidCommand;
import me.kate.clans.commands.TopCommand;
import me.kate.clans.config.MessageConfig;
import me.kate.clans.config.Messages;
import me.kate.clans.config.PluginConfig;
import me.kate.clans.database.DatabaseManager;
import me.kate.clans.hooks.ClansHook;
import me.kate.clans.hooks.WorldEditHook;
import me.kate.clans.hooks.WorldGuardHook;
import me.kate.clans.listeners.BlockBreakListener;
import me.kate.clans.listeners.EntityDeathListener;
import me.kate.clans.listeners.InventoryClickListener;
import me.kate.clans.listeners.MonsterSpawnerListener;
import me.kate.clans.listeners.PlayerInteractListener;
import me.kate.clans.listeners.PlayerJoinListener;
import me.kate.clans.listeners.PlayerQuitListener;
import me.kate.clans.listeners.SignChangeListener;
import me.kate.clans.listeners.SilkSpawnersSpawnerBreakListener;
import me.kate.clans.listeners.factions.FactionCreateListener;
import me.kate.clans.listeners.factions.FactionDisbandListener;
import me.kate.clans.objects.ClansTop;
import me.kate.clans.objects.TopClan;
import me.kate.clans.raids.RaidManager;
import me.kate.clans.raids.WrappedFactionManager;
import me.kate.clans.timer.ShieldTimer;
import me.kate.clans.utils.Util;
import me.kate.clans.worlds.MultiverseWorldBuilder;
import me.kate.clans.worlds.MultiverseWorldBuilder.Status;
import me.kate.clans.worlds.generator.ClansChunkGenerator;

public class ClansPlugin extends JavaPlugin
{
	private PluginConfig config;
	private MessageConfig mconfig;
	private DatabaseManager data;
	private WorldTracker tracker;
	private boolean setupComplete;
	private RaidManager manager;
	private WrappedFactionManager playerManager;
	private MultiverseWorldBuilder worldBuilder;
	private ClansHook clHook;
	private WorldEditHook weHook;
	private WorldGuardHook wgHook;
	private World world;
	private ClansTop top;
	private ShieldTimer timer;
	private Logger logger;
	private RegionManager rmanager;
	
	/**
	 * Players can still break chests with signs on them
	 * World generation is broken
	 * Sometimes factions chunks are null
	 * Point system incomplete
	 * Shield system incomplete
	 */
	
	@Override
	public void onEnable()
	{
		this.config = new PluginConfig(this);
		this.mconfig = new MessageConfig(this);
		this.data = new DatabaseManager(this);
		this.tracker = new WorldTracker(this);
		this.clHook = new ClansHook(this);
		this.timer = new ShieldTimer(this);
		this.weHook = new WorldEditHook(this);
		this.wgHook = new WorldGuardHook(this);
		this.manager = new RaidManager();
		this.top = new ClansTop();
		this.playerManager = new WrappedFactionManager();
		this.setupComplete = false;
		this.logger = getLogger();
		
		clHook.getHook();
		weHook.getHook();
		wgHook.getHook();
		
		data.setupDatabase();
		config.create();
		mconfig.create();
		
		new Messages(this).load();
		
		logger.info("Finshed loading plugin configuration!");
		logger.info("Creating clans world...");
		
		this.worldBuilder = new MultiverseWorldBuilder(this, config.getWorldName());
		
		if (worldBuilder.createWorld().equals(Status.FAILURE))
		{
			logger.severe("Failed to create clans world!");
		}
		
		this.world = worldBuilder.getWorld();
		
		this.rmanager = wgHook.getRegionManager(world);
		
		this.getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
		this.getServer().getPluginManager().registerEvents(new MonsterSpawnerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new SilkSpawnersSpawnerBreakListener(this), this);
		this.getServer().getPluginManager().registerEvents(new FactionCreateListener(this), this);
		this.getServer().getPluginManager().registerEvents(new FactionDisbandListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		this.getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
		
		this.getCommand("raid").setExecutor(new RaidCommand(this));
		this.getCommand("clanstop").setExecutor(new TopCommand(this));
		this.getCommand("debug").setExecutor(new DebugCommand(this));
		
		if (config.getSchematic() == null)
		{	
			logger.info(" ");
			logger.info(" ");
			logger.severe("WARNING: A BASE SCHEMATIC HAS NOT BEEN SET IN THE PLUGIN CONFIG!");
			logger.info(" ");
			logger.info(" ");
		}
		
		timer.startShieldCheckTask();
		
		Factions.getInstance().getAllFactions().forEach(faction ->
		{
			Util.chunkFix(faction);
		});
		
		final PointCalculation calc = new PointCalculation(this);
		
		calc.start(new PointCallback() 
		{
			@Override
			public void onComplete(List<TopClan> clans) 
			{
				Bukkit.broadcastMessage("Finished updating clanstop!");
			}
		});
	}
	
	@Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        return new ClansChunkGenerator(id);
    }
	
	public PluginConfig getPluginConfig()
	{
		return this.config;
	}
	
	public MessageConfig getMessageConfig()
	{
		return this.mconfig;
	}
	
	public DatabaseManager getDatabase()
	{
		return this.data;
	}
	
	public WorldTracker getWorldTracker()
	{
		return this.tracker;
	}
	
	public boolean isSetupComplete()
	{
		return this.setupComplete;
	}
	
	public ClansHook getClansHook()
	{
		return this.clHook;
	}
	
	public WorldEditHook getWEHook()
	{
		return this.weHook;
	}
	
	public WorldGuardHook getWGHook()
	{
		return this.wgHook;
	}
	
	public RegionManager getRegionManager()
	{
		return this.rmanager;
	}
	
	public World getClansWorld()
	{
		return this.world;
	}
	
	public RaidManager getRaidManager()
	{
		return this.manager;
	}
	
	public WrappedFactionManager getFactionManager()
	{
		return this.playerManager;
	}
	
	public ClansTop getClansTop()
	{
		return this.top;
	}
	
	public ShieldTimer getTimer()
	{
		return this.timer;
	}
}
