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
		System.out.println("CACA");
		if (orientation == NORTH) {
			if (surroundingTiles.get(NORTH) instanceof Powering && surroundingTiles.get(EST) instanceof Powering && surroundingTiles.get(WEST) instanceof Powering) {
				Powering in1 = (Powering) (surroundingTiles.get(EST));
				Powering in2 = (Powering) (surroundingTiles.get(WEST));
				Powering out = (Powering) (surroundingTiles.get(NORTH));
				if (in1.isPowered() || in2.isPowered()) {
					out.addSourcePower(this);

				} else {
					out.removeSourcePower(this);
				}
			}
		}
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
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
	public void onTurningPowerOff() {
	}

	@Override
	public void onTurningPowerOn() {
	}
}
