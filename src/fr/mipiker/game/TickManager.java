package fr.mipiker.game;

public class TickManager {

	private int nbTicksSecond = 1;
	private long lastTime;
	private int a;

	public TickManager() {
		lastTime = System.currentTimeMillis();
	}

	/**
	 * @return if this update is a tick upadate
	 */
	public boolean update() {
		long actualTime = System.currentTimeMillis();

		a += actualTime - lastTime;

		if (a >= nbTicksSecond * 1000) {
			a -= nbTicksSecond * 1000;
			lastTime = System.currentTimeMillis();
			return true;
		}
		lastTime = System.currentTimeMillis();
		return false;
	}

}
