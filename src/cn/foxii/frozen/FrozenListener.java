package cn.foxii.frozen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class FrozenListener implements Listener {
	private final Frozen plugin;
	public FrozenListener(Frozen pl) {
		pl.getServer().getPluginManager().registerEvents(this, pl);
		plugin=pl;
		pl.getLogger().info("Listener registered");
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if(!plugin.config.ifOnMobSpawnEnable)
			return;
		if(plugin.config.ifOnMobSpawnCompatible)
		plugin.dealNewSpawned(event.getLocation().getChunk(), event.getEntity());
		if((plugin.config.ifMobSpawnEntityThresholdEnable&&Frozen.getLivingEntityCount(event.getLocation().getChunk())>plugin.config.mobSpawnEntityThreshold)||
				(plugin.config.ifMobSpawnFreezeAI&&Frozen.ifTPSBelow(plugin.config.mobSpawnFreezeTPS)))
			Frozen.setFromMobSpawner(event.getEntity(), true);
	}
	@EventHandler(ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
		if(!plugin.config.ifOnChunkLoadEnable)
			return;
		if ((plugin.config.ifOnChunkLoadCompatible&&Frozen.ifTPSBelow(plugin.config.freezeTPS)&&Frozen.getLivingEntityCount(event.getChunk()) > plugin.config.monitorEntityThreshold)||
				(plugin.config.ifChunkLoadEntityThresholdEnable&&Frozen.getLivingEntityCount(event.getChunk())>plugin.config.chunkLoadEntityThreshold)||
				(plugin.config.ifChunkLoadFreezeAI&&Frozen.ifTPSBelow(plugin.config.chunkLoadFreezeTPS))) 
			plugin.freezeChunkAI(event.getChunk());
	}
	@EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
		plugin.unregisterChunk(event.getChunk());
	}
}
