package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.Math;

import org.joml.*;

import fr.mipiker.game.item.Item;
import fr.mipiker.game.tiles.*;
import fr.mipiker.game.tiles.gate.Gate;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.utils.SelectionUtils;

public class Selector {

	private Tile selectedTile;

	public void selectTile(Input input, Map map, Window window, Player player) {
		// Select a tile
		Tile newSelectedTile = null;
		if (input.isMouseMoved() || !player.getVelocity().equals(new Vector3f(0), 0.001f)) {
			Vector3f dir = SelectionUtils.getRayFromMouse(new Vector2f(input.getMousePosX(), input.getMousePosY()),
					window, player.getCamera());
			Rayf ray = new Rayf(player.getCamera().getPosition(), dir);
			Planef plane = new Planef(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(1, 0, 0));
			float t = Intersectionf.intersectRayPlane(ray, plane, 0.0001f);
			if (t != -1) {
				Vector3f intersect = player.getCamera().getPosition().add(dir.mul(t), new Vector3f());
				newSelectedTile = map
						.getTile(new Vector2i((int) Math.floor(intersect.x), (int) Math.floor(intersect.z)));
			}
		}
		// Un highlight the previous selected tile
		if (selectedTile != null) {
			selectedTile.getMesh().getMaterial().setAmbientStrength(1);
			selectedTile.getBelongChunk().resetMeshOnUpdate();
		}
		if (newSelectedTile != null)
			selectedTile = newSelectedTile;
		// Highlight the new selected tile
		if (selectedTile != null) {
			selectedTile.getMesh().getMaterial().setAmbientStrength(2);
			selectedTile.getBelongChunk().resetMeshOnUpdate();
		}
	}

	void action(Input input, Map map, Item selectedItem, SoundManager soundManager) {
		SoundSource ss;
		if (input.isMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			if (selectedItem != null) {
				EnumTiles type = EnumTiles.getTile(selectedItem.TYPE);
				if (type != null) {
					if (type != selectedTile.TYPE) {
						selectedTile = Tile.newTile(type, selectedTile.getBelongChunk(), selectedTile.getPos());
						map.setTile(selectedTile);
						if (Settings.SOUNDS) {
							ss = soundManager.getSoundSource("place");
							ss.setPosition(
									new Vector3f(selectedTile.getPos().getWorldPos().x, 0,
											selectedTile.getPos().getWorldPos().y));
							ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
									(float) (Math.random() * 100)));
							ss.play();
						}
					}
				}
			}
		}
		if (!(selectedTile instanceof Empty) && input.isMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT)) {
			selectedTile = new Empty(selectedTile.getBelongChunk(), selectedTile.getPos());
			map.setTile(selectedTile);
			if (Settings.SOUNDS) {
				ss = soundManager.getSoundSource("delete");
				ss.setPosition(
						new Vector3f(selectedTile.getPos().getWorldPos().x, 0, selectedTile.getPos().getWorldPos().y));
				ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
						(float) (Math.random() * 100)));
				ss.play();
			}
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && selectedTile instanceof Switch) {
			((Switch) selectedTile).setPower(!((Switch) selectedTile).isPowered());
			if (Settings.SOUNDS) {
				ss = soundManager.getSoundSource("action");
				ss.setPosition(
						new Vector3f(selectedTile.getPos().getWorldPos().x, 0, selectedTile.getPos().getWorldPos().y));
				ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
						(float) (Math.random() * 100)));
				ss.play();
			}
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && selectedTile instanceof Gate) {
			((Gate) selectedTile).rotate();
			if (Settings.SOUNDS) {
				ss = soundManager.getSoundSource("action");
				ss.setPosition(
						new Vector3f(selectedTile.getPos().getWorldPos().x, 0, selectedTile.getPos().getWorldPos().y));
				ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
						(float) (Math.random() * 100)));
				ss.play();
			}
		}
		if (input.getLastKeyState(GLFW_KEY_E) == GLFW_PRESS && selectedTile instanceof Wire) {
			if (((Wire) selectedTile).canBeBridged()) {
				if (Settings.SOUNDS) {
					ss = soundManager.getSoundSource("action");
					ss.setPosition(new Vector3f(selectedTile.getPos().getWorldPos().x, 0,
							selectedTile.getPos().getWorldPos().y));
					ss.setSpeed(new Vector3f((float) (Math.random() * 100), (float) (Math.random() * 100),
							(float) (Math.random() * 100)));
					ss.play();
				}
				((Wire) selectedTile).setBridge(!((Wire) selectedTile).isBridge());
			}
		}
		if (input.getLastKeyState(GLFW_KEY_F5) == GLFW_PRESS)
			selectedTile.mustUpdate();
		if (input.getLastKeyState(GLFW_KEY_F4) == GLFW_PRESS)
			System.out.println("\n" + selectedTile.toString());
	}

	Tile getSelectedTile() {
		return selectedTile;
	}
}
