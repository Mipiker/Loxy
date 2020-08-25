package fr.mipiker.game.utils;

import org.joml.Vector2i;

import fr.mipiker.game.Chunk;

public class UtilsCoords {

	/**
	 * Return a chunk pos from a world pos
	 * 
	 * @param worldPos
	 * @return
	 */
	public static Vector2i getChunkPos(Vector2i worldPos) {
		int x = 0;
		int y = 0;
		if (worldPos.x >= 0)
			x = (int) (worldPos.x / (float) Chunk.SIZE);
		else
			x = -((int) (-(worldPos.x - Chunk.SIZE + 1) / (float) Chunk.SIZE));
		if (worldPos.y >= 0)
			y = (int) (worldPos.y / (float) Chunk.SIZE);
		else
			y = -((int) (-(worldPos.y - Chunk.SIZE + 1) / (float) Chunk.SIZE));
		return new Vector2i(x, y);
	}

	public static Vector2i getTilePosInChunkPos(Vector2i worldPos) {
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
		return new Vector2i(x, y);
	}

}
