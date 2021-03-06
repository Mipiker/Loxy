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
	private SoundManager soundManager;

	@Override
	public void init(Window window, Input input, SoundManager soundManager, Engine engine) {
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
		this.soundManager = soundManager;

		camera.setRotation(new Vector3f(90, 0, 0));
		camera.setPosition(new Vector3f(0, 20, 0));

		player = new Player(scene.getCamera(), renderer.getHud(), soundManager, window);

		command = new Command(this, engine);

		setMap(UtilsMapIO.load(player, Settings.LAST_PLAYED_MAP_NAME));

		soundManager.setListener(new SoundListener(new Vector3f()));
		SoundBuffer bufferAction = new SoundBuffer("resources/sounds/action.wav");
		soundManager.addSoundBuffer(bufferAction);
		SoundSource action = new SoundSource(false, false);
		action.setBuffer(bufferAction.getBufferId());
		soundManager.addSoundSource("action", action);
		SoundBuffer bufferDelete = new SoundBuffer("resources/sounds/delete.wav");
		soundManager.addSoundBuffer(bufferDelete);
		SoundSource delete = new SoundSource(false, false);
		delete.setBuffer(bufferDelete.getBufferId());
		soundManager.addSoundSource("delete", delete);
		SoundBuffer bufferPlace = new SoundBuffer("resources/sounds/place.wav");
		soundManager.addSoundBuffer(bufferPlace);
		SoundSource place = new SoundSource(false, false);
		place.setBuffer(bufferPlace.getBufferId());
		soundManager.addSoundSource("place", place);
	}

	@Override
	public void update(Input input) {
		if (map == null)
			setMap(new Map());
		pageManager.update(input, player, this);
		boolean isTickUpdate = tick.update();
		if (!"In Game".equalsIgnoreCase(pageManager.getPage())) {
			scene.setBlur(true);
			if (engine.getNbUpdate() % nbMapUpdate == 0)
				map.update(scene, player, isTickUpdate);
		} else {
			scene.setBlur(false);
			player.update(input, map, window);
			if (engine.getNbUpdate() % nbMapUpdate == 0)
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				UtilsMapIO.save(map, player);
			}
		}).start();
		map.delete();
		Settings.save();
		soundManager.cleanup();
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
		if (map != null) {
			if (this.map != null)
				this.map.delete();
			scene.resetMeshes();
			this.map = map;
		}
	}
}
