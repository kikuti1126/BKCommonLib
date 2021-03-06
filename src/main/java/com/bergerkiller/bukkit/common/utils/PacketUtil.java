package com.bergerkiller.bukkit.common.utils;

import java.util.Collection;
import java.util.Map;

import net.minecraft.server.v1_4_R1.Packet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketFields;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.protocol.PacketListener;

public class PacketUtil {
	private static final Map<Class<?>, Integer> packetsToIds = PacketFields.DEFAULT.<Map<Class<?>, Integer>>getField("a").get(null);

	public static int getPacketId(Class<?> packetClass) {
		return packetsToIds.get(packetClass);
	}

	public static void registerPacketToId(Class<?> packetClass, int id) {
		packetsToIds.put(packetClass, id);
	}

	public static void sendPacket(Player player, Object packet) {
		sendPacket(player, packet, true);
	}

	public static void sendPacket(Player player, Object packet, boolean throughListeners) {
		CommonPlugin.getInstance().sendPacket(player, packet, throughListeners);
	}

	public static void sendCommonPacket(Player player, CommonPacket packet) {
		sendCommonPacket(player, packet, true);
	}

	public static void sendCommonPacket(Player player, CommonPacket packet, boolean throughListeners) {
		sendPacket(player, packet.getHandle(), throughListeners);
	}

	public static void broadcastChunkPacket(org.bukkit.Chunk chunk, Object packet, boolean throughListeners) {
		if (chunk == null || packet == null) {
			return;
		}

		for (Player player : WorldUtil.getPlayers(chunk.getWorld())) {
			if (EntityUtil.isNearChunk(player, chunk.getX(), chunk.getZ(), CommonUtil.VIEW)) {
				sendPacket(player, packet, throughListeners);
			}
		}
	}

	public static void broadcastBlockPacket(Block block, Object packet, boolean throughListeners) {
		broadcastBlockPacket(block.getWorld(), block.getX(), block.getZ(), packet, throughListeners);
	}

	public static void broadcastBlockPacket(org.bukkit.World world, final int x, final int z, Object packet, boolean throughListeners) {
		if (world == null || packet == null) {
			return;
		}
		for (Player player : WorldUtil.getPlayers(world)) {
			if (EntityUtil.isNearBlock(player, x, z, CommonUtil.BLOCKVIEW)) {
				sendPacket(player, packet, throughListeners);
			}
		}
	}

	public static void broadcastPacket(Object packet, boolean throughListeners) {
		for (Player player : CommonUtil.getOnlinePlayers()) {
			sendPacket(player, packet, throughListeners);
		}
	}

	public static void addPacketListener(Plugin plugin, PacketListener listener, PacketType... packets) {
		if (listener == null || LogicUtil.nullOrEmpty(packets)) {
			return;
		}
		int[] ids = new int[packets.length];
		for (int i = 0; i < packets.length; i++) {
			if (packets[i] == null) {
				throw new IllegalArgumentException("Can not register a null packet type");
			} else {
				ids[i] = packets[i].getId();
			}
		}
		CommonPlugin.getInstance().addPacketListener(plugin, listener, ids);
	}

	public static void removePacketListeners(Plugin plugin) {
		CommonPlugin.getInstance().removePacketListeners(plugin);
	}

	public static void removePacketListener(PacketListener listener) {
		CommonPlugin.getInstance().removePacketListener(listener, true);
	}

	public static void broadcastPacketNearby(Location location, double radius, Object packet) {
		broadcastPacketNearby(location.getWorld(), location.getX(), location.getY(), location.getZ(), radius, packet);
	}

	public static void broadcastPacketNearby(org.bukkit.World world, double x, double y, double z, double radius, Object packet) {
		CommonUtil.getCraftServer().getHandle().sendPacketNearby(x, y, z, radius, WorldUtil.getDimension(world), (Packet) packet);
	}

	/**
	 * Obtains a collection of all plugins currently listening for the Packet type specified.
	 * Packets of this type can be expected to be handled by these plugins when sending it.
	 * 
	 * @param packetId to check
	 * @return collection of listening plugins
	 */
	public static Collection<Plugin> getListenerPlugins(PacketType packetType) {
		return getListenerPlugins(packetType.getId());
	}

	/**
	 * Obtains a collection of all plugins currently listening for the Packet id specified.
	 * Packets of this type can be expected to be handled by these plugins when sending it.
	 * 
	 * @param packetId to check
	 * @return collection of listening plugins
	 */
	public static Collection<Plugin> getListenerPlugins(int packetId) {
		return CommonPlugin.getInstance().getListening(packetId);
	}
}
