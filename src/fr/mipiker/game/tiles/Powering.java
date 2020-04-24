package fr.mipiker.game.tiles;


public interface Powering {
	
	public void setPower(boolean power);

	public boolean isPowered();
	
	public void addSourcePower(Powering tile);

	public void removeSourcePower(Powering tile);

	public void onTurningPowerOn();
	
	public void onTurningPowerOff();
}
