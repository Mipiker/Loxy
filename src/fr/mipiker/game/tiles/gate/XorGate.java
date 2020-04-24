package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class XorGate extends Tile implements Powering{

	public XorGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.XOR_GATE, belongChunk, pos);
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
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

}
