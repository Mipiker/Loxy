package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

public class OrGate extends Gate {

	public OrGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.OR_GATE, belongChunk, pos);

	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		if (orientation == NORTH) {
			if (surroundingTiles.get(NORTH) instanceof Powering && surroundingTiles.get(EAST) instanceof Powering && surroundingTiles.get(WEST) instanceof Powering) {
				Powering in1 = (Powering) (surroundingTiles.get(EAST));
				Powering in2 = (Powering) (surroundingTiles.get(WEST));
				if ((in1.isPowered(EAST.getOpposite()) || in2.isPowered(WEST.getOpposite())) && !power) { // When the power is set to o,
					power = true;
					surroundingTiles.get(NORTH).mustUpdate();
				} else if (!in1.isPowered(EAST.getOpposite()) && !in2.isPowered(WEST.getOpposite()) && power) {
					power = false;
					surroundingTiles.get(NORTH).mustUpdate();
				}
			}
		}
	}

	@Override
	public boolean isPowered(EnumCardinalPoint e) {
		// A gate can only give power to his inputs
		return e == orientation ? power : false;

	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

}
