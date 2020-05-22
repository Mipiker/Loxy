package fr.mipiker.game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import java.util.HashMap;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import fr.mipiker.game.Player;
import fr.mipiker.game.Settings;
import fr.mipiker.isisEngine.Hud;
import fr.mipiker.isisEngine.Input;
import fr.mipiker.isisEngine.TexturedFont;
import fr.mipiker.isisEngine.Window;
import fr.mipiker.isisEngine.loader.FontLoader;

public class PageManager {

	static TexturedFont font;
	private static String fontPath = "resources/Optimus.otf";
	
	private float margin;
	private String page = "Menu";
	private Window window;
	private HashMap<String, Button> bMenu = new HashMap<>();
	private HashMap<String, Button> bOptions = new HashMap<>();
	private boolean firstRender;

	public PageManager(Hud hud, Window window) {
		this.window = window;
		font = FontLoader.loadFont(fontPath, window.getSize().y / 20);

		// Menu
		bMenu.put("Continue", new Button("Continue", hud));
		bMenu.get("Continue").setHoveredLeftClickCallback(() -> {
			page = "In Game";
			unShow(bMenu, hud);
			firstRender = true;
		});
		bMenu.put("Load", new Button("Load", hud));
		bMenu.get("Load").setHoveredLeftClickCallback(() -> {
			page = "Load Game";
			unShow(bMenu, hud);
			firstRender = true;
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
			b.setHoveredCallBack(() -> {
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
			bOptions.get("Value Render Distance").setText(Integer.toString(Settings.RENDER_DISTANCE), hud);
		});
		bOptions.get("Value Render Distance").setAlignedRightClickCallback(() -> {
			if (Settings.RENDER_DISTANCE > 1) {
				Settings.RENDER_DISTANCE--;
				bOptions.get("Value Render Distance").setText(Integer.toString(Settings.RENDER_DISTANCE), hud);
			}
		});
		bOptions.get("Value Default Chunk Size").setAlignedLeftClickCallback(() -> {
			Settings.DEFAULT_CHUNK_SIZE++;
			bOptions.get("Value Default Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE), hud);
		});
		bOptions.get("Value Default Chunk Size").setAlignedRightClickCallback(() -> {
			if (Settings.DEFAULT_CHUNK_SIZE > 1) {
				Settings.DEFAULT_CHUNK_SIZE--;
				bOptions.get("Value Default Chunk Size").setText(Integer.toString(Settings.DEFAULT_CHUNK_SIZE), hud);
			}
		});
		bOptions.put("Back", new Button("Back", hud));
		Button b = bOptions.get("Back");
		b.setHoveredCallBack(() -> {
			b.getComponent().getTransformation().scaling(1.25f);
			b.getComponent().setPos(new Vector2f(b.getPos().x, b.getPos().y - b.getComponent().getSize().y / 8));
		});
		b.setHoveredLeftClickCallback(() -> {
			page = "Menu";
			Settings.save();
			firstRender = true;
			unShow(bOptions, hud);
		});
	}

	public void update(Input input, Player player, Hud hud) {
		Vector2f windowSize = new Vector2f(window.getSize());

		margin = windowSize.y * 0.25f;
		int height = font.getSize().y;

		switch (page) {
		case "Menu":
			bMenu.get("Continue").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height));
			bMenu.get("Load").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 2));
			bMenu.get("Options").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 3));
			bMenu.get("Quit Game").update(input, windowSize, new Vector2f(margin));
			break;
		case "Options":
			bOptions.get("Render Distance").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height));
			bOptions.get("Value Render Distance").update(input, windowSize, new Vector2f(windowSize.x - margin, windowSize.y - margin - height));
			bOptions.get("Default Chunk Size").update(input, windowSize, new Vector2f(margin, windowSize.y - margin - height * 2));
			bOptions.get("Value Default Chunk Size").update(input, windowSize, new Vector2f(windowSize.x - margin, windowSize.y - margin - height * 2));
			bOptions.get("Back").update(input, windowSize, new Vector2f(margin));
			break;
		case "In Game":
			if(input.isLastKeyPress(GLFW_KEY_ESCAPE)) {
				page = "Menu";
				firstRender = true;
				player.getSlotBar().unShow(hud);
			}
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
					b.resetComponent(hud);
					firstRender = false;
				}
				for (Button b : bMenu.values())
					b.show(hud);
			}
			break;
		case "In Game":
			if (firstRender) {
				player.getSlotBar().show(hud);
				firstRender = false;
			}
			break;
		case "Options":
			if (firstRender) {
				for (Button b : bOptions.values()) {
					b.resetComponent(hud);
					firstRender = false;
				}
			}
			for (Button b : bOptions.values())
				b.show(hud);
			break;
		}
	}
	private void unShow(HashMap<String, Button> list, Hud hud) {
		for (Button b : list.values())
			b.unShow(hud);
	}

	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		firstRender = true;
		this.page = page;
	}
}
