package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class InvGate extends Gate implements Powering {

	public InvGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.INV_GATE, belongChunk, pos);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		if (surroundingTiles.get(orientation.getOpposite()) instanceof Powering) {
			Powering tile = (Powering) surroundingTiles.get(orientation.getOpposite());
			power = !tile.isPowered(orientation);
			surroundingTiles.get(orientation).mustUpdate();
		}
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	public boolean isPowered(EnumCardinalPoint e) {
		// A gate can only give power to his outputs
		return e == orientation ? power : false;
	}

}
