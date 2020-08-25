package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import fr.mipiker.game.item.*;
import fr.mipiker.game.item.gate.*;
import fr.mipiker.game.ui.*;
import fr.mipiker.isisEngine.*;

public class Player {

	private Camera camera;
	private int speedFactor = 1;
	private SlotBar displayedSlotBar, selectionSlotBar, tileSlotBar;
	private Vector3f velocity = new Vector3f();
	private SoundManager soundManager;
	private Selector selector;
	private RegionSelector regionSelector;

	public Player(Camera camera, Hud hud, SoundManager soundManager, Window window) {
		this.camera = camera;
		this.soundManager = soundManager;
		initSlotBar(hud, window);
		selector = new Selector();
		regionSelector = new RegionSelector();
	}

	public void update(Input input, Map map, Window window) {
		move(input);
		soundManager.getListener().setPosition(camera.getPosition());
		soundManager.getListener().setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
				(float) (Math.random() * 100)));
		updateSlotBar(input, window, map);
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
		if (camera.getPosition().y < 10)
			camera.setPosition(new Vector3f(camera.getPosition().x, 10, camera.getPosition().z));
	}

	private void select(Input input, Map map, Window window) {
		if (displayedSlotBar.equals(tileSlotBar)) { // Tile mode
			selector.selectTile(input, map, window, this);
			if (selector.getSelectedTile() != null)
				selector.action(input, map, displayedSlotBar.getSelectedSlot().getItem(), soundManager);
		} else { // Area selection mode
			selector.selectTile(input, map, window, this);
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT) && !regionSelector.isLockedStartPos()
					&& !regionSelector.isLockedEndPos()) // Start region selection
				regionSelector.lockStartPos(selector.getSelectedTile().getPos().getWorldPos());
			if (input.isLastMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT) && regionSelector.isLockedStartPos()
					&& !regionSelector.isLockedEndPos()) // End region selection
				regionSelector.lockEndPos(selector.getSelectedTile().getPos().getWorldPos());
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT) && regionSelector.isLockedStartPos()
					&& regionSelector.isLockedEndPos()) // reset region selection
				regionSelector.reset(map, displayedSlotBar.getSelectedSlot().getItem());
			if (regionSelector.isLockedStartPos()) {
				regionSelector.selectRegion(regionSelector.getLockedStartPos(),
						regionSelector.isLockedEndPos() ? regionSelector.getLockedEndPos()
								: selector.getSelectedTile().getPos().getWorldPos(),
						map);
				if (regionSelector.isLockedEndPos() && selector.getSelectedTile() != null)
					regionSelector.action(input, map, displayedSlotBar.getSelectedSlot().getItem(),
							selector.getSelectedTile());
			}
		}
	}

	public void updateSlotBar(Input input, Window window, Map map) {
		if (input.isLastKeyPress(GLFW_KEY_Q)) {
			displayedSlotBar.unShow();
			if (displayedSlotBar.equals(selectionSlotBar)) {
				regionSelector.reset(map, displayedSlotBar.getSelectedSlot().getItem());
				displayedSlotBar = tileSlotBar;
			} else
				displayedSlotBar = selectionSlotBar;
			displayedSlotBar.show();
			displayedSlotBar.resetPos(window.getSize().x);
		}
		if (input.isMouseScroll()) {
			displayedSlotBar.getSelectedSlot().getComponentSlot().getTransformation().translate(0, -Slot.SIZE, 0);
			if (displayedSlotBar.getSelectedSlot().hasItem())
				displayedSlotBar.getSelectedSlot().getComponentItem().getTransformation().translate(0, -Slot.SIZE, 0);
			displayedSlotBar.moveSelectSlot(input.getMouseScroll());
			displayedSlotBar.getSelectedSlot().getComponentSlot().getTransformation().translate(0, Slot.SIZE, 0);
			if (displayedSlotBar.getSelectedSlot().hasItem())
				displayedSlotBar.getSelectedSlot().getComponentItem().getTransformation().translate(0, Slot.SIZE, 0);
		}
	}

	private void initSlotBar(Hud hud, Window window) {
		tileSlotBar = new SlotBar(6, hud);
		tileSlotBar.addItem(new WireItem());
		tileSlotBar.addItem(new PowerItem());
		tileSlotBar.addItem(new OrGateItem());
		tileSlotBar.addItem(new XorGateItem());
		tileSlotBar.addItem(new AndGateItem());
		tileSlotBar.addItem(new InvGateItem());
		tileSlotBar.resetPos(window.getSize().x);
		selectionSlotBar = new SlotBar(3, hud);
		selectionSlotBar.addItem(new RemoveItem());
		selectionSlotBar.addItem(new PasteItem());
		selectionSlotBar.addItem(new StackItem());
		selectionSlotBar.resetPos(window.getSize().x);
		displayedSlotBar = tileSlotBar;
	}

	public SlotBar getSlotBar() {
		return displayedSlotBar;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public Camera getCamera() {
		return camera;
	}
}
