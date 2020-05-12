package fr.mipiker.game.tiles.gate;

import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;


public abstract class Gate extends Tile implements Powering{
	
	protected boolean power;

	protected Gate(EnumTiles TYLE_TYPE, Chunk belongChunk, PositionTile pos) {
		super(TYLE_TYPE, belongChunk, pos);
		property.add(EnumProperty.ONLY_TICK_UPDATE);
		property.add(EnumProperty.CONNECT_TO_WIRE);
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
}
