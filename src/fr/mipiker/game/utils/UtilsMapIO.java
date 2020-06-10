package fr.mipiker.game.utils;

import static org.apache.commons.io.FileUtils.*;

import java.io.*;
import java.util.Map.Entry;

import org.joml.*;

import fr.mipiker.game.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.tiles.gate.Gate;

public class UtilsMapIO {

	/**
	 * Save the map in the folder "save/name"
	 * 
	 * @param map
	 *            the map to be saved
	 * @param name
	 *            the name of the folder that save the map
	 */
	public static boolean save(Map map, Player player) {
		new File("save/" + map.getName() + "/chunk").mkdirs();
		// Chunk
		for (Entry<Vector2i, Chunk> e : map.getChunks().entrySet())
			if (!saveChunk(map.getName(), e.getValue()))
				return false;
		// Setting
		try (DataOutputStream dos = new DataOutputStream(
				new FileOutputStream(new File("save/" + map.getName() + "/map.settings")))) {
			// Player
			dos.writeFloat(player.getCamera().getPosition().x);
			dos.writeFloat(player.getCamera().getPosition().y);
			dos.writeFloat(player.getCamera().getPosition().z);
			// Chunk
			dos.writeInt(Chunk.SIZE);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("[Info] Map " + map.getName() + " saved");
		return true;
	}

	public static boolean isChunkSaved(String mapName, Vector2i chunkPos) {
		return new File("save/" + mapName + "/chunk/" + chunkPos.x + "_" + chunkPos.y + ".chk").exists();
	}

	public static boolean saveChunk(String mapName, Chunk chunk) {
		File file = new File("save/" + mapName + "/chunk/" + chunk.getPos().x + "_" + chunk.getPos().y + ".chk");
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					Tile tile = chunk.getTile(new Vector2i(x, y));
					dos.writeByte(tile.TYPE.getValue()); // Type
					if (tile instanceof Gate)
						dos.writeByte(tile.getOrientation().getValue()); // Orientation
					if (tile instanceof Powering)
						dos.writeBoolean(((Powering) tile).isPowered()); // Power
					if (tile instanceof Wire)
						dos.writeBoolean(((Wire) tile).isBridge()); // Bridge wire
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Load a saved map
	 * 
	 * @param name
	 *            the name folder where the map is saved
	 * @return the saved map
	 */
	public static Map load(Player player, String name) {
		if (!new File("save/" + name + "/chunk").isDirectory())
			return null;
		// Setting
		try (DataInputStream dis = new DataInputStream(
				new FileInputStream(new File("save/" + name + "/map.settings")))) {
			player.getCamera().setPosition(new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat())); // Player
			Chunk.SIZE = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// Chunk
		Map map = new Map(name);
		for (String fileName : new File("save/" + name + "/chunk").list()) {
			String[] pos = fileName.substring(0, fileName.length() - 4).split("_");
			int chunkX = Integer.parseInt(pos[0]);
			int chunkY = Integer.parseInt(pos[1]);
			if (!loadChunk(map, new Vector2i(chunkX, chunkY)))
				return null;
		}
		System.out.println("[Info] Map " + name + " loaded");
		return map;
	}

	/**
	 * Load a specific chunk to the given map
	 * 
	 * @param chunkPos
	 *            the position of the chunk to be loaded to the map
	 * @return if the chunk has been loaded correctly
	 */
	public static boolean loadChunk(Map map, Vector2i chunkPos) {
		if (!isChunkSaved(map.getName(), chunkPos))
			return false;
		Chunk chunk = new Chunk(chunkPos, map);
		try (DataInputStream dis = new DataInputStream(new FileInputStream(
				new File("save/" + map.getName() + "/chunk/" + chunkPos.x + "_" + chunkPos.y + ".chk")))) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int x = 0; x < Chunk.SIZE; x++) {
					Tile tile = Tile.newTile(EnumTiles.getTile(dis.readByte()), chunk,
							new PositionTile(chunk.getPos(), new Vector2i(x, y))); // Type
					chunk.setTile(tile);
					if (tile instanceof Gate)
						tile.setOrientation(EnumCardinalPoint.getOrientation(dis.readByte())); // Orientation
					if (tile instanceof Powering)
						((Powering) tile).setPower(dis.readBoolean()); // Power
					if (tile instanceof Wire)
						((Wire) tile).setBridge(dis.readBoolean()); // Wire bridge
				}
			}
			map.getChunks().put(chunk.getPos(), chunk);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Delete the saved map
	 * 
	 * @param name
	 *            the name of folder where is saved the map
	 */
	public static boolean delete(String name) {
		try {
			deleteDirectory(new File("save/" + name));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("[Info] Map " + name + " deleted");
		return true;
	}

	public static boolean copy(String mapName, String copyMapName) {
		File from = new File("save/" + mapName);
		File to = new File("save/" + copyMapName);
		try {
			copyDirectory(from, to);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("[Info] Map " + mapName + " copied");
		return true;
	}
}
