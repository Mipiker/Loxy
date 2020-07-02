package fr.mipiker.game.tiles;

import org.joml.Vector2i;

import fr.mipiker.game.Chunk;
import fr.mipiker.game.utils.UtilsCoords;

public class PositionTile {

	private Vector2i worldPos, chunkPos;

	/**
	 * Seems to be bugged
	 */
	public PositionTile(Vector2i worldPos) {
		this.chunkPos = UtilsCoords.getChunkPosFromWorldPos(worldPos);
		this.worldPos = new Vector2i(worldPos);
	}
	public PositionTile(Vector2i positionOfChunk, Vector2i chunkPos) {
		this.chunkPos = new Vector2i(chunkPos);
		this.worldPos = new Vector2i(positionOfChunk).mul(Chunk.SIZE).add(chunkPos);
	}
	public PositionTile(PositionTile posTile) {
		this.worldPos = new Vector2i(posTile.worldPos);
		this.chunkPos = new Vector2i(posTile.chunkPos);
	}

	public Vector2i getWorldPos() {
		return worldPos;
	}
	public Vector2i getChunkPos() {
		return chunkPos;
	}
	public void setWorldPos(Vector2i worldPos) {
		this.chunkPos = UtilsCoords.getChunkPosFromWorldPos(new Vector2i(worldPos));
		this.worldPos = new Vector2i(worldPos);
	}

	@Override
	public String toString() {
		return "World pos : " + worldPos.x + ", " + worldPos.y + "\nChunk pos : " + chunkPos.x + ", " + chunkPos.y;
	}

}
