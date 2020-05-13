package fr.mipiker.game.tiles;

import java.util.*;
import java.util.Map.Entry;
import fr.mipiker.game.Chunk;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

public class Wire extends Tile implements Powering {

	private boolean power = false;
	private HashMap<EnumCardinalPoint, Integer> powerConnectionType = new HashMap<>();
	private static final int UNKNOWN = 0, GET_POWER = 1, GIVE_POWER = 2;

	public Wire(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.WIRE, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		updatePower(surroundingTiles);
	}
	private void updatePower(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {

		// If power is off we set the power connection type of all the surrounding tiles
		if (!power) {
			powerConnectionType.clear();
			for (Entry<EnumCardinalPoint, Tile> e : surroundingTiles.entrySet()) {
				if (e.getValue() instanceof Powering) {
					Powering tile = (Powering) e.getValue();
					if (tile.isPowered(e.getKey().getOpposite()))
						powerConnectionType.put(e.getKey(), GET_POWER);
					else
						powerConnectionType.put(e.getKey(), GIVE_POWER);
				}
			}

			// If the power is off and if this wire will get power from an other tile
			if (powerConnectionType.containsValue(GET_POWER)) {
				// This wire is now powered
				setPower(true);
				// and all the surrounding tile that are not those who power this wire will be updated
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
					if (e.getValue() == GIVE_POWER) {
						surroundingTiles.get(e.getKey()).mustUpdate();
					}
				}
			}

		} else {
			// If a tile has been placed and is not powered: add it to power connection type and give it power
			for (Entry<EnumCardinalPoint, Tile> e : surroundingTiles.entrySet()) {
				if (e.getValue() instanceof Powering && !powerConnectionType.containsKey(e.getKey())) {
					Powering tile = (Powering) e.getValue();
					if (!tile.isPowered(e.getKey().getOpposite())) {
						powerConnectionType.put(e.getKey(), GIVE_POWER);
					}
				}
			}
			ArrayList<EnumCardinalPoint> removePowerConnectionType = new ArrayList<>();
			// If a tile that gave power to this wire is now off, it is set to unknown
			for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
				if (e.getValue() == GET_POWER) {
					if (surroundingTiles.get(e.getKey()) instanceof Powering) {
						Powering tile = (Powering) surroundingTiles.get(e.getKey());
						if (!tile.isPowered(e.getKey().getOpposite())) {
							powerConnectionType.put(e.getKey(), UNKNOWN);
						}
					} else
						removePowerConnectionType.add(e.getKey());
				}
			}

			// If a tile is replaced and can't be powered : remove the connection
			for (EnumCardinalPoint e : removePowerConnectionType)
				powerConnectionType.remove(e);

			// If there is no tile that power this wire
			if (!powerConnectionType.containsValue(GET_POWER)) {
				// All the tiles that get power from this wire will update and the power shut down
				setPower(false);
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
					if (e.getValue() == GIVE_POWER) {
						surroundingTiles.get(e.getKey()).mustUpdate();
					}
				}
			} else { // If there is at least 1 tile that give power
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
					// Thoses who gave power to this wire now get power from this wire
					if (e.getValue() == UNKNOWN) {
						powerConnectionType.put(e.getKey(), GIVE_POWER);
						surroundingTiles.get(e.getKey()).mustUpdate();
					}
				}
			}
		}

	}

	// ----------

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		updateTexture(surroundingTiles);
	}
	private void updateTexture(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		boolean north = surroundingTiles.get(NORTH).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean est = surroundingTiles.get(EAST).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean south = surroundingTiles.get(SOUTH).property.contains(EnumProperty.CONNECT_TO_WIRE);
		boolean west = surroundingTiles.get(WEST).property.contains(EnumProperty.CONNECT_TO_WIRE);

		if (!north && !est && !south && !west) { // 0
			setAndOrientTexture(0, NORTH);
		} else if (north && !est && !south && !west) { // 1 - haut
			setAndOrientTexture(2, NORTH);
		} else if (!north && est && !south && !west) { // 1 - est
			setAndOrientTexture(2, EAST);
		} else if (!north && !est && south && !west) { // 1 - south
			setAndOrientTexture(2, SOUTH);
		} else if (!north && !est && !south && west) { // 1 - west
			setAndOrientTexture(2, WEST);
		} else if (north && !est && south && !west) { // 2_0 - north/south
			setAndOrientTexture(4, NORTH);
		} else if (!north && est && !south && west) { // 2_0 - west/est
			setAndOrientTexture(4, EAST);
		} else if (north && est && !south && !west) { // 2_1 - north/est
			setAndOrientTexture(6, NORTH);
		} else if (!north && est && south && !west) { // 2_1 - est/south
			setAndOrientTexture(6, EAST);
		} else if (!north && !est && south && west) { // 2_1 - south/west
			setAndOrientTexture(6, SOUTH);
		} else if (north && !est && !south && west) { // 2_1 - south/west
			setAndOrientTexture(6, WEST);
		} else if (north && est && south && !west) { // 3 - north/est/south
			setAndOrientTexture(8, NORTH);
		} else if (!north && est && south && west) { // 3 - est/south/west
			setAndOrientTexture(8, EAST);
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
	public void setPower(boolean power) {
		this.power = power;
		mustRenderUpdate();
	}
	@Override
	public boolean isPowered() {
		return power;
	}
	@Override
	public boolean isPowered(EnumCardinalPoint e) {
		// A wire can only give power to tiles that are not giving power to this wire
		if (powerConnectionType.get(e) == null || powerConnectionType.get(e) != GET_POWER)
			return power;
		return false;
	}

	@Override
	public String toString() {
		String str = super.toString() + "\nPower : " + power;
		for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
			str += "\n" + e.getKey() + " : " + (e.getValue() == GIVE_POWER ? "give" : (e.getValue() == GET_POWER ? "get" : "unknown"));
		}
		return str;
	}
}
