package fr.mipiker.game.tiles;

import java.util.HashMap;
import fr.mipiker.game.Chunk;

public class Switch extends Tile implements Powering {

	private boolean power = false;

	public Switch(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.POWER, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		// Set the texture
		if ((power ? 1 : 0) != getActualTexture())
			setTexture(power ? 1 : 0); // call a render update too
	}

	public void setPower(boolean power) {
		this.power = power;
		mustUpdateWithSurrounding();
	}
	public boolean isPowered() {
		return power;
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}
}
