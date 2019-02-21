package cn.foxii.frozen;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TPSMonitor extends BukkitRunnable {
	private final Frozen plugin;

	public TPSMonitor(Frozen pl) {
		plugin = pl;
		runTaskTimer(pl, plugin.config.taskDelay, plugin.config.checkTPSInterval);
		pl.getLogger().info(String.format("TPSMonitor starts with interval %d s, delay %d s", plugin.config.checkTPSInterval/20,plugin.config.taskDelay/20));
	}

	@Override
	public void run() {
		if (Frozen.ifTPSBelow(plugin.config.freezeTPS)) {
			for (World world : Bukkit.getWorlds())
				for (Chunk chunk : world.getLoadedChunks())
					if (Frozen.getLivingEntityCount(chunk) > plugin.config.monitorEntityThreshold)
						plugin.freezeChunkAI(chunk);
					else
						plugin.unfreezeChunkAI(chunk);
		} else if (Frozen.ifTPSAbove(plugin.config.unfreezeAllTPS))
			plugin.unfreezeAllChunkAI();
	}

}
