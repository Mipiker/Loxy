package fr.mipiker.game.ui;

import static fr.mipiker.game.ui.ButtonPositionX.*;
import static fr.mipiker.game.ui.ButtonPositionY.*;
import static java.lang.Math.min;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.io.File;
import java.util.*;

import org.joml.*;
import org.lwjgl.glfw.GLFW;

import fr.mipiker.game.*;
import fr.mipiker.game.Map;
import fr.mipiker.game.utils.UtilsMapIO;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.loader.FontLoader;

public class PageManager {

	static TexturedFont font;
	private static String fontPath = "resources/Optimus.otf";

	private float margin;
	private int fontHeight;
	private String page = "Menu";
	private Window window;
	private HashMap<String, Button> bMenu = new HashMap<>();
	private HashMap<String, Button> bOptions = new HashMap<>();
	private HashMap<String, Button> bWorld = new HashMap<>();
	private HashMap<String, Button> bNewWorld = new HashMap<>();
	private boolean firstRender;
	private String selectedActionWorld = "Load";

	/*
	 * Need to some refactoring
	 */
	public PageManager(Hud hud, Window window, MainLoxy game) {
		this.window = window;
		font = FontLoader.loadFont(fontPath, window.getSize().y / 20);

		// Menu
		bMenu.put("Continue", new Button("Continue", hud, LEFT, LINE0));
		bMenu.get("Continue").setHoveredLeftClickCallback(() -> {
			page = "In Game";
			Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
			unShow(bMenu, hud);
			firstRender = true;
		});
		bMenu.put("World", new Button("World", hud, LEFT, LINE1));
		bMenu.get("World").setHoveredLeftClickCallback(() -> {
			page = "World";
			unShow(bMenu, hud);
			firstRender = true;
			resetButtonWorld(hud, game);
		});
		bMenu.put("Options", new Button("Options", hud, LEFT, LINE2));
		bMenu.get("Options").setHoveredLeftClickCallback(() -> {
			page = "Options";
			unShow(bMenu, hud);
			firstRender = true;
		});
		bMenu.put("Quit Game", new Button("Quit Game", hud, LEFT, ButtonPositionY.BOTTOM));
		bMenu.get("Quit Game").setHoveredLeftClickCallback(() -> GLFW.glfwSetWindowShouldClose(window.getID(), true));
		for (Button b : bMenu.values()) {
			b.setHoveredCallback(() -> {
				b.getComponent().getTransformation().scaling(1.25f);
				b.getComponent().setPos(new Vector2f(b.getPos().x, b.getPos().y - b.getComponent().getSize().y / 8));
			});
		}
		// Option
		bOptions.put("Render Distance", new Button("Render Distance", hud, LEFT, LINE0));
		bOptions.put("Value Render Distance",
				new Button(Integer.toString(Settings.RENDER_DISTANCE), hud, RIGHT, LINE0));
		bOptions.put("Auto Save Time", new Button("Auto Save Time", hud, LEFT, LINE1));
		bOptions.put("Value Auto Save Time",
				new Button(Integer.toString(Settings.AUTO_SAVE_TIME) + "s", hud, RIGHT, LINE1));
		bOptions.put("Sounds", new Button("Sounds", hud, LEFT, LINE2));
		bOptions.put("Value Sounds", new Button(Settings.SOUNDS ? "on" : "off", hud, RIGHT, LINE2));
		for (Button b : bOptions.values()) {
			b.setMouseAlignedCallback(() -> {
				b.getComponent().getTransformation().scaling(1.25f);
				b.getComponent().setPos(new Vector2f(b.getPos().x, b.getPos().y - b.getComponent().getSize().y / 8));
			});
		}
		bOptions.get("Value Render Distance").setAlignedLeftClickCallback(() -> {
			Settings.RENDER_DISTANCE++;
			bOptions.get("Value Render Distance").setText(Integer.toString(Settings.RENDER_DISTANCE));
		});
		bOptions.get("Value Render Distance").setAlignedRightClickCallback(() -> {
			if (Settings.RENDER_DISTANCE > 1) {
				Settings.RENDER_DISTANCE--;
				bOptions.get("Value Render Distance").setText(Integer.toString(Settings.RENDER_DISTANCE));
			}
		});
		bOptions.get("Value Auto Save Time").setAlignedLeftClickCallback(() -> {
			Settings.AUTO_SAVE_TIME++;
			bOptions.get("Value Auto Save Time").setText(Integer.toString(Settings.AUTO_SAVE_TIME) + "s");
			game.getMap().resetTimer();
		});
		bOptions.get("Value Auto Save Time").setAlignedRightClickCallback(() -> {
			if (Settings.AUTO_SAVE_TIME > 1) {
				Settings.AUTO_SAVE_TIME--;
				bOptions.get("Value Auto Save Time").setText(Integer.toString(Settings.AUTO_SAVE_TIME) + "s");
				game.getMap().resetTimer();
			}
		});
		bOptions.get("Value Sounds").setAlignedLeftClickCallback(() -> {
			Settings.SOUNDS = !Settings.SOUNDS;
			bOptions.get("Value Sounds").setText(Settings.SOUNDS ? "on" : "off");
		});

		Button b = new Button("Back", hud, LEFT, BOTTOM);
		b.setHoveredCallback(() -> {
			b.getComponent().getTransformation().scaling(1.25f);
			b.getComponent().setPos(new Vector2f(b.getPos().x, b.getPos().y - b.getComponent().getSize().y / 8));
		});
		b.setHoveredLeftClickCallback(() -> {
			page = "Menu";
			firstRender = true;
			unShow(bOptions, hud);
		});
		bOptions.put("Back", b);

		// World
		resetButtonWorld(hud, game);

		// New World
		resetButtonNewWorld(hud, game);
	}

	public void update(Input input, Player player, MainLoxy game) {
		Vector2f windowSize = new Vector2f(window.getSize());

		margin = windowSize.y * 0.25f;
		if (font.getSize() != null)
			fontHeight = font.getSize().y;

		switch (page) {
		case "Menu":
			for (Button b : bMenu.values())
				b.update(input, windowSize, margin, fontHeight);
			break;
		case "Options":
			for (Button b : bOptions.values())
				b.update(input, windowSize, margin, fontHeight);
			break;
		case "In Game":
			if (input.isLastKeyPress(GLFW_KEY_ESCAPE)) {
				page = "Menu";
				firstRender = true;
				player.getSlotBar().unShow();
				new Thread(new Runnable() {
					@Override
					public void run() {
						UtilsMapIO.save(game.getMap(), game.getPlayer());
					}
				}).start();
			}
			break;
		case "World":
			try {
				for (Button b : bWorld.values()) {
					if (b.getText().equalsIgnoreCase(selectedActionWorld))
						b.setSelected(true);
					else
						b.setSelected(false);
					b.update(input, windowSize, margin, fontHeight);
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
			break;
		case "New World":
			for (Button b : bNewWorld.values())
				b.update(input, windowSize, margin, fontHeight);
			break;
		}
	}

	public void render(Hud hud, Player player) {
		if (window.isResized()) {
			font = FontLoader.loadFont(fontPath, window.getSize().y / 20);
			firstRender = true;
		}

		switch (page) {
		case "Menu":
			if (firstRender) {
				for (Button b : bMenu.values()) {
					b.resetTextComponent();
					firstRender = false;
				}
				for (Button b : bMenu.values())
					b.show();
			}
			break;
		case "In Game":
			if (firstRender) {
				player.getSlotBar().show();
				player.getSlotBar().resetPos(window.getSize().x);
				firstRender = false;
			}
			break;
		case "Options":
			if (firstRender) {
				for (Button b : bOptions.values()) {
					b.resetTextComponent();
					firstRender = false;
				}
			}
			for (Button b : bOptions.values())
				b.show();
			break;
		case "World":
			if (firstRender) {
				for (Button b : bWorld.values()) {
					b.resetTextComponent();
					firstRender = false;
				}
			}
			for (Button b : bWorld.values())
				b.show();
			break;
		case "New World":
			if (firstRender) {
				for (Button b : bNewWorld.values()) {
					b.resetTextComponent();
					firstRender = false;
				}
			}
			for (Button b : bNewWorld.values()) {
				if (b != bNewWorld.get("Chunk Size") || b != bNewWorld.get("Value Chunk Size"))
					b.show();
			}
			bNewWorld.get("Chunk Size").show(); // Should be better with a priority render call
			bNewWorld.get("Value Chunk Size").show();
			break;
		}
	}

	private void unShow(HashMap<String, Button> list, Hud hud) {
		for (Button b : list.values())
			b.unShow();
	}

	private void resetButtonWorld(Hud hud, MainLoxy game) {
		unShow(bWorld, hud);
		bWorld.clear();
		bWorld.put("Back", new Button("Back", hud, LEFT, BOTTOM));
		bWorld.get("Back").setHoveredLeftClickCallback(() -> {
			page = "Menu";
			firstRender = true;
			unShow(bWorld, hud);
		});
		bWorld.put("New", new Button("New", hud, LEFT, LINE0));
		bWorld.get("New").setHoveredLeftClickCallback(() -> {
			page = "New World";
			resetButtonNewWorld(hud, game);
			firstRender = true;
			unShow(bWorld, hud);
		});
		bWorld.put("Load", new Button("Load", hud, LEFT, LINE1));
		bWorld.get("Load").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Load").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Load";
		});
		bWorld.get("Load").setSelected(true);
		bWorld.put("Copy", new Button("Copy", hud, LEFT, LINE2));
		bWorld.get("Copy").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Copy").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Copy";
		});
		bWorld.put("Delete", new Button("Delete", hud, LEFT, LINE3));
		bWorld.get("Delete").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Delete").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Delete";
		});
		String[] saveListName = new File("save").list();
		for (int i = 0; i < min(saveListName.length, 5); i++) {
			bWorld.put(saveListName[i], new Button(saveListName[i], hud, MIDDLE, ButtonPositionY.getLine(i)));
			Button b = bWorld.get(saveListName[i]);
			b.setHoveredLeftClickCallback(() -> {
				if ("Load".equalsIgnoreCase(selectedActionWorld)) {
					game.setMap(UtilsMapIO.load(game.getPlayer(), b.getText()));
					page = "In Game";
					unShow(bWorld, hud);
					firstRender = true;
					Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
				} else if ("Copy".equalsIgnoreCase(selectedActionWorld)) {
					UtilsMapIO.copy(b.getText(), b.getText() + " - Copy");
					resetButtonWorld(hud, game);
				} else if ("Delete".equalsIgnoreCase(selectedActionWorld)) {
					UtilsMapIO.delete(b.getText());
					bWorld.get(b.getText()).unShow();
					bWorld.remove(b.getText());
				}
			});
		}
		for (Button b1 : bWorld.values()) {
			b1.setHoveredCallback(() -> {
				b1.getComponent().getTransformation().scaling(1.25f);
				b1.getComponent()
						.setPos(new Vector2f(b1.getPos().x, b1.getPos().y - b1.getComponent().getSize().y / 8));
			});
		}
	}

	private void resetButtonNewWorld(Hud hud, MainLoxy game) {
		unShow(bNewWorld, hud);
		bNewWorld.clear();
		bNewWorld.put("Back", new Button("Back", hud, LEFT, BOTTOM));
		bNewWorld.get("Back").setHoveredLeftClickCallback(() -> {
			page = "World";
			firstRender = true;
			unShow(bNewWorld, hud);
		});
		bNewWorld.put("World Name", new TextInput(hud, "Loxy World", MIDDLE, LINE0));
		bNewWorld.put("Create", new Button("Create", hud, LEFT, LINE0));
		bNewWorld.get("Create").setHoveredLeftClickCallback(() -> {
			Chunk.SIZE = Integer.parseInt(bNewWorld.get("Value Chunk Size").getText());
			game.setMap(new Map(bNewWorld.get("World Name").getText()));
			page = "In Game";
			unShow(bNewWorld, hud);
			firstRender = true;
			Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
		});
		bNewWorld.put("Chunk Size", new Button("Chunk Size", hud, MIDDLE, LINE1));
		bNewWorld.put("Value Chunk Size", new Button(Integer.toString(Settings.DEFAULT_CHUNK_SIZE), hud, RIGHT, LINE1));
		bNewWorld.get("Chunk Size").setHoveredLeftClickCallback(() -> {
			Settings.DEFAULT_CHUNK_SIZE++; // Not the good way to do this
			bNewWorld.get("Value Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE));
		});
		bNewWorld.get("Chunk Size").setHoveredRightClickCallback(() -> {
			if (Settings.DEFAULT_CHUNK_SIZE > 1) {
				Settings.DEFAULT_CHUNK_SIZE--;
				bNewWorld.get("Value Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE));
			}
		});
		for (Button b1 : bNewWorld.values()) {
			b1.setHoveredCallback(() -> {
				b1.getComponent().getTransformation().scaling(1.25f);
				b1.getComponent()
						.setPos(new Vector2f(b1.getPos().x, b1.getPos().y - b1.getComponent().getSize().y / 8));
			});
		}
	}

	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		firstRender = true;
		this.page = page;
	}
}
