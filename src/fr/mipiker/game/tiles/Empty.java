package fr.mipiker.game.tiles;

import java.util.HashMap;

import fr.mipiker.game.Chunk;

public class Empty extends Tile {

	public Empty(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.EMPTY, belongChunk, pos);
	}
	
	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

}
