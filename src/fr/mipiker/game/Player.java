package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.*;
import fr.mipiker.game.item.*;
import fr.mipiker.game.item.gate.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.tiles.gate.*;
import fr.mipiker.game.ui.*;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.utils.SelectionUtils;

public class Player {

	private Camera camera;
	private int speedFactor = 1;
	private Tile selectedTile;
	private Tile lastSelectedTile;
	private SlotBar slotBar;
	private Vector3f velocity = new Vector3f();

	public Player(Camera camera, Hud hud, Window window) {
		this.camera = camera;
		initSlotBar(hud, window);
	}

	public void update(Input input, Map map, Window window) {
		move(input);
		updateSlotBar(input, window);
		select(input, map, window);
	}

	public void move(Input input) {
		if (input.isKeyPress(GLFW_KEY_W))
			velocity.z += -0.01f * speedFactor;
		else
			velocity.z /= 1.01;
		if (input.isKeyPress(GLFW_KEY_S))
			velocity.z += 0.01f * speedFactor;
		else
			velocity.z /= 1.01;
		if (input.isKeyPress(GLFW_KEY_A))
			velocity.x += -0.01f * speedFactor;
		else
			velocity.x /= 1.01;
		if (input.isKeyPress(GLFW_KEY_D))
			velocity.x += 0.01f * speedFactor;
		else
			velocity.x /= 1.01;
		if (input.isKeyPress(GLFW_KEY_SPACE))
			velocity.y += 0.01f * speedFactor;
		else
			velocity.y /= 1.01;
		if (input.isKeyPress(GLFW_KEY_LEFT_CONTROL))
			velocity.y += -0.01f * speedFactor;
		else
			velocity.y /= 1.001;
		velocity.min(new Vector3f(0.05f)).max(new Vector3f(-0.05f));
		camera.moveAlongAxis(new Vector3f(velocity.x, velocity.y, velocity.z));
	}

	public void select(Input input, Map map, Window window) {

		Tile newSelected = null;
		boolean hasSeached = false;
		// Select a tile
		if (camera.getPosition().y < 50) {
			if ((input.isMouseMoved() || !velocity.equals(new Vector3f(0), 0.001f))) {
				hasSeached = true;
				Vector2f mousePos = new Vector2f(input.getMousePosX(), input.getMousePosY());
				find:
				for (Chunk chunk : map.getChunks().values()) {
					Vector2f[][] positions = new Vector2f[Chunk.SIZE][Chunk.SIZE];
					for (int y_ = 0; y_ < positions[0].length; y_++)
						for (int x_ = 0; x_ < positions.length; x_++)
							positions[x_][y_] = new Vector2f(chunk.getPos()).mul(Chunk.SIZE).add(x_, y_);

					Vector2f[][][] size = new Vector2f[Chunk.SIZE][Chunk.SIZE][2];
					for (int y_ = 0; y_ < positions[0].length; y_++) {
						for (int x_ = 0; x_ < positions.length; x_++) {
							size[x_][y_][0] = new Vector2f(0, 0);
							size[x_][y_][1] = new Vector2f(1, 1);
						}
					}

					Vector2i pos = SelectionUtils.selectTileFromMouse(mousePos, window, camera, positions, size);

					if (pos != null) {
						newSelected = chunk.getTile(pos);
						break find;
					}
				}

			}

			if (hasSeached) {
				if (selectedTile != null)
					lastSelectedTile = selectedTile;
				selectedTile = newSelected;
			}

			if (selectedTile != null) {
				Tile prev = selectedTile;
				selectedTile = actionSelect(input, map, selectedTile);
				if (lastSelectedTile != null && !selectedTile.getPos().equals(lastSelectedTile.getPos())) {
					lastSelectedTile.getMesh().getMaterial().setAmbientStrength(1);
					lastSelectedTile.getBelongChunk().resetMeshOnUpdate();
					selectedTile.getMesh().getMaterial().setAmbientStrength(2);
					lastSelectedTile.getBelongChunk().resetMeshOnUpdate();
				}
				if (!prev.equals(selectedTile)) {
					selectedTile.getMesh().getMaterial().setAmbientStrength(2);
					lastSelectedTile.getBelongChunk().resetMeshOnUpdate();
				}
			}
		} else {
			if (lastSelectedTile != null) {
				lastSelectedTile.getMesh().getMaterial().setAmbientStrength(1);
				lastSelectedTile.getBelongChunk().resetMeshOnUpdate();
			}
		}
	}

	private Tile actionSelect(Input input, Map map, Tile tile) {
		if (input.isMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			if (slotBar.getSelectedSlot().getItem() != null) {
				if (!(tile instanceof Wire) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.WIRE) {
					tile = new Wire(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				} else if (!(tile instanceof Switch) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.POWER) {
					tile = new Switch(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				} else if (!(tile instanceof AndGate) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.AND_GATE) {
					tile = new AndGate(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				} else if (!(tile instanceof OrGate) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.OR_GATE) {
					tile = new OrGate(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				} else if (!(tile instanceof XorGate) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.XOR_GATE) {
					tile = new XorGate(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				} else if (!(tile instanceof InvGate) && slotBar.getSelectedSlot().getItem().ITEM_TYPE == EnumItem.INV_GATE) {
					tile = new InvGate(tile.getBelongChunk(), tile.getPos());
					map.setTile(tile);
				}
			}
		}
		if (!(tile instanceof Empty) && input.isMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT)) {
			tile = new Empty(tile.getBelongChunk(), tile.getPos());
			map.setTile(tile);
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && tile instanceof Switch)
			((Switch) tile).setPower(!((Switch) tile).isPowered());
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && (tile instanceof Gate))
			((Gate) tile).rotate();
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && (tile instanceof Wire))
			((Wire) tile).setBridge(!((Wire) tile).isBridge());
		if (input.getLastKeyState(GLFW_KEY_F5) == GLFW_PRESS)
			tile.mustUpdate();
		if (input.getLastKeyState(GLFW_KEY_F4) == GLFW_PRESS)
			System.out.println("\n" + tile.toString());

		return tile;
	}

	public void updateSlotBar(Input input, Window window) {
		if (window.isResized())
			resetPosSlotBar(window);
		if (input.isMouseScroll()) {
			slotBar.getSelectedSlot().getComponentSlot().getTransformation().translate(0, -Slot.SIZE, 0);
			if (slotBar.getSelectedSlot().hasItem())
				slotBar.getSelectedSlot().getComponentItem().getTransformation().translate(0, -Slot.SIZE, 0);
			slotBar.moveSelectSlot(input.getMouseScroll());
			slotBar.getSelectedSlot().getComponentSlot().getTransformation().translate(0, Slot.SIZE, 0);
			if (slotBar.getSelectedSlot().hasItem())
				slotBar.getSelectedSlot().getComponentItem().getTransformation().translate(0, Slot.SIZE, 0);
		}
	}

	public void initSlotBar(Hud hud, Window window) {
		slotBar = new SlotBar(10);
		slotBar.addItem(new WireItem());
		slotBar.addItem(new PowerItem());
		slotBar.addItem(new OrGateItem());
		slotBar.addItem(new XorGateItem());
		slotBar.addItem(new AndGateItem());
		slotBar.addItem(new InvGateItem());
		resetPosSlotBar(window);
		for (Slot slot : slotBar.getSlots()) {
			hud.addComponent(slot.getComponentSlot());
			if (slot.hasItem())
				hud.addComponent(slot.getComponentItem());
		}
	}

	private void resetPosSlotBar(Window window) {
		int spacebtw = 10;
		float posX = (window.getWidth() / 2f) - (((Slot.SIZE + spacebtw) * slotBar.getSize()) / 2f);
		for (int i = 0; i < slotBar.getSize(); i++) {
			Slot slot = slotBar.getSlot(i);
			slot.getComponentSlot().getTransformation().setTranslation(posX, spacebtw, 0);
			if (slot.hasItem())
				slot.getComponentItem().getTransformation().setTranslation(posX, spacebtw, 0.2f);
			posX += Slot.SIZE + spacebtw;
		}
		slotBar.getSelectedSlot().getComponentSlot().getTransformation().translate(0, Slot.SIZE, 0);
		if (slotBar.getSelectedSlot().hasItem())
			slotBar.getSelectedSlot().getComponentItem().getTransformation().translate(0, Slot.SIZE, 0);
	}

	public Camera getCamera() {
		return camera;
	}

}