package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class XorGate extends Gate implements Powering{

	public XorGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.XOR_GATE, belongChunk, pos);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	public boolean isPowered(EnumCardinalPoint e) {
		return false;
	}

}
