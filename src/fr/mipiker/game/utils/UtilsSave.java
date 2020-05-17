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
	public static boolean save(Map map, Player player) {
		new File("save/" + map.getName() + "/chunk").mkdirs();
		// Chunk
		for (Entry<Vector2i, Chunk> e : map.getChunks().entrySet()) {
			File file = new File("save/" + map.getName() + "/chunk/" + e.getKey().x + "_" + e.getKey().y + ".chk");
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
		// Setting
		try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("save/" + map.getName() + "/setting.stt")))) {
			// Player
			dos.writeFloat(player.getCamera().getPosition().x);
			dos.writeFloat(player.getCamera().getPosition().y);
			dos.writeFloat(player.getCamera().getPosition().z);
			// Chunk
			dos.writeInt(Chunk.SIZE);
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
		if (!new File("save/" + name + "/chunk").isDirectory())
			return null;
		// Setting
		try (DataInputStream dis = new DataInputStream(new FileInputStream(new File("save/" + name + "/setting.stt")))) {
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
			Chunk chunk = new Chunk(new Vector2i(chunkX, chunkY), map);
			try (DataInputStream dis = new DataInputStream(new FileInputStream(new File("save/" + name + "/chunk/" + fileName)))) {
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
				return null;
			}
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
		for (File file : new File("save/" + name).listFiles())
			if (file.isDirectory()) {
				for (File file_ : new File(file.getPath()).listFiles())
					file_.delete();
				file.delete();
			} else
				file.delete();
		return new File("save/" + name).delete();
	}

}
