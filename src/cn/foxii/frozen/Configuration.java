package cn.foxii.frozen;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
	private static Configuration INSTANCE;

	public boolean ifTPSMonitorEnable;

	public int checkTPSInterval;

	public int taskDelay;

	public int monitorEntityThreshold;

	public int freezeTPS;

	public int unfreezeAllTPS;

	public boolean ifListenerEnable;

	public boolean ifOnMobSpawnEnable;
	public boolean ifOnMobSpawnCompatible;

	public boolean ifMobSpawnEntityThresholdEnable;
	public int mobSpawnEntityThreshold;

	public boolean ifMobSpawnFreezeAI;
	public int mobSpawnFreezeTPS;

	public boolean ifOnChunkLoadEnable;
	public boolean ifOnChunkLoadCompatible;

	public boolean ifChunkLoadEntityThresholdEnable;
	public int chunkLoadEntityThreshold;

	public boolean ifChunkLoadFreezeAI;
	public int chunkLoadFreezeTPS;

	private Configuration(FileConfiguration fileConfig) {
		ifTPSMonitorEnable=fileConfig.getBoolean("TPSMonitor.enable");
		checkTPSInterval=fileConfig.getInt("TPSMonitor.check_interval_tick");
		taskDelay=fileConfig.getInt("TPSMonitor.task_delay_tick");
		monitorEntityThreshold=fileConfig.getInt("TPSMonitor.chunk_entity_threshold");
		freezeTPS=fileConfig.getInt("TPSMonitor.freeze_AI_when_TPS_below");
		unfreezeAllTPS=fileConfig.getInt("TPSMonitor.unfreeze_all_AI_when_TPS_above");
		
		ifListenerEnable=fileConfig.getBoolean("Listener.enable");
		
		ifOnMobSpawnEnable=fileConfig.getBoolean("Listener.on_mob_spawn.enable");
		ifOnMobSpawnCompatible=fileConfig.getBoolean("Listener.on_mob_spawn.compatible_with_TPSMonitor")&&ifTPSMonitorEnable;
		ifMobSpawnEntityThresholdEnable=fileConfig.getBoolean("Listener.on_mob_spawn.freeze_when_entity_above.enable");
		mobSpawnEntityThreshold=fileConfig.getInt("Listener.on_mob_spawn.freeze_when_entity_above.chunks_entity");
		ifMobSpawnFreezeAI=fileConfig.getBoolean("Listener.on_mob_spawn.freeze_when_TPS_below.enable");
		mobSpawnFreezeTPS=fileConfig.getInt("Listener.on_mob_spawn.freeze_when_TPS_below.TPS_below");
		
		ifOnChunkLoadEnable=fileConfig.getBoolean("Listener.on_chunk_load.enable");
		ifOnChunkLoadCompatible=fileConfig.getBoolean("Listener.on_chunk_load.compatible_with_TPSMonitor")&&ifTPSMonitorEnable;
		ifChunkLoadEntityThresholdEnable=fileConfig.getBoolean("Listener.on_chunk_load.freeze_when_entity_above.enable");
		chunkLoadEntityThreshold=fileConfig.getInt("Listener.on_chunk_load.freeze_when_entity_above.chunks_entity");
		ifChunkLoadFreezeAI=fileConfig.getBoolean("Listener.on_chunk_load.freeze_when_TPS_below.enable");
		chunkLoadFreezeTPS=fileConfig.getInt("Listener.on_chunk_load.freeze_when_TPS_below.TPS_below");
	}

	public static Configuration getInstance(FileConfiguration fileConfig) {
		if (INSTANCE == null)
			INSTANCE = new Configuration(fileConfig);
		return INSTANCE;
	}
}
