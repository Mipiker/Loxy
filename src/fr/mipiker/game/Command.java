package fr.mipiker.game;

import java.io.*;
import fr.mipiker.isisEngine.*;

public class Command implements Runnable {

	private MainLoxy game;
	private volatile boolean stopThread;

	public Command(MainLoxy game, Engine engine) {
		this.game = game;
	}

	private void execute(String line) {
		if (line.charAt(0) == '/') {
			line = line.substring(1);
			String[] words = line.split(" ");
			String[] args = new String[words.length - 1];
			for (int i = 1; i < words.length; i++)
				args[i - 1] = words[i];
			commandPassed(words[0], args);
		}
	}

	private void commandPassed(String command, String[] args) {
		System.out.println("execute command " + command);
		for (String a : args) {
			System.out.println("args : " + a);
		}
		switch (command) {
		case "command":
			if (args.length == 1 && args[0] == "stop")
				stopThread = true;
			break;
		case "map":
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("update")) {
					if(args[1].equalsIgnoreCase("next")) {
						game.getMap().update(game.getScene(), game.getPlayer(), false);
					}
					try {
						game.setMapUpdate(Integer.parseInt(args[1]));
					} catch (NumberFormatException e) {
					}
				}
			}
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
						execute(input);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		} while (!stopThread);
		System.out.println("[Info] Stop command");
	}

	public void init() {
		new Thread(this, "Command").start();
	}

	public void term() {
		stopThread = true;
	}

}
