package fr.mipiker.game;

import java.io.*;
import fr.mipiker.game.utils.UtilsSave;
import fr.mipiker.isisEngine.*;

public class Command implements Runnable {

	private MainLoxy game;
	private volatile boolean stopThread;

	public Command(MainLoxy game, Engine engine) {
		this.game = game;
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
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("update")) {
					try {
						game.setMapUpdate(Integer.parseInt(args[1]));
					} catch (NumberFormatException e) {
					}
				}
			}
			break;
		case "save":
			if (args.length == 1) {
				if (UtilsSave.save(game.getMap(), game.getPlayer(), args[0]))
					System.out.println("[Info] Map " + args[0] + " successfully saved.");
				else
					System.out.println("[Error] Couldn't save map " + args[0] + ". Maybe the syntax of the given name contains unwanted charaters");
			}
			break;
		case "load":
			if (args.length == 1) {
				Map map = UtilsSave.load(game.getPlayer(), args[0]);
				if (map != null) {
					game.setMap(map);
					System.out.println("[Info] Map " + args[0] + " successfully loaded.");
				} else
					System.out.println("[Error] Couldn't load map " + args[0] + ". Maybe the given name doesn't correspond to any saved map.");
			}
			break;
		case "delete":
			if (args.length == 1) {
				if (UtilsSave.delete(args[0]))
					System.out.println("[Info] Map " + args[0] + " successfully deleted.");
				else
					System.out.println("[Error] Couldn't delete map " + args[0] + ". Maybe the given name doesn't correspond to any saved map.");
			}
			break;
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
