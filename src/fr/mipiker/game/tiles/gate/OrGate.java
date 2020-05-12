package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class OrGate extends Gate {

	public OrGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.OR_GATE, belongChunk, pos);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		System.out.println((surroundingTiles.get(orientation) instanceof Powering) + " " + (surroundingTiles.get(orientation.getClockwise()) instanceof Powering) + " " + (surroundingTiles.get(orientation.getAntiClockwise()) instanceof Powering));
		if (surroundingTiles.get(orientation) instanceof Powering && surroundingTiles.get(orientation.getClockwise()) instanceof Powering && surroundingTiles.get(orientation.getAntiClockwise()) instanceof Powering) {
			System.out.println("u");
			Powering in1 = (Powering) (surroundingTiles.get(orientation.getClockwise()));
			Powering in2 = (Powering) (surroundingTiles.get(orientation.getAntiClockwise()));
			if ((in1.isPowered(orientation.getAntiClockwise()) || in2.isPowered(orientation.getClockwise())) && !power) {
				power = true;
				System.out.println("a");
				surroundingTiles.get(orientation).mustUpdate();
			} else if (!in1.isPowered(orientation.getAntiClockwise()) && !in2.isPowered(orientation.getClockwise()) && power) {
				power = false;
				System.out.println("b");
				surroundingTiles.get(orientation).mustUpdate();
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
