package fr.mipiker.game;

import static fr.mipiker.game.Settings.RENDER_DISTANCE;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

import java.util.*;
import java.util.Map.Entry;

import org.joml.Vector2i;

import fr.mipiker.game.tiles.*;
import fr.mipiker.game.utils.UtilsCoords;
import fr.mipiker.isisEngine.*;

public class Map {

	private String name = "Loxy world";
	private HashMap<Vector2i, Chunk> chunks = new HashMap<>();
	private ArrayList<Chunk> chunkToUpdate = new ArrayList<>();

	public Map() {
	}
	public Map(String name) {
		this.name = name;
	}
	public Map(Map map) {
		name = map.name;
		chunks = new HashMap<>(map.chunks);
		chunkToUpdate = new ArrayList<>(map.chunkToUpdate);
	}

	public void update(Scene scene, Player player, boolean tickUpdate) {
		generate(new Vector2i((int) player.getCamera().getPosition().x, (int) player.getCamera().getPosition().z), scene);
		Chunk[] chunkToUpdate = this.chunkToUpdate.toArray(new Chunk[this.chunkToUpdate.size()]);
		this.chunkToUpdate.clear();
		for (Chunk chunk : chunkToUpdate)
			chunk.fixTilesToUpdate();
		for (Chunk chunk : chunkToUpdate)
			chunk.update(tickUpdate);
	}

	public void renderUpdate(Scene scene) {
		for (Chunk chunk : chunks.values())
			chunk.renderUpdate(scene);
	}

	public void generate(Vector2i pos, Scene scene) {
		// Unload chunk out from render distance
		Vector2i chunkPlayerPos = UtilsCoords.getChunkPosFromWorldPos(pos);
		for (Entry<Vector2i, Chunk> e : chunks.entrySet()) {
			Vector2i chunkPos = e.getKey();
			if (chunkPlayerPos.distance(chunkPos) > RENDER_DISTANCE) { // If the chunk is in render distance
				e.getValue().setCanRender(false);
				for (Mesh mesh : e.getValue().getMeshesToRender())
					scene.removeMesh(mesh);
			}
		}

		// Create new chunk and load them
		for (int y = chunkPlayerPos.y - RENDER_DISTANCE; y < chunkPlayerPos.y + RENDER_DISTANCE; y++) {
			for (int x = chunkPlayerPos.x - RENDER_DISTANCE; x < chunkPlayerPos.x + RENDER_DISTANCE; x++) {
				if (chunkPlayerPos.distance(x, y) < RENDER_DISTANCE) {
					if (chunks.get(new Vector2i(x, y)) == null)
						chunks.put(new Vector2i(x, y), new Chunk(new Vector2i(x, y), this));
					if (!chunks.get(new Vector2i(x, y)).getCanRender())
						chunks.get(new Vector2i(x, y)).setCanRender(true);
				}
			}
		}
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
		surroundingTiles.put(NORTH, getTile(pos.getWorldPos().add(0, -1, new Vector2i())));
		surroundingTiles.put(EAST, getTile(pos.getWorldPos().add(1, 0, new Vector2i())));
		surroundingTiles.put(SOUTH, getTile(pos.getWorldPos().add(0, 1, new Vector2i())));
		surroundingTiles.put(WEST, getTile(pos.getWorldPos().add(-1, 0, new Vector2i())));
		return surroundingTiles;
	}

	public void addChunkToUpdate(Chunk chunk) {
		if (!chunkToUpdate.contains(chunk))
			chunkToUpdate.add(chunk);
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
