package fr.mipiker.game.tiles.gate;

import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public abstract class Gate extends Tile implements Powering {

	protected boolean power;

	protected Gate(EnumTiles TYLE_TYPE, Chunk belongChunk, PositionTile pos) {
		super(TYLE_TYPE, belongChunk, pos);
		property.add(EnumProperty.ONLY_TICK_UPDATE);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	public static Gate newGate(EnumTiles type, Chunk belongChunk, PositionTile pos) {
		switch (type) {
		case AND_GATE:
			return new AndGate(belongChunk, pos);
		case INV_GATE:
			return new InvGate(belongChunk, pos);
		case OR_GATE:
			return new OrGate(belongChunk, pos);
		case XOR_GATE:
			return new XorGate(belongChunk, pos);
		default:
			break;
		}
		return null;
	}

	@Override
	public Gate copy() {
		Gate g = newGate(TYPE, belongChunk, pos);
		g.power = power;
		return g;
	}

	@Override
	public boolean isPowered() {
		return power;
	}
	/**
	 * A gate can only set power from itsef with it's inputs
	 */
	@Override
	public void setPower(boolean power) {
		mustUpdate();
	}

	/**
	 * Clock wise rotation
	 */
	public void rotate() {
		setOrientation(orientation.getClockwise());
		mustUpdateWithSurrounding();
	}
}
