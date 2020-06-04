package fr.mipiker.game;

import static fr.mipiker.game.Settings.*;

import java.io.*;

import fr.mipiker.game.utils.UtilsMapIO;
import fr.mipiker.isisEngine.Engine;

public class Command implements Runnable {

	private MainLoxy game;
	private volatile boolean stopThread;

	public Command(MainLoxy game, Engine engine) {
		this.game = game;
		init();
	}

	public void prepareCommand(String line) {
		if (line.charAt(0) == '/') {
			line = line.substring(1);
			String[] words = line.split(" ");
			String[] args = new String[words.length - 1];
			for (int i = 1; i < words.length; i++)
				args[i - 1] = words[i];
			executeCommand(words[0], args);
		}
	}

	private void executeCommand(String command, String[] args) {
		switch (command) {
		case "map":
			if (args.length >= 1 || args.length <= 3) {

				if ("save".equalsIgnoreCase(args[0])) {
					if (args.length == 2)
						game.getMap().setName(args[1]);
					if (UtilsMapIO.save(game.getMap(), game.getPlayer()))
						System.out.println("[Info] Map " + game.getMap().getName() + " successfully saved.");
					else
						System.out.println("[Error] Couldn't save map " + game.getMap().getName()
								+ ". Maybe the syntax of the given name contains unwanted charaters.");
				}

				else if ("create".equalsIgnoreCase(args[0])) {
					game.setMap(new Map(args[1]));
					Chunk.SIZE = DEFAULT_CHUNK_SIZE;
					if (args.length == 3)
						try {
							Chunk.SIZE = Integer.parseInt(args[2]);
						} catch (NumberFormatException e) {
							System.out.println("[Error] " + args[2] + "is not an Integer");
						}
				}

				if (args.length == 2) {

					if ("update".equalsIgnoreCase(args[0])) {
						try {
							game.setMapUpdate(Integer.parseInt(args[1]));
						} catch (NumberFormatException e) {
							System.out.println("[Error] " + args[1] + " is not an integer.");
						}

					} else if ("load".equalsIgnoreCase(args[0])) {
						Map map = UtilsMapIO.load(game.getPlayer(), args[1]);
						if (map != null) {
							game.setMap(map);
							System.out.println("[Info] Map " + args[1] + " successfully loaded.");
						} else
							System.out.println("[Error] Couldn't load map " + args[1]
									+ ". Maybe the given name doesn't correspond to any saved map.");

					} else if ("delete".equalsIgnoreCase(args[0])) {
						if (UtilsMapIO.delete(args[1]))
							System.out.println("[Info] Map " + args[1] + " successfully deleted.");
						else
							System.out.println("[Error] Couldn't delete map " + args[1]
									+ ". Maybe the given name doesn't correspond to any saved map.");

					} else if ("rename".equalsIgnoreCase(args[0]))
						game.getMap().setName(args[1]);
				}
			}
			break;
		case "render_distance":
			if (args.length == 1) {
				try {
					RENDER_DISTANCE = Integer.parseInt(args[0]);
					save();
				} catch (NumberFormatException e) {
					System.out.println("[Error] " + args[0] + " is not an integer.");
				}
			}
			break;
		default:
			System.out.println("[Error] Unknown command.");
		}
	}

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		do {
			try {
				// wait until we have data to complete a readLine()
				while (!br.ready() && !stopThread) {
					Thread.sleep(100);
				}
				if (!stopThread) {
					input = br.readLine();
					if (input.length() > 0)
						prepareCommand(input);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		} while (!stopThread);
	}

	public void init() {
		new Thread(this, "Command").start();
	}

	public void term() {
		stopThread = true;
	}

}
