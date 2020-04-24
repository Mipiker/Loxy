package fr.mipiker.game.tiles.gate;

import java.util.HashMap;
import fr.mipiker.game.Chunk;
import fr.mipiker.game.tiles.*;

public class AndGate extends Gate implements Powering {

	public AndGate(Chunk belongChunk, PositionTile pos) {
		super(EnumTiles.AND_GATE, belongChunk, pos);
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
		mustUpdate();
	}

	@Override
	public void removeSourcePower(Powering tile) {
		mustUpdate();
	}

	@Override
	public void onTurningPowerOn() {
	}

	@Override
	public void onTurningPowerOff() {
	}

	@Override
	protected void update(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		updateTexture(surroundingTiles);
		System.out.println("update AND");
	}

	private void updateTexture(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
		boolean north = surroundingTiles.get(EnumCardinalPoint.NORTH).getProperty().contains(EnumProperty.CONNECT_TO_WIRE);
		boolean est = surroundingTiles.get(EnumCardinalPoint.EST).getProperty().contains(EnumProperty.CONNECT_TO_WIRE);
		boolean south = surroundingTiles.get(EnumCardinalPoint.SOUTH).getProperty().contains(EnumProperty.CONNECT_TO_WIRE);
		boolean west = surroundingTiles.get(EnumCardinalPoint.WEST).getProperty().contains(EnumProperty.CONNECT_TO_WIRE);

		if (north && est && !south && west) {
			orientTexture((float) (0 * Math.PI / 2));
		} else if (north && est && south && !west) {
			orientTexture((float) (1 * Math.PI / 2));
		} else if (!north && est && south && west) {
			orientTexture((float) (2 * Math.PI / 2));
		} else if (north && !est && south && west) {
			orientTexture((float) (3 * Math.PI / 2));
		} else if (!north && est && !south && west) {
			orientTexture((float) (0 * Math.PI / 2));
		} else if (north && !est && south && !west) {
			orientTexture((float) (1 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (2 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (3 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (0 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (1 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (2 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (3 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (0 * Math.PI / 2));
			// } else if(north && est && !south && west) {
			// orientTexture((float) (0 * Math.PI / 2));
		}

		belongChunk.resetMeshOnUpdate();
	}

	@Override
	protected void renderUpdate(HashMap<EnumCardinalPoint, Tile> surroundingTiles) {
	}

}
