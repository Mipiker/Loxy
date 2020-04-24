package fr.mipiker.game.tiles;

import java.util.HashMap;
import java.util.Map.Entry;
import fr.mipiker.game.Chunk;

public class Switch extends Tile implements Powering {

	private boolean power = false;

	public Switch(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.POWER, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		System.out.println("update switch " + pos);
		if ((power ? 1 : 0) != getActualTexture())
			setTexture(power ? 1 : 0);
		for (Entry<EnumCardinalPoint, Tile> entry : surroundingTiles.entrySet()) {
			if (entry.getValue() instanceof Powering) {
				if (power) {
					((Powering) entry.getValue()).addSourcePower(this);
					// entry.getValue().shouldUpdate = true;
				} else {
					((Powering) entry.getValue()).removeSourcePower(this);
					// entry.getValue().shouldUpdate = true;
				}
			}
		}
	}

	public void setPower(boolean power) {
		this.power = power;
		mustUpdate();
	}
	public boolean isPowered() {
		return power;
	}

	@Override
	public void addSourcePower(Powering p) {
	}
	@Override
	public void removeSourcePower(Powering p) {
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
