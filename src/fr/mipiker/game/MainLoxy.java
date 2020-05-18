package fr.mipiker.game;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector3f;
import fr.mipiker.game.item.Item;
import fr.mipiker.game.tiles.Tile;
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

	@Override
	public void init(Window window, Input input, Engine engine) {
		this.window = window;
		this.engine = engine;
		tick = new TickManager();
		renderer = new Renderer(window);
		scene = renderer.getScene();
		camera = scene.getCamera();

		Tile.load();
		Item.loadItem();
		Settings.load();
		
		camera.setRotation(new Vector3f(90, 0, 0));
		camera.setPosition(new Vector3f(0, 20, 0));

		player = new Player(scene.getCamera(), renderer.getHud(), window);

		command = new Command(this, engine);
		command.init();
		command.prepareCommand("/map load adder");

	}

	@Override
	public void update(Input input) {
		boolean isTickUpdate = tick.update();
		player.update(input, map, window);
		if (engine.getNbUpdate() % nbMapUpdate == 0 && map != null)
			map.update(scene, player, isTickUpdate);
	}

	@Override
	public void render(Window window) {
		if (map != null)
			map.renderUpdate(scene);
		renderer.render(window);
	}

	@Override
	public void keyInput(int key, int action) {
		// Close the window
		if (key == GLFW_KEY_ESCAPE && action == GLFW_REPEAT)
			glfwSetWindowShouldClose(window.getID(), true);
		// FullScreen
		if (key == GLFW_KEY_F11 && action == GLFW_PRESS)
			window.setFullscreen(!window.isFullscreen());
		// FullScreen
		if (key == GLFW_KEY_F12 && action == GLFW_PRESS)
			System.out.println("[Info] Fps : " + window.getFps());
	}

	@Override
	public void terminate() {
		command.term();
	}

	public static void main(String[] args) {

		System.setProperty("org.lwjgl.librarypath", "lib\\all_natives");
		Window.initialize();
		new Engine(new Window("Isis", 720, 480, true), new MainLoxy()).run();
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
