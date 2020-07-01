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

	RegionSelector() {
		prevStartPos = new Vector2i();
		prevEndPos = new Vector2i();
	}

	void selectRegion(Vector2i startPos, Vector2i endPos, Map map) {
		// Unhightlight previous selection
		hightLightRegion(false, false, prevStartPos, prevEndPos, map);
		// Hightlight new selection
		prevStartPos = startPos;
		prevEndPos = endPos;
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

	void action(Input input, Map map, Item selectedItem) {
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
		hightLightRegion(false, false, lockedStartPos, lockedEndPos, map);
		lockedStartPos = null;
		lockedEndPos = null;
	}
}
