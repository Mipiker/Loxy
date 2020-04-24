package fr.mipiker.game.tiles.gate;

import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;


public abstract class Gate extends Tile {

	protected Gate(EnumTiles TYLE_TYPE, Chunk belongChunk, PositionTile pos) {
		super(TYLE_TYPE, belongChunk, pos);
		property.add(EnumProperty.ONLY_TICK_UPDATE);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	

}
