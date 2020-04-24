package fr.mipiker.game;

import java.io.*;
import fr.mipiker.isisEngine.Window;

public class Command implements Runnable {

	private Window window;
	private Player player;
	private TickManager tick;
	private volatile boolean stopThread;

	public Command(Window window, Player player, TickManager tick) {
		this.window = window;
		this.player = player;
		this.tick = tick;
	}

	private void execute(String line) {
		if (line.charAt(0) == '/') {
			System.out.println("command");
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
					Thread.sleep(200);
				}
				if (!stopThread) {
					input = br.readLine();
					if (input.length() > 0)
						execute(input);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		} while ("".equals(input));
	}

	public void init() {
		new Thread(this, "Command").start();
	}

	public void term() {
		stopThread = true;
	}

}
