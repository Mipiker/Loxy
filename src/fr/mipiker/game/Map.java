package fr.mipiker.game;

import java.util.*;
import org.joml.Vector2i;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.utils.UtilsCoords;
import fr.mipiker.isisEngine.*;

public class Map {

	public static final int RENDER_DISTANCE = 5;

	private HashMap<Vector2i, Chunk> chunks;
	private ArrayList<Chunk> chunkToUpdate = new ArrayList<>();

	public Map() {
		chunks = new HashMap<>();
	}

	public void update(Scene scene, Player player, boolean tickUpdate) {
		generate(new Vector2i((int) player.getCamera().getPosition().x, (int) player.getCamera().getPosition().z), scene);

		Chunk[] chunkToUpdate = this.chunkToUpdate.toArray(new Chunk[this.chunkToUpdate.size()]);
		this.chunkToUpdate.clear();
		for (Chunk chunk : chunkToUpdate) {
			chunk.update(scene, tickUpdate);
		}
	}

	public void renderUpdate(Scene scene) {
		for (Chunk chunk : chunks.values())
			chunk.renderUpdate(scene);
	}

	public void generate(Vector2i pos, Scene scene) {
		Vector2i chunkPlayer = UtilsCoords.getChunkPosFromWorldPos(pos);
		for (int y = chunkPlayer.y - RENDER_DISTANCE; y < chunkPlayer.y + RENDER_DISTANCE; y++) {
			for (int x = chunkPlayer.x - RENDER_DISTANCE; x < chunkPlayer.x + RENDER_DISTANCE; x++) {
				if (chunks.get(new Vector2i(x, y)) == null) {
					chunks.put(new Vector2i(x, y), new Chunk(new Vector2i(x, y), this));
					for (Mesh mesh : chunks.get(new Vector2i(x, y)).getMeshesToRender())
						scene.addMesh(mesh);
				}
			}
		}
	}

	public Mesh[] getMeshesToRender() {
		int size = 0;
		for (Chunk chunk : chunks.values())
			size += chunk.getMeshesToRender().length;
		Mesh[] meshesToRender = new Mesh[size];
		int index = 0;
		for (Chunk chunk : chunks.values()) {
			for (int i = 0; i < chunk.getMeshesToRender().length; i++) {
				meshesToRender[index] = chunk.getMeshesToRender()[i];
				index++;
			}
		}
		return meshesToRender;
	}

	public Chunk getChunk(Vector2i posChunk) {
		return chunks.get(posChunk);
	}
	public Chunk getChunkFromWorldPos(Vector2i worldPos) {
		return chunks.get(UtilsCoords.getChunkPosFromWorldPos(worldPos));
	}
	public HashMap<Vector2i, Chunk> getChunks() {
		return chunks;
	}

	public void setTile(Tile tile) {
		tile.getBelongChunk().setTile(tile);
	}
	public Tile getTile(Vector2i worldPos) {
		int x = 0;
		int y = 0;
		if (worldPos.x >= 0)
			x = worldPos.x % Chunk.SIZE;
		else
			x = (Chunk.SIZE + (worldPos.x + 1) % Chunk.SIZE) - 1;
		if (worldPos.y >= 0)
			y = worldPos.y % Chunk.SIZE;
		else
			y = (Chunk.SIZE + (worldPos.y + 1) % Chunk.SIZE) - 1;
		Chunk chunk = getChunkFromWorldPos(worldPos);
		if (chunk != null)
			return chunk.getTile(new Vector2i(x, y));
		return null;
	}

	public HashMap<EnumCardinalPoint, Tile> getSurroundingTiles(PositionTile pos) {
		HashMap<EnumCardinalPoint, Tile> surroundingTiles = new HashMap<>();
		surroundingTiles.put(EnumCardinalPoint.NORTH, getTile(pos.getWorldPos().add(0, -1, new Vector2i())));
		surroundingTiles.put(EnumCardinalPoint.EST, getTile(pos.getWorldPos().add(1, 0, new Vector2i())));
		surroundingTiles.put(EnumCardinalPoint.SOUTH, getTile(pos.getWorldPos().add(0, 1, new Vector2i())));
		surroundingTiles.put(EnumCardinalPoint.WEST, getTile(pos.getWorldPos().add(-1, 0, new Vector2i())));
		return surroundingTiles;
	}

	public void addChunkToUpdate(Chunk chunk) {
		if (!chunkToUpdate.contains(chunk))
			chunkToUpdate.add(chunk);
	}
}
