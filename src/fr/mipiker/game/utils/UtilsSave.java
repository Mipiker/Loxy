package fr.mipiker.game.utils;

import java.io.*;
import java.util.Map.Entry;
import org.joml.*;
import fr.mipiker.game.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.tiles.gate.Gate;

public class UtilsSave {

	/**
	 * Save the map in the folder "save/name"
	 * 
	 * @param map
	 *            the map to be saved
	 * @param name
	 *            the name of the folder that save the map
	 */
	public static boolean save(Map map, Player player, String name) {
		new File("save/" + name).mkdirs();
		// Map
		for (Entry<Vector2i, Chunk> e : map.getChunks().entrySet()) {
			File file = new File("save/" + name + "/" + e.getKey().x + "_" + e.getKey().y + ".chk");
			try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
				for (int y = 0; y < Chunk.SIZE; y++) {
					for (int x = 0; x < Chunk.SIZE; x++) {
						Tile tile = e.getValue().getTile(new Vector2i(x, y));
						dos.writeByte(tile.TYPE.getValue()); // Type
						if (tile instanceof Gate)
							dos.writeByte(tile.getOrientation().getValue()); // Orientation
						if (tile instanceof Powering)
							dos.writeBoolean(((Powering) tile).isPowered()); // Power
						if (tile instanceof Wire)
							dos.writeBoolean(((Wire) tile).isBridge()); // Bridge wire
					}
				}

			} catch (IOException e1) {
				return false;
			}
		}
		// Player
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("save/" + name + "/player.ply")))) {
			dos.writeFloat(player.getCamera().getPosition().x);
			dos.writeFloat(player.getCamera().getPosition().y);
			dos.writeFloat(player.getCamera().getPosition().z);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Load a saved map. It erase the actual map
	 * 
	 * @param name
	 *            the name folder where the map is saved
	 * @return the saved map
	 */
	public static Map load(Player player, String name) {
		if (!new File("save/" + name).isDirectory())
			return null;
		// Map
		Map map = new Map();
		for (String fileName : new File("save/" + name).list()) {
			if (!"player.ply".equalsIgnoreCase(fileName)) {
				String[] pos = fileName.substring(0, fileName.length() - 4).split("_");
				int chunkX = Integer.parseInt(pos[0]);
				int chunkY = Integer.parseInt(pos[1]);
				Chunk chunk = new Chunk(new Vector2i(chunkX, chunkY), map);
				try (DataInputStream dis = new DataInputStream(new FileInputStream(new File("save/" + name + "/" + fileName)))) {
					for (int y = 0; y < Chunk.SIZE; y++) {
						for (int x = 0; x < Chunk.SIZE; x++) {
							Tile tile = Tile.newTile(EnumTiles.getTile(dis.readByte()), chunk, new PositionTile(chunk.getPos(), new Vector2i(x, y))); // Type
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
				}
			}
		}
		// Player
		try (DataInputStream dis = new DataInputStream(new FileInputStream(new File("save/" + name + "/player.ply")))) {
			player.getCamera().setPosition(new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Delete the saved map
	 * 
	 * @param name
	 *            the name of folder where is saved the map
	 */
	public static boolean delete(String name) {
		File dir = new File("save/" + name);
		if (!dir.isDirectory())
			return false;
		for (String file : dir.list())
			new File("save/" + name + "/" + file).delete();
		return dir.delete();
	}

}
