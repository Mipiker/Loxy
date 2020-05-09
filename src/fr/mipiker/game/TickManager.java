package fr.mipiker.game;

public class TickManager {

	private int nbTicksSecond = 1;
	private long lastTime;
	private int rest;

	public TickManager() {
		lastTime = System.currentTimeMillis();
	}

	/**
	 * @return if this update is a tick upadate
	 */
	public boolean update() {
		long actualTime = System.currentTimeMillis();

		rest += actualTime - lastTime;

		if (rest >= nbTicksSecond * 1000) {
			rest -= nbTicksSecond * 1000;
			lastTime = System.currentTimeMillis();
			return true;
		}
		lastTime = System.currentTimeMillis();
		return false;
	}

}
