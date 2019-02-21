package cn.foxii.frozen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Frozen extends JavaPlugin {
	private List<Chunk> disableAIChunks;
	@SuppressWarnings("unused")
	private TPSMonitor tpsMonitor;
	@SuppressWarnings("unused")
	private FrozenListener entityListener;

	private static Object MINECRAFTSERVER;
	private static Field RECENTTPS;
	private static Method GETHANDLE;
	private static Field FROMMOBSPAWNER;

	public Configuration config;

	@Override
	public void onEnable() {
		tryVersionCompatible();

		disableAIChunks = new ArrayList<>(20);

		saveDefaultConfig();
		config = Configuration.getInstance(getConfig());

		if (config.ifTPSMonitorEnable)
			tpsMonitor = new TPSMonitor(this);
		if (config.ifListenerEnable)
			entityListener = new FrozenListener(this);
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll(this);
	}

	@Deprecated
	public void debug() {
		for (Chunk chunk : disableAIChunks) {
			Bukkit.broadcastMessage(String.format("世界(%s)区块(%d,%d)中心位置(%d,%d)", chunk.getWorld().getName(),
					chunk.getX(), chunk.getZ(), chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8));
		}

	}

	public void freezeChunkAI(Chunk chunk) {
		if (!disableAIChunks.contains(chunk))
			disableAIChunks.add(chunk);
		setChunkAI(chunk, true);
	}

	public void unfreezeChunkAI(Chunk chunk) {
		if (disableAIChunks.contains(chunk)) {
			setChunkAI(chunk, false);
			disableAIChunks.remove(chunk);
		}
	}

	public void unfreezeAllChunkAI() {
		for (Chunk chunk : disableAIChunks)
			setChunkAI(chunk, false);
		disableAIChunks.clear();
	}

	public void unregisterChunk(Chunk chunk) {
		disableAIChunks.remove(chunk);
	}

	public void dealNewSpawned(Chunk chunk, Entity entity) {
		if (disableAIChunks.contains(chunk) && !(entity instanceof ArmorStand) && !(entity instanceof Player))
			setFromMobSpawner(entity, true);
	}

	public static void tryVersionCompatible() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		String versionString = name.substring(name.lastIndexOf('.') + 1);
		String obcPrefix = String.format("org.bukkit.craftbukkit.%s.", versionString);
		String nmsPrefix = String.format("net.minecraft.server.%s.", versionString);
		try {
			MINECRAFTSERVER = Class.forName(obcPrefix + "CraftServer").getMethod("getServer")
					.invoke(Bukkit.getServer());
			RECENTTPS = MINECRAFTSERVER.getClass().getField("recentTps");

			GETHANDLE = Class.forName(obcPrefix + "entity.CraftEntity").getMethod("getHandle");
			FROMMOBSPAWNER = Class.forName(nmsPrefix + "Entity").getField("fromMobSpawner");
		} catch (Exception e) {
			System.out.println("An error occurred while attempting to be compatible with the server, please report this to me with you server version");
			e.printStackTrace();
		}
	}

	public static int getLivingEntityCount(Chunk chunk) {
		int entityCount = 0;
		for (Entity entity : chunk.getEntities())
			if (entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !(entity instanceof Player))
				entityCount++;
		return entityCount;
	}

	public static void setFromMobSpawner(Entity entity, boolean isfromMobSpawner) {
		try {
			Object minecraftEntity = GETHANDLE.invoke(entity);
			FROMMOBSPAWNER.set(minecraftEntity, isfromMobSpawner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double[] getTPS() {
		try {
			return (double[]) (RECENTTPS.get(MINECRAFTSERVER));
		} catch (Exception e) {
			e.printStackTrace();
			return new double[] { 20, 20, 20 };
		}
	}

	public static boolean ifTPSAbove(double arg) {
		double[] tps = getTPS();
		return tps[0] > arg && tps[1] > arg && tps[2] > arg;
	}

	public static boolean ifTPSBelow(double arg) {
		double[] tps = getTPS();
		return tps[0] < arg || tps[1] < arg || tps[2] < arg;
	}

	public static void setChunkAI(Chunk chunk, boolean isfromMobSpawner) {
		for (Entity entity : chunk.getEntities()) {
			if (entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !(entity instanceof Player))
				setFromMobSpawner(entity, isfromMobSpawner);
		}
	}
}
