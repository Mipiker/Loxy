package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.Math;

import org.joml.*;

import fr.mipiker.game.item.*;
import fr.mipiker.game.item.gate.*;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.tiles.gate.Gate;
import fr.mipiker.game.ui.*;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.utils.SelectionUtils;

public class Player {

	private Camera camera;
	private int speedFactor = 1;
	private Tile selectedTile;
	private SlotBar slotBar;
	private Vector3f velocity = new Vector3f();
	private SoundManager soundManager;

	public Player(Camera camera, Hud hud, SoundManager soundManager, Window window) {
		this.camera = camera;
		this.soundManager = soundManager;
		initSlotBar(hud, window);
	}

	public void update(Input input, Map map, Window window) {
		move(input);
		soundManager.getListener().setPosition(camera.getPosition());
		soundManager.getListener().setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
				(float) (Math.random() * 100)));
		updateSlotBar(input, window);
		if (map != null)
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
		// Select a tile
		Tile newSelectedTile = null;
		if (camera.getPosition().y < 100 && (input.isMouseMoved() || !velocity.equals(new Vector3f(0), 0.001f))) {
			Vector3f dir = SelectionUtils.getRayFromMouse(new Vector2f(input.getMousePosX(), input.getMousePosY()),
					window, camera);
			Rayf ray = new Rayf(camera.getPosition(), dir);
			Planef plane = new Planef(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(1, 0, 0));
			float t = Intersectionf.intersectRayPlane(ray, plane, 0.0001f);
			if (t != -1) {
				Vector3f intersect = camera.getPosition().add(dir.mul(t), new Vector3f());
				newSelectedTile = map
						.getTile(new Vector2i((int) Math.floor(intersect.x), (int) Math.floor(intersect.z)));
			}
		}
		if (selectedTile != null) {
			selectedTile.getMesh().getMaterial().setAmbientStrength(1);
			selectedTile.getBelongChunk().resetMeshOnUpdate();
		}
		if (newSelectedTile != null)
			selectedTile = newSelectedTile;
		if (selectedTile != null) {
			selectedTile = actionSelect(input, map, selectedTile, soundManager);
			selectedTile.getMesh().getMaterial().setAmbientStrength(2);
			selectedTile.getBelongChunk().resetMeshOnUpdate();
		}
	}

	private Tile actionSelect(Input input, Map map, Tile tile, SoundManager soundManager) {
		SoundSource ss;
		if (input.isMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			Item selected = slotBar.getSelectedSlot().getItem();
			if (selected != null) {
				EnumTiles type = EnumTiles.getTile(selected.TYPE);
				if (type != null) {
					if (type != tile.TYPE) {
						tile = Tile.newTile(type, tile.getBelongChunk(), tile.getPos());
						map.setTile(tile);
						ss = soundManager.getSoundSource("place");
						ss.setPosition(new Vector3f(tile.getPos().getWorldPos().x, 0, tile.getPos().getWorldPos().y));
						ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
								(float) (Math.random() * 100)));
						ss.play();
					}
				}
			}
		}
		if (!(tile instanceof Empty) && input.isMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT)) {
			tile = new Empty(tile.getBelongChunk(), tile.getPos());
			map.setTile(tile);
			ss = soundManager.getSoundSource("delete");
			ss.setPosition(new Vector3f(tile.getPos().getWorldPos().x, 0, tile.getPos().getWorldPos().y));
			ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
					(float) (Math.random() * 100)));
			ss.play();
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && tile instanceof Switch) {
			((Switch) tile).setPower(!((Switch) tile).isPowered());
			ss = soundManager.getSoundSource("action");
			ss.setPosition(new Vector3f(tile.getPos().getWorldPos().x, 0, tile.getPos().getWorldPos().y));
			ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
					(float) (Math.random() * 100)));
			ss.play();
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && (tile instanceof Gate)) {
			((Gate) tile).rotate();
			ss = soundManager.getSoundSource("action");
			ss.setPosition(new Vector3f(tile.getPos().getWorldPos().x, 0, tile.getPos().getWorldPos().y));
			ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
					(float) (Math.random() * 100)));
			ss.play();
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && (tile instanceof Wire)) {
			if (((Wire) tile).canBeBridged()) {
				ss = soundManager.getSoundSource("action");
				ss.setPosition(new Vector3f(tile.getPos().getWorldPos().x, 0, tile.getPos().getWorldPos().y));
				ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
						(float) (Math.random() * 100)));
				ss.play();
				((Wire) tile).setBridge(!((Wire) tile).isBridge());
			}
		}
		if (input.getLastKeyState(GLFW_KEY_F5) == GLFW_PRESS)
			tile.mustUpdate();
		if (input.getLastKeyState(GLFW_KEY_F4) == GLFW_PRESS)
			System.out.println("\n" + tile.toString());

		return tile;
	}

	public void updateSlotBar(Input input, Window window) {
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

	private void initSlotBar(Hud hud, Window window) {
		slotBar = new SlotBar(10);
		slotBar.addItem(new WireItem());
		slotBar.addItem(new PowerItem());
		slotBar.addItem(new OrGateItem());
		slotBar.addItem(new XorGateItem());
		slotBar.addItem(new AndGateItem());
		slotBar.addItem(new InvGateItem());
		resetPosSlotBar(window);
	}

	public void resetPosSlotBar(Window window) {
		int spacebtw = 10;
		float posX = (window.getSize().x / 2f) - (((Slot.SIZE + spacebtw) * slotBar.getSize()) / 2f);
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

	public SlotBar getSlotBar() {
		return slotBar;
	}

	public Camera getCamera() {
		return camera;
	}
}
