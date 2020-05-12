package fr.mipiker.game.tiles;

public interface Powering {

	public void setPower(boolean power);

	public boolean isPowered();

	/**
	 * Some tiles should not get power because this tile take power from it's inputs and reject it to
	 * it's output.</br>
	 * In ohher words, the power circulate in one way (like {@link Switch} or {@link Gate}).
	 * 
	 * @param e
	 *            Where is the tile who ask from the tile who answer
	 * @return If the tile who ask get power from the tile who answer
	 */
	public boolean isPowered(EnumCardinalPoint e);
}
