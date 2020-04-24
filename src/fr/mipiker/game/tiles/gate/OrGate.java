package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class OrGate extends Tile implements Powering{

	public OrGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.OR_GATE, belongChunk, pos);
	}

	@Override

	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	public void setPower(boolean power) {
	}

	@Override
	public boolean isPowered() {
		return false;
	}

	@Override
	public void addSourcePower(Powering tile) {
	}

	@Override
	public void removeSourcePower(Powering tile) {
	}

	@Override
	public void onTurningPowerOn() {
	}

	@Override
	public void onTurningPowerOff() {
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

}
