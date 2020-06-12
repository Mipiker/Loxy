
package fr.mipiker.game;

import static fr.mipiker.game.Settings.RENDER_DISTANCE;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

import java.util.*;
import java.util.Map.Entry;

import org.joml.Vector2i;

import fr.mipiker.game.tiles.*;
import fr.mipiker.game.utils.*;
import fr.mipiker.isisEngine.*;

public class Map {

	private String name;
	private HashMap<Vector2i, Chunk> chunks = new HashMap<>();
	private ArrayList<Chunk> chunkToUpdate = new ArrayList<>();
	private ArrayList<Vector2i> chunkToSave = new ArrayList<>();
	private Timer autoSaveTimer;
	private boolean unloadChunks;

	public Map() {
		this("Loxy World");
	}
	public Map(String name) {
		this.name = name;
		resetTimer();
	}
	public Map(Map map) {
		this(map.name);
		chunks = new HashMap<>(map.chunks);
		chunkToUpdate = new ArrayList<>(map.chunkToUpdate);
	}

	public void update(Scene scene, Player player, boolean tickUpdate) {
		updateStateChunks(UtilsCoords.getChunkPosFromWorldPos(
				new Vector2i((int) player.getCamera().getPosition().x, (int) player.getCamera().getPosition().z)),
				scene);
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

	private void updateStateChunks(Vector2i chunkPlayerPos, Scene scene) {
		// Out of render distance
		for (Entry<Vector2i, Chunk> e : chunks.entrySet()) { // facile
			Vector2i distance = e.getKey().sub(chunkPlayerPos, new Vector2i());
			// Out of render distance + 0
			if (distance.x > RENDER_DISTANCE || distance.x < -RENDER_DISTANCE || distance.y > RENDER_DISTANCE
					|| distance.y < -RENDER_DISTANCE) {
				e.getValue().setCanRender(false); // Don't update it's render
				for (Mesh mesh : e.getValue().getMeshesToRender()) // Unshow it
					scene.removeMesh(mesh);
				// Out of render distance + 1
				if (distance.x > RENDER_DISTANCE + 1 || distance.x < -RENDER_DISTANCE - 1
						|| distance.y > RENDER_DISTANCE + 1 || distance.y < -RENDER_DISTANCE - 1) {
					if (!chunkToSave.contains(e.getKey())) // Save the chunk and free memory
						chunkToSave.add(e.getKey());
				} else
					chunkToSave.remove(e.getKey()); // Keep the chunk loaded
			} else
				chunkToSave.remove(e.getKey()); // Keep the chunk loaded
		}

		// In render distance
		for (int y = chunkPlayerPos.y - RENDER_DISTANCE; y < chunkPlayerPos.y + RENDER_DISTANCE + 1; y++) {
			for (int x = chunkPlayerPos.x - RENDER_DISTANCE; x < chunkPlayerPos.x + RENDER_DISTANCE + 1; x++) {
				Vector2i chunkPos = new Vector2i(x, y);
				if (chunks.get(chunkPos) == null) { // If the chunk isn't loaded
					if (!UtilsMapIO.loadChunk(this, chunkPos)) // If the chunk isn't saved
						chunks.put(chunkPos, new Chunk(chunkPos, this)); // Create a new one
				}
				if (!chunks.get(chunkPos).getCanRender()) // Allow it to update it's render
					chunks.get(chunkPos).setCanRender(true);
				chunkToSave.remove(chunkPos); // Keep the chunk loaded
			}
		}
		// Unload chunks too out from render distance + 1
		if (unloadChunks) {
			for (Vector2i chunkPos : chunkToSave)
				chunks.remove(chunkPos);
			unloadChunks = false;
			chunkToSave.clear();
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

	public void delete() {
		autoSaveTimer.cancel();
	}

	public void resetTimer() {
		if (autoSaveTimer != null)
			autoSaveTimer.cancel();
		autoSaveTimer = new Timer();
		autoSaveTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				for (Vector2i pos : chunkToSave)
					UtilsMapIO.saveChunk(name, chunks.get(pos));
				unloadChunks = true;
			}
		}, Settings.AUTO_SAVE_TIME * 1000, Settings.AUTO_SAVE_TIME * 1000);

	}
}
