package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.lang.Math;

import org.joml.*;

import fr.mipiker.game.item.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.isisEngine.Input;

public class RegionSelector {

	// Selection
	private Vector2i prevPos1, prevPos2;
	private Vector2i lockedPos1, lockedPos2;
	private Vector2i posMinRegionSelected, posMaxRegionSelected, sizeRegionSelected;
	private boolean executedOnSelecting;
	private Vector2i prevTileSelectedPos;
	private EnumItem lastSelectedItem;
	// Stack
	private EnumCardinalPoint directionStack;
	private int nbStack;

	RegionSelector() {
		prevPos1 = new Vector2i();
		prevPos2 = new Vector2i();
	}

	void selectRegion(Vector2i startPos, Vector2i endPos, Map map) {
		// Unhightlight previous selection only if needed
		if (!startPos.equals(prevPos1) || !endPos.equals(prevPos2)) {
			hightLightRegion(false, false, prevPos1, prevPos2, map);
			prevPos1 = startPos;
			prevPos2 = endPos;
		}
		// Execute an action on selecting
		if (isLockedEndPos() && isLockedStartPos() && !executedOnSelecting)
			onSelecting(map);
		else if (!isLockedEndPos() || !isLockedStartPos())
			executedOnSelecting = false;
		// Hightlight new selection
		hightLightRegion(true, false, startPos, endPos, map);
	}

	void action(Input input, Map map, Item selectedItem, Tile selectedTile) {

		// On switching selected item
		boolean passVIPPaste = false;
		if (selectedItem.TYPE != EnumItem.PASTE && lastSelectedItem == EnumItem.PASTE) {
			if (prevTileSelectedPos != null)
				hightLightRegion(false, false, prevTileSelectedPos,
						new Vector2i(prevTileSelectedPos).add(sizeRegionSelected).add(-1, -1), map);
		} else if (selectedItem.TYPE == EnumItem.PASTE && lastSelectedItem != EnumItem.PASTE) {
			passVIPPaste = true;
		}
		lastSelectedItem = selectedItem.TYPE;

		if (selectedItem.TYPE == EnumItem.STACK
				&& !selectedTile.getPos().getWorldPos().equals(prevTileSelectedPos)) {
			prevTileSelectedPos = selectedTile.getPos().getWorldPos();

			// Unhighlight the previous area
			if (directionStack == EnumCardinalPoint.SOUTH) {
				hightLightRegion(false, false, new Vector2i(posMinRegionSelected.x, posMaxRegionSelected.y + 1),
						new Vector2i(posMaxRegionSelected.x, posMaxRegionSelected.y + sizeRegionSelected.y * nbStack),
						map);
			} else if (directionStack == EnumCardinalPoint.NORTH) {
				hightLightRegion(false, false, new Vector2i(posMinRegionSelected.x, posMinRegionSelected.y - 1),
						new Vector2i(posMaxRegionSelected.x, posMinRegionSelected.y - sizeRegionSelected.y * nbStack),
						map);
			} else if (directionStack == EnumCardinalPoint.EAST) {
				hightLightRegion(false, false, new Vector2i(posMaxRegionSelected.x + 1, posMinRegionSelected.y),
						new Vector2i(posMaxRegionSelected.x + sizeRegionSelected.x * nbStack, posMaxRegionSelected.y),
						map);
			} else if (directionStack == EnumCardinalPoint.WEST) {
				hightLightRegion(false, false, new Vector2i(posMinRegionSelected.x - 1, posMinRegionSelected.y),
						new Vector2i(posMinRegionSelected.x - sizeRegionSelected.x * nbStack, posMaxRegionSelected.y),
						map);
			}

			// Calcul the stack status
			Vector2i selectedPos = selectedTile.getPos().getWorldPos();
			if (selectedPos.y > posMaxRegionSelected.y && selectedPos.x >= posMinRegionSelected.x &&
					selectedPos.x <= posMaxRegionSelected.x) {
				directionStack = EnumCardinalPoint.SOUTH;
				nbStack = (selectedPos.y - posMaxRegionSelected.y - 1) / sizeRegionSelected.y + 1;
			} else if (selectedPos.y < posMinRegionSelected.y && selectedPos.x >= posMinRegionSelected.x
					&& selectedPos.x <= posMaxRegionSelected.x) {
				directionStack = EnumCardinalPoint.NORTH;
				nbStack = (posMaxRegionSelected.y - selectedPos.y) / sizeRegionSelected.y;
			} else if (selectedPos.x < posMinRegionSelected.x && selectedPos.y >= posMinRegionSelected.y
					&& selectedPos.y <= posMaxRegionSelected.y) {
				directionStack = EnumCardinalPoint.WEST;
				nbStack = (posMaxRegionSelected.x - selectedPos.x) / sizeRegionSelected.x;
			} else if (selectedPos.x > posMaxRegionSelected.x && selectedPos.y >= posMinRegionSelected.y
					&& selectedPos.y <= posMaxRegionSelected.y) {
				directionStack = EnumCardinalPoint.EAST;
				nbStack = (selectedPos.x - posMinRegionSelected.x) / sizeRegionSelected.x;
			} else {
				directionStack = null;
				nbStack = 0;
			}

			System.out.println(directionStack + " " + nbStack);

			// Highlight the new area
			if (directionStack == EnumCardinalPoint.SOUTH) {
				hightLightRegion(true, true, new Vector2i(posMinRegionSelected.x, posMaxRegionSelected.y + 1),
						new Vector2i(posMaxRegionSelected.x, posMaxRegionSelected.y + sizeRegionSelected.y * nbStack),
						map);
			} else if (directionStack == EnumCardinalPoint.NORTH) {
				hightLightRegion(true, true, new Vector2i(posMinRegionSelected.x, posMinRegionSelected.y - 1),
						new Vector2i(posMaxRegionSelected.x, posMinRegionSelected.y - sizeRegionSelected.y * nbStack),
						map);
			} else if (directionStack == EnumCardinalPoint.EAST) {
				hightLightRegion(true, true, new Vector2i(posMaxRegionSelected.x + 1, posMinRegionSelected.y),
						new Vector2i(posMaxRegionSelected.x + sizeRegionSelected.x * nbStack, posMaxRegionSelected.y),
						map);
			} else if (directionStack == EnumCardinalPoint.WEST) {
				hightLightRegion(true, true, new Vector2i(posMinRegionSelected.x - 1, posMinRegionSelected.y),
						new Vector2i(posMinRegionSelected.x - sizeRegionSelected.x * nbStack, posMaxRegionSelected.y),
						map);
			}

		} else if ((selectedItem.TYPE == EnumItem.PASTE
				&& !selectedTile.getPos().getWorldPos().equals(prevTileSelectedPos)) || passVIPPaste) {
			// Unhightlight the previous area
			if (prevTileSelectedPos != null)
				hightLightRegion(false, false, prevTileSelectedPos,
						new Vector2i(prevTileSelectedPos).add(sizeRegionSelected).add(-1, -1), map);
			prevTileSelectedPos = selectedTile.getPos().getWorldPos();
			// Hightlight the new area selected
			hightLightRegion(true, true, prevTileSelectedPos,
					new Vector2i(prevTileSelectedPos).add(sizeRegionSelected).add(-1, -1), map);
		}

		// Action
		if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			prevTileSelectedPos = selectedTile.getPos().getWorldPos();
			if (selectedItem.TYPE == EnumItem.REMOVE) {
				for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
					for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
						Tile tile = map.getTile(new Vector2i(x, y));
						map.setTile(new Empty(tile.getBelongChunk(), tile.getPos()));
					}
				}
			} else if (selectedItem.TYPE == EnumItem.PASTE) {
				int y_ = 0, x_ = 0;
				for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
					for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
						Tile copy = Tile.copy(map.getTile(new Vector2i(x, y)));
						copy.setPosition(new Vector2i(x_ + prevTileSelectedPos.x, y_ + prevTileSelectedPos.y), map);
						map.setTile(copy);
						x_++;
					}
					x_ = 0;
					y_++;
				}
			} else if (selectedItem.TYPE == EnumItem.STACK) {
				if (directionStack == EnumCardinalPoint.NORTH) {
					for (int i = 0; i < nbStack; i++) {
						for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
							for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
								Tile copy = Tile.copy(map.getTile(new Vector2i(x, y)));
								copy.setPosition(new Vector2i(x, y - sizeRegionSelected.y * (i + 1)), map);
								map.setTile(copy);
							}
						}
					}
				} else if (directionStack == EnumCardinalPoint.SOUTH) {
					for (int i = 0; i < nbStack; i++) {
						for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
							for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
								Tile copy = Tile.copy(map.getTile(new Vector2i(x, y)));
								copy.setPosition(new Vector2i(x, y + sizeRegionSelected.y * (i + 1)), map);
								map.setTile(copy);
							}
						}
					}
				} else if (directionStack == EnumCardinalPoint.EAST) {
					for (int i = 0; i < nbStack; i++) {
						for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
							for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
								Tile copy = Tile.copy(map.getTile(new Vector2i(x, y)));
								copy.setPosition(new Vector2i(x + sizeRegionSelected.x * (i + 1), y), map);
								map.setTile(copy);
							}
						}
					}
				} else if (directionStack == EnumCardinalPoint.WEST) {
					for (int i = 0; i < nbStack; i++) {
						for (int y = posMinRegionSelected.y; y <= posMaxRegionSelected.y; y++) {
							for (int x = posMinRegionSelected.x; x <= posMaxRegionSelected.x; x++) {
								Tile copy = Tile.copy(map.getTile(new Vector2i(x, y)));
								copy.setPosition(new Vector2i(x - sizeRegionSelected.x * (i + 1), y), map);
								map.setTile(copy);
							}
						}
					}
				}
			}
		}
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

	/**
	 * Save the selected area
	 */
	private void onSelecting(Map map) {
		executedOnSelecting = true;
		int yMin = Math.min(lockedPos1.y, lockedPos2.y);
		int yMax = Math.max(lockedPos1.y, lockedPos2.y);
		int xMin = Math.min(lockedPos1.x, lockedPos2.x);
		int xMax = Math.max(lockedPos1.x, lockedPos2.x);
		posMinRegionSelected = new Vector2i(xMin, yMin);
		posMaxRegionSelected = new Vector2i(xMax, yMax);
		sizeRegionSelected = new Vector2i(xMax - xMin + 1, yMax - yMin + 1);
	}

	void lockStartPos(Vector2i startPos) {
		lockedPos1 = startPos;
	}
	void lockEndPos(Vector2i endPos) {
		this.lockedPos2 = endPos;
	}
	Vector2i getLockedStartPos() {
		return lockedPos1;
	}
	Vector2i getLockedEndPos() {
		return lockedPos2;
	}
	boolean isLockedStartPos() {
		return lockedPos1 != null;
	}
	boolean isLockedEndPos() {
		return lockedPos2 != null;
	}

	void reset(Map map, Item selectedItem) {
		if (lockedPos1 != null && lockedPos2 != null)
			hightLightRegion(false, false, lockedPos1, lockedPos2, map);
		if (selectedItem.TYPE == EnumItem.PASTE) {
			hightLightRegion(false, false, prevTileSelectedPos,
					new Vector2i(prevTileSelectedPos).add(sizeRegionSelected).add(-1, -1), map);
		}
		lockedPos1 = null;
		lockedPos2 = null;
		executedOnSelecting = false;
	}
}
