package fr.mipiker.game.tiles;

import java.util.*;
import java.util.Map.Entry;
import fr.mipiker.game.Chunk;
import static fr.mipiker.game.tiles.EnumCardinalPoint.*;

// Need some refactoring
public class Wire extends Tile implements Powering {

	private boolean power = false;
	private boolean bridge = false;
	private boolean bridgeVerticalPower = false;
	private boolean bridgeHorizontalPower = false;
	private HashMap<EnumCardinalPoint, Integer> powerConnectionType = new HashMap<>();
	private static final int UNKNOWN = 0, GET_POWER = 1, GIVE_POWER = 2;

	public Wire(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.WIRE, belongChunk, pos);
		property.add(EnumProperty.CONNECT_TO_WIRE);
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		int nb = 0;
		for (Entry<EnumCardinalPoint, Tile> e : surroundingTiles.entrySet())
			if (e.getValue() instanceof Powering)
				nb++;
		if (bridge && nb != 4) {
			power = false;
			bridge = false;
		}
		if (bridge && nb == 4)
			updatePowerBridge(surroundingTiles);
		else
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
				setPower(true); // This wire is now powered
				// and all the surrounding tile that are not those who power this wire will be updated
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet())
					if (e.getValue() == GIVE_POWER)
						surroundingTiles.get(e.getKey()).mustUpdate();
			}

		} else {

			whenPoweredUpdateNewSurroundingTile(surroundingTiles);

			// If there is no tile that power this wire
			if (!powerConnectionType.containsValue(GET_POWER)) {
				// All the tiles that get power from this wire will update and the power shut down
				setPower(false);
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet())
					if (e.getValue() == GIVE_POWER)
						surroundingTiles.get(e.getKey()).mustUpdate();

			} else { // If there is at least 1 tile that give power
				for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
					// Thoses who gave power to this wire now get power from this wire
					if (e.getValue() == UNKNOWN || e.getValue() == GIVE_POWER) {
						powerConnectionType.put(e.getKey(), GIVE_POWER);
						surroundingTiles.get(e.getKey()).mustUpdate();
					}
				}
			}
		}

	}
	private void updatePowerBridge(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		// It's the same as updatePower
		// Update when not powered
		if (!bridgeHorizontalPower) {
			powerConnectionType.remove(EAST);
			powerConnectionType.remove(WEST);
		}
		if (!bridgeVerticalPower) {
			powerConnectionType.remove(NORTH);
			powerConnectionType.remove(SOUTH);
		}
		for (Entry<EnumCardinalPoint, Tile> e : surroundingTiles.entrySet()) {
			if (e.getValue() instanceof Powering) {
				Powering tile = (Powering) e.getValue();
				if (!bridgeHorizontalPower && (e.getKey() == EAST || e.getKey() == WEST)) {
					if (tile.isPowered(e.getKey().getOpposite()))
						powerConnectionType.put(e.getKey(), GET_POWER);
					else
						powerConnectionType.put(e.getKey(), GIVE_POWER);
				}
				if (!bridgeVerticalPower && (e.getKey() == NORTH || e.getKey() == SOUTH)) {
					if (tile.isPowered(e.getKey().getOpposite()))
						powerConnectionType.put(e.getKey(), GET_POWER);
					else
						powerConnectionType.put(e.getKey(), GIVE_POWER);
				}

			}
		}
		// Powering up
		if (!bridgeHorizontalPower && (powerConnectionType.get(WEST) == GET_POWER || powerConnectionType.get(EAST) == GET_POWER)) {
			setBridgeHorizontalPower(true);
			if (powerConnectionType.get(WEST) == GIVE_POWER)
				surroundingTiles.get(WEST).mustUpdate();
			if (powerConnectionType.get(EAST) == GIVE_POWER)
				surroundingTiles.get(EAST).mustUpdate();
		}
		if (!bridgeVerticalPower && (powerConnectionType.get(NORTH) == GET_POWER || powerConnectionType.get(SOUTH) == GET_POWER)) {
			setBridgeVerticalPower(true);
			if (powerConnectionType.get(NORTH) == GIVE_POWER)
				surroundingTiles.get(NORTH).mustUpdate();
			if (powerConnectionType.get(SOUTH) == GIVE_POWER)
				surroundingTiles.get(SOUTH).mustUpdate();
		}
		whenPoweredUpdateNewSurroundingTile(surroundingTiles);
		if (powerConnectionType.get(EAST) != GET_POWER && powerConnectionType.get(WEST) != GET_POWER) {
			setBridgeHorizontalPower(false);
			if (powerConnectionType.get(EAST) == GIVE_POWER)
				surroundingTiles.get(EAST).mustUpdate();
			if (powerConnectionType.get(WEST) == GIVE_POWER)
				surroundingTiles.get(WEST).mustUpdate();
		} else {
			if (powerConnectionType.get(EAST) == UNKNOWN) {
				surroundingTiles.get(EAST).mustUpdate();
				powerConnectionType.put(EAST, GIVE_POWER);
			}
			if (powerConnectionType.get(WEST) == UNKNOWN) {
				surroundingTiles.get(WEST).mustUpdate();
				powerConnectionType.put(WEST, GIVE_POWER);
			}
		}
		if (powerConnectionType.get(SOUTH) != GET_POWER && powerConnectionType.get(NORTH) != GET_POWER) {
			setBridgeVerticalPower(false);
			if (powerConnectionType.get(SOUTH) == GIVE_POWER)
				surroundingTiles.get(SOUTH).mustUpdate();
			if (powerConnectionType.get(NORTH) == GIVE_POWER)
				surroundingTiles.get(NORTH).mustUpdate();
		} else {
			if (powerConnectionType.get(SOUTH) == UNKNOWN) {
				surroundingTiles.get(SOUTH).mustUpdate();
				powerConnectionType.put(SOUTH, GIVE_POWER);
			}
			if (powerConnectionType.get(NORTH) == UNKNOWN) {
				surroundingTiles.get(NORTH).mustUpdate();
				powerConnectionType.put(NORTH, GIVE_POWER);
			}
		}
	}
	private void whenPoweredUpdateNewSurroundingTile(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
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
		// If a tile that gave power to this wire now don't, it is set to unknown
		for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet()) {
			if (e.getValue() == GET_POWER) {
				if (surroundingTiles.get(e.getKey()) instanceof Powering) {
					Powering tile = (Powering) surroundingTiles.get(e.getKey());
					if (!tile.isPowered(e.getKey().getOpposite()))
						powerConnectionType.put(e.getKey(), UNKNOWN);
				} else // If it has been replaced by a tile that don't hundle power
					removePowerConnectionType.add(e.getKey());
			}
		}
		// If a tile is replaced and can't be powered : remove the connection
		for (EnumCardinalPoint e : removePowerConnectionType)
			powerConnectionType.remove(e);
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
			if (!bridge)
				setAndOrientTexture(10, NORTH);
			else if (!bridgeVerticalPower && !bridgeHorizontalPower)
				setAndOrientTexture(12, NORTH);
			else if (bridgeVerticalPower && bridgeHorizontalPower)
				setAndOrientTexture(13, NORTH);
			else if (!bridgeVerticalPower && bridgeHorizontalPower)
				setAndOrientTexture(14, NORTH);
			else if (bridgeVerticalPower && !bridgeHorizontalPower)
				setAndOrientTexture(15, NORTH);
		}

		if (!bridge)
			setTexture(power ? getActualTexture() + 1 : getActualTexture());
	}

	// ----------

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
		if (powerConnectionType.get(e) == null || powerConnectionType.get(e) != GET_POWER) {
			if (!bridge)
				return power;
			else if (e == EAST || e == WEST)
				return bridgeHorizontalPower;
			else if (e == SOUTH || e == NORTH)
				return bridgeVerticalPower;
		}
		return false;
	}

	// ----------

	public void setBridge(boolean bridge) {
		if (powerConnectionType.size() == 4) {
			this.bridge = bridge;
			mustUpdate();
			mustRenderUpdate();
		}
	}
	public boolean isBridge() {
		return bridge;
	}
	private void setBridgeHorizontalPower(boolean bridgeHorizontalPower) {
		this.bridgeHorizontalPower = bridgeHorizontalPower;
		mustRenderUpdate();
	}
	private void setBridgeVerticalPower(boolean bridgeVerticalPower) {
		this.bridgeVerticalPower = bridgeVerticalPower;
		mustRenderUpdate();
	}

	// ----------

	@Override
	public String toString() {
		String str = super.toString();
		if (bridge)
			str += "\nVertical power: " + bridgeVerticalPower + "\nHorizontal power : " + bridgeHorizontalPower;
		else
			str += "\nPower : " + power;
		for (Entry<EnumCardinalPoint, Integer> e : powerConnectionType.entrySet())
			str += "\n" + e.getKey() + " : " + (e.getValue() == GIVE_POWER ? "give" : (e.getValue() == GET_POWER ? "get" : "unknown"));
		return str;
	}
}
