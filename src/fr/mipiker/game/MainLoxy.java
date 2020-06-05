package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.*;

import fr.mipiker.game.item.Item;
import fr.mipiker.game.tiles.Tile;
import fr.mipiker.game.ui.PageManager;
import fr.mipiker.game.utils.UtilsMapIO;
import fr.mipiker.isisEngine.*;

public class MainLoxy implements IGame {

	private Renderer renderer;
	private Scene scene;
	private Camera camera;
	private Window window;
	private Map map;
	private Player player;
	private Command command;
	private TickManager tick;
	private Engine engine;
	private int nbMapUpdate = 1;
	private PageManager pageManager;

	@Override
	public void init(Window window, Input input, Engine engine) {
		Tile.load();
		Item.loadItem();
		Settings.load();

		this.window = window;
		this.engine = engine;
		tick = new TickManager();
		renderer = new Renderer(window);
		scene = renderer.getScene();
		camera = scene.getCamera();
		pageManager = new PageManager(renderer.getHud(), window, this);

		camera.setRotation(new Vector3f(90, 0, 0));
		camera.setPosition(new Vector3f(0, 20, 0));

		player = new Player(scene.getCamera(), renderer.getHud(), window);

		command = new Command(this, engine);

		setMap(UtilsMapIO.load(player, Settings.LAST_PLAYED_MAP_NAME));
	}

	@Override
	public void update(Input input) {
		pageManager.update(input, player, renderer.getHud());
		boolean isTickUpdate = tick.update();
		if (!"In Game".equalsIgnoreCase(pageManager.getPage())) {
			scene.setBlur(true);
			if (engine.getNbUpdate() % nbMapUpdate == 0 && map != null)
				map.update(scene, player, isTickUpdate);
		} else {
			scene.setBlur(false);
			player.update(input, map, window);
			if (engine.getNbUpdate() % nbMapUpdate == 0 && map != null)
				map.update(scene, player, isTickUpdate);
		}
	}

	@Override
	public void render(Window window) {
		if (map != null)
			map.renderUpdate(scene);
		pageManager.render(renderer.getHud(), player);
		renderer.render(window);
	}

	@Override
	public void keyInput(int key, int action) {
		// FullScreen
		if (key == GLFW_KEY_F11 && action == GLFW_PRESS)
			window.setFullscreen(!window.isFullscreen());
		// FPS
		if (key == GLFW_KEY_F12 && action == GLFW_PRESS)
			System.out.println("[Info] Fps : " + window.getFps());
	}

	@Override
	public void terminate() {
		command.term();
		Settings.save();
	}

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", "lib\\all_natives");
		Window.initialize();
		new Engine(new Window("Isis", new Vector2i(720, 480), true), new MainLoxy()).run();
		Window.terminate();
	}

	public void setMapUpdate(int factor) {
		this.nbMapUpdate = factor;
	}

	public Scene getScene() {
		return scene;
	}

	public Player getPlayer() {
		return player;
	}

	public Map getMap() {
		return map;
	}
	public void setMap(Map map) {
		scene.resetMeshes();
		this.map = map;
	}
}
