package fr.mipiker.game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.io.File;
import java.util.HashMap;

import org.joml.*;
import org.lwjgl.glfw.GLFW;

import fr.mipiker.game.*;
import fr.mipiker.game.utils.UtilsMapIO;
import fr.mipiker.isisEngine.*;
import fr.mipiker.isisEngine.loader.FontLoader;

public class PageManager {

	static TexturedFont font;
	private static String fontPath = "resources/Optimus.otf";

	private float margin;
	private int height;
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
		bMenu.put("Continue", new Button("Continue", hud));
		bMenu.get("Continue").setHoveredLeftClickCallback(() -> {
			page = "In Game";
			Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
			unShow(bMenu, hud);
			firstRender = true;
		});
		bMenu.put("World", new Button("World", hud));
		bMenu.get("World").setHoveredLeftClickCallback(() -> {
			page = "World";
			unShow(bMenu, hud);
			firstRender = true;
			resetButtonWorld(hud, game);
		});
		bMenu.put("Options", new Button("Options", hud));
		bMenu.get("Options").setHoveredLeftClickCallback(() -> {
			page = "Options";
			unShow(bMenu, hud);
			firstRender = true;
		});
		bMenu.put("Quit Game", new Button("Quit Game", hud));
		bMenu.get("Quit Game").setHoveredLeftClickCallback(() -> GLFW.glfwSetWindowShouldClose(window.getID(), true));
		for (Button b : bMenu.values()) {
			b.setHoveredCallback(() -> {
				b.getComponent().getTransformation().scaling(1.25f);
				b.getComponent().setPos(new Vector2f(b.getPos().x, b.getPos().y - b.getComponent().getSize().y / 8));
			});
		}
		// Option
		bOptions.put("Render Distance", new Button("Render Distance", hud));
		bOptions.put("Value Render Distance", new Button(Integer.toString(Settings.RENDER_DISTANCE), hud));
		bOptions.put("Default Chunk Size", new Button("Default Chunk Size", hud));
		bOptions.put("Value Default Chunk Size", new Button(Integer.toString(Settings.DEFAULT_CHUNK_SIZE), hud));
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
		bOptions.get("Value Default Chunk Size").setAlignedLeftClickCallback(() -> {
			Settings.DEFAULT_CHUNK_SIZE++;
			bOptions.get("Value Default Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE));
		});
		bOptions.get("Value Default Chunk Size").setAlignedRightClickCallback(() -> {
			if (Settings.DEFAULT_CHUNK_SIZE > 1) {
				Settings.DEFAULT_CHUNK_SIZE--;
				bOptions.get("Value Default Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE));
			}
		});
		Button b = new Button("Back", hud);
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

	public void update(Input input, Player player, Hud hud) {
		Vector2f windowSize = new Vector2f(window.getSize());

		margin = windowSize.y * 0.25f;
		if (font.getSize() != null)
			height = font.getSize().y;

		switch (page) {
		case "Menu":
			bMenu.get("Continue").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height));
			bMenu.get("World").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 2));
			bMenu.get("Options").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 3));
			bMenu.get("Quit Game").update(input, windowSize, new Vector2f(margin));
			break;
		case "Options":
			bOptions.get("Render Distance").update(input, windowSize,
					new Vector2f(margin, windowSize.y - margin - height));
			bOptions.get("Value Render Distance").update(input, windowSize,
					new Vector2f(windowSize.x - margin, windowSize.y - margin - height));
			bOptions.get("Default Chunk Size").update(input, windowSize,
					new Vector2f(margin, windowSize.y - margin - height * 2));
			bOptions.get("Value Default Chunk Size").update(input, windowSize,
					new Vector2f(windowSize.x - margin, windowSize.y - margin - height * 2));
			bOptions.get("Back").update(input, windowSize, new Vector2f(margin));
			break;
		case "In Game":
			if (input.isLastKeyPress(GLFW_KEY_ESCAPE)) {
				page = "Menu";
				firstRender = true;
				player.getSlotBar().unShow(hud);
			}
			break;
		case "World":
			for (Button b : bWorld.values()) {
				if (b.getText().equalsIgnoreCase(selectedActionWorld))
					b.setSelected(true);
				else
					b.setSelected(false);
			}
			bWorld.get("New").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height));
			bWorld.get("Load").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 2));
			bWorld.get("Copy").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 3));
			bWorld.get("Delete").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 4));
			bWorld.get("Back").update(input, windowSize, new Vector2f(margin));
			int i = 0;
			for (String s : new File("save").list()) {
				i++;
				bWorld.get(s).update(input, windowSize,
						new Vector2f(windowSize.x / 2, windowSize.y - margin - height * i));
			}
			break;
		case "New World":
			bNewWorld.get("Create").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height));
			bNewWorld.get("Back").update(input, windowSize, new Vector2f(margin));
			bNewWorld.get("World Name").update(input, windowSize,
					new Vector2f(windowSize.x / 2, windowSize.y - margin - height));
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
				player.getSlotBar().show(hud);
				player.resetPosSlotBar(window);
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
			for (Button b : bNewWorld.values())
				b.show();
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
		bWorld.put("Back", new Button("Back", hud));
		bWorld.get("Back").setHoveredLeftClickCallback(() -> {
			page = "Menu";
			firstRender = true;
			unShow(bWorld, hud);
		});
		bWorld.put("New", new Button("New", hud));
		bWorld.get("New").setHoveredLeftClickCallback(() -> {
			page = "New World";
			resetButtonNewWorld(hud, game);
			firstRender = true;
			unShow(bWorld, hud);
		});
		bWorld.put("Load", new Button("Load", hud));
		bWorld.get("Load").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Load").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Load";
		});
		bWorld.get("Load").setSelected(true);
		bWorld.put("Copy", new Button("Copy", hud));
		bWorld.get("Copy").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Copy").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Copy";
		});
		bWorld.put("Delete", new Button("Delete", hud));
		bWorld.get("Delete").setSelectedColor(new Vector4f(0.4f, 0.69f, .19f, 1));
		bWorld.get("Delete").setHoveredLeftClickCallback(() -> {
			selectedActionWorld = "Delete";
		});
		for (String s : new File("save").list()) {
			bWorld.put(s, new Button(s, hud));
			bWorld.get(s).setHoveredLeftClickCallback(() -> {
				if ("Load".equalsIgnoreCase(selectedActionWorld)) {
					game.setMap(UtilsMapIO.load(game.getPlayer(), s));
					page = "In Game";
					unShow(bWorld, hud);
					firstRender = true;
					Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
				} else if ("Copy".equalsIgnoreCase(selectedActionWorld)) {
					UtilsMapIO.copy(s, s + " - Copy");
					resetButtonWorld(hud, game);
				} else if ("Delete".equalsIgnoreCase(selectedActionWorld)) {
					UtilsMapIO.delete(s);
					bWorld.get(s).unShow();
					bWorld.remove(s);
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
		bNewWorld.put("Back", new Button("Back", hud));
		bNewWorld.get("Back").setHoveredLeftClickCallback(() -> {
			page = "World";
			firstRender = true;
			unShow(bNewWorld, hud);
		});
		bNewWorld.put("World Name", new TextInput(hud, "Loxy World"));
		bNewWorld.put("Create", new Button("Create", hud));
		bNewWorld.get("Create").setHoveredLeftClickCallback(() -> {
			game.setMap(new Map(bNewWorld.get("World Name").getText()));
			page = "In Game";
			unShow(bNewWorld, hud);
			firstRender = true;
			Settings.LAST_PLAYED_MAP_NAME = game.getMap().getName();
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
