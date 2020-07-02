package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.lang.Math;

import org.joml.*;

import fr.mipiker.game.item.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.isisEngine.Input;

public class RegionSelector {

	private Vector2i prevStartPos, prevEndPos;
	private Vector2i lockedStartPos, lockedEndPos;
	private Vector2i prevPosPaste, prevSizePaste;
	private boolean wasPaste;
	private boolean executedOnSelecting;
	private Tile[][] regionSelected, regionToBeErased;

	RegionSelector() {
		prevStartPos = new Vector2i();
		prevEndPos = new Vector2i();
		prevPosPaste = new Vector2i();
		prevSizePaste = new Vector2i();
	}

	void selectRegion(Vector2i startPos, Vector2i endPos, Map map) {
		if (!startPos.equals(prevStartPos) || !endPos.equals(prevEndPos)) {
			// Unhightlight previous selection
			hightLightRegion(false, false, prevStartPos, prevEndPos, map);
			prevStartPos = startPos;
			prevEndPos = endPos;
		}
		if (isLockedEndPos() && isLockedStartPos() && !executedOnSelecting)
			onSelecting(map);
		else if (!isLockedEndPos() || !isLockedStartPos())
			executedOnSelecting = false;
		// Hightlight new selection
		hightLightRegion(true, isLockedStartPos() && isLockedEndPos(), startPos, endPos, map);
	}

	private void hightLightRegion(boolean hightLight, boolean blue, Vector2i startPos, Vector2i endPos, Map map) {
		int yMin = Math.min(startPos.y, endPos.y);
		int yMax = Math.max(startPos.y, endPos.y);
		int xMin = Math.min(startPos.x, endPos.x);
		int xMax = Math.max(startPos.x, endPos.x);
		for (int y = yMin; y <= yMax; y++) {
			for (int x = xMin; x <= xMax; x++) {
				Tile tile = map.getTile(new Vector2i(x, y));
				tile.getMesh().getMaterial().setAmbientStrength(hightLight ? 2 : 1);
				tile.getMesh().getMaterial().setAmbientLight(blue ? new Vector3f(0.5f, 0.5f, 1) : new Vector3f(1));
				tile.getBelongChunk().resetMeshOnUpdate();
			}
		}
	}

	void action(Input input, Map map, Item selectedItem, Tile selectedTile) {
		if (selectedItem.TYPE == EnumItem.PASTE) {
			// Undo previous iterration
			if (regionToBeErased != null)
				for (int y = 0; y < regionSelected[0].length; y++)
					for (int x = 0; x < regionSelected.length; x++)
						map.setTile(regionToBeErased[x][y]);
			hightLightRegion(false, false, prevPosPaste, prevPosPaste.add(prevSizePaste, new Vector2i()), map);
			// Do this iteration
			Vector2i pos = selectedTile.getPos().getWorldPos();
			Vector2i size = lockedEndPos.sub(lockedStartPos, new Vector2i());
			this.prevPosPaste = pos;
			this.prevSizePaste = size;
			wasPaste = true;
			regionToBeErased = saveRegion(pos, pos.add(size, new Vector2i()), map);
			for (int y = 0; y < regionSelected[0].length; y++) {
				for (int x = 0; x < regionSelected.length; x++) {
					Tile copy = Tile.copy(regionSelected[x][y]);
					copy.setPosition(regionToBeErased[x][y].getPos(), regionToBeErased[x][y].getBelongChunk());
					map.setTile(copy);
				}
			}
			hightLightRegion(true, true, pos, pos.add(size, new Vector2i()), map);
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT))
				regionToBeErased = null; // If pasting, just not reset the tiles as they was in the previous iteration
		} else if (wasPaste) {
			resetPaste(map);
			wasPaste = false;
		}
		if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			if (selectedItem.TYPE == EnumItem.REMOVE) {
				int yMin = Math.min(lockedStartPos.y, lockedEndPos.y);
				int yMax = Math.max(lockedStartPos.y, lockedEndPos.y);
				int xMin = Math.min(lockedStartPos.x, lockedEndPos.x);
				int xMax = Math.max(lockedStartPos.x, lockedEndPos.x);
				for (int y = yMin; y <= yMax; y++) {
					for (int x = xMin; x <= xMax; x++) {
						Tile tile = map.getTile(new Vector2i(x, y));
						map.setTile(new Empty(tile.getBelongChunk(), tile.getPos()));
					}
				}
			}
		}
	}

	/**
	 * Save the selected area
	 */
	private void onSelecting(Map map) {
		executedOnSelecting = true;
		regionSelected = saveRegion(lockedStartPos, lockedEndPos, map);
	}

	private Tile[][] saveRegion(Vector2i start, Vector2i end, Map map) {
		int yMin = Math.min(start.y, end.y);
		int yMax = Math.max(start.y, end.y);
		int xMin = Math.min(start.x, end.x);
		int xMax = Math.max(start.x, end.x);
		Tile[][] region = new Tile[xMax - xMin + 1][yMax - yMin + 1];
		for (int y = 0; y < region[0].length; y++)
			for (int x = 0; x < region.length; x++)
				region[x][y] = map.getTile(new Vector2i(xMin + x, yMin + y));
		return region;
	}

	void lockStartPos(Vector2i startPos) {
		lockedStartPos = startPos;
	}
	void lockEndPos(Vector2i endPos) {
		this.lockedEndPos = endPos;
	}
	Vector2i getLockedStartPos() {
		return lockedStartPos;
	}
	Vector2i getLockedEndPos() {
		return lockedEndPos;
	}
	boolean isLockedStartPos() {
		return lockedStartPos != null;
	}
	boolean isLockedEndPos() {
		return lockedEndPos != null;
	}

	void reset(Map map) {
		if (lockedStartPos != null && lockedEndPos != null)
			hightLightRegion(false, false, lockedStartPos, lockedEndPos, map);
		lockedStartPos = null;
		lockedEndPos = null;
		resetPaste(map);
		executedOnSelecting = false;
	}
	private void resetPaste(Map map) {
		if (regionToBeErased != null)
			for (int y = 0; y < regionSelected[0].length; y++)
				for (int x = 0; x < regionSelected.length; x++)
					map.setTile(regionToBeErased[x][y]);
		hightLightRegion(false, false, prevPosPaste, prevPosPaste.add(prevSizePaste, new Vector2i()), map);
		prevPosPaste = new Vector2i();
		prevSizePaste = new Vector2i();
	}
}
