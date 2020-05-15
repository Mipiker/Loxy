package fr.mipiker.game.tiles;

import java.util.HashMap;
import java.util.Map.Entry;
import fr.mipiker.game.Chunk;

public class Switch extends Tile implements Powering {

	private boolean power = false;

	public Switch(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.SWITCH, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		mustRenderUpdate();
		for (Entry<EnumCardinalPoint, Tile> e : surroundingTiles.entrySet())
			e.getValue().mustUpdate();
	}

	public void setPower(boolean power) {
		this.power = power;
		mustUpdate();
	}
	public boolean isPowered() {
		return power;
	}
	public boolean isPowered(EnumCardinalPoint e) {
		return power; // Switch can give his power in all directions
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		if ((power ? 1 : 0) != getActualTexture())
			setTexture(power ? 1 : 0); // call a render update too
	}

	@Override
	public String toString() {
		return super.toString() + "\nPower : " + power;
	}
}
