package fr.mipiker.game.tiles;

import java.util.*;
import java.util.Map.Entry;
import fr.mipiker.game.Chunk;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

public class Wire extends Tile implements Powering {

	private boolean power = false;
	private HashMap<Powering, Integer> connectionTypePower = new HashMap<>();
	private static final int NO_POWER = 0, GET_POWER = 1, GIVE_POWER = 2;

	public Wire(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.WIRE, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		updatePower(surroundingTiles);
		belongChunk.addTileToRenderUpdate(this);
	}
	private void updatePower(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		System.out.println("update wire " + pos);

		boolean turnOnPower = false;
		boolean turnOffPower = false;
		// Complete connectionTypePower
		for (Entry<EnumCardinalPoint, Tile> entry : surroundingTiles.entrySet()) {
			if (entry.getValue() instanceof Powering) {
				// No current coming
				if (!connectionTypePower.containsValue(GET_POWER)) {
					power = false;
					if (connectionTypePower.get((Powering) entry.getValue()) == null || connectionTypePower.get((Powering) entry.getValue()) != NO_POWER) {
						connectionTypePower.put((Powering) entry.getValue(), NO_POWER);
						turnOffPower = true;
					}
					// Current comming
				} else {
					power = true;
					if (connectionTypePower.get((Powering) entry.getValue()) == null || connectionTypePower.get((Powering) entry.getValue()) == NO_POWER) {
						connectionTypePower.put((Powering) entry.getValue(), GIVE_POWER);
						turnOnPower = true;
					}
				}
			}
		}

		// Set the power
		if (turnOnPower) {
			onTurningPowerOn();
			power = true;
		} else if (turnOffPower) {
			onTurningPowerOff();
			power = false;
		}
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		updateTexture(surroundingTiles);
	}
	private void updateTexture(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		boolean north = surroundingTiles.get(NORTH).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean est = surroundingTiles.get(EST).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean south = surroundingTiles.get(SOUTH).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean west = surroundingTiles.get(WEST).property.contains(EnumProperty.CONNECT_TO_WIRE);

		if (!north && !est && !south && !west) { // 0
			setAndOrientTexture(0, NORTH);
		} else if (north && !est && !south && !west) { // 1 - haut
			setAndOrientTexture(2, NORTH);
		} else if (!north && est && !south && !west) { // 1 - est
			setAndOrientTexture(2, EST);
		} else if (!north && !est && south && !west) { // 1 - south
			setAndOrientTexture(2, SOUTH);
		} else if (!north && !est && !south && west) { // 1 - west
			setAndOrientTexture(2, WEST);
		} else if (north && !est && south && !west) { // 2_0 - north/south
			setAndOrientTexture(4, NORTH);
		} else if (!north && est && !south && west) { // 2_0 - west/est
			setAndOrientTexture(4, EST);
		} else if (north && est && !south && !west) { // 2_1 - north/est
			setAndOrientTexture(6, NORTH);
		} else if (!north && est && south && !west) { // 2_1 - est/south
			setAndOrientTexture(6, EST);
		} else if (!north && !est && south && west) { // 2_1 - south/west
			setAndOrientTexture(6, SOUTH);
		} else if (north && !est && !south && west) { // 2_1 - south/west
			setAndOrientTexture(6, WEST);
		} else if (north && est && south && !west) { // 3 - north/est/south
			setAndOrientTexture(8, NORTH);
		} else if (!north && est && south && west) { // 3 - est/south/west
			setAndOrientTexture(8, EST);
		} else if (north && !est && south && west) { // 3 - south/west/north
			setAndOrientTexture(8, SOUTH);
		} else if (north && est && !south && west) { // 3 - west/north/est
			setAndOrientTexture(8, WEST);
		} else if (north && est && south && west) { // 4 - north/est/south/west
			setAndOrientTexture(10, NORTH);
		}

		setTexture(power ? getActualTexture() + 1 : getActualTexture());
	}

	@Override
	public void onTurningPowerOn() {
		// Set the power to surrounding tiles but not those who have give it to this tile
		for (Entry<Powering, Integer> entry : connectionTypePower.entrySet())
			if (entry.getValue() == GIVE_POWER)
				entry.getKey().addSourcePower(this);
	}
	@Override
	public void onTurningPowerOff() {
		for (Entry<Powering, Integer> entry : connectionTypePower.entrySet())
			entry.getKey().removeSourcePower(this);
	}

	@Override
	public void addSourcePower(Powering tile) {
		connectionTypePower.put(tile, GET_POWER);
		mustUpdate();
	}
	@Override
	public void removeSourcePower(Powering tile) {
		connectionTypePower.put(tile, NO_POWER);
		mustUpdate();
	}

	@Override
	public void setPower(boolean power) {
		this.power = power;
		// updateThenUpdateSurrounding(true);
		// belongChunk.setShouldResetMesh(true);
	}
	@Override
	public boolean isPowered() {
		return power;
	}
}
