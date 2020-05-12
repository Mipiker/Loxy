package fr.mipiker.game.tiles;

public enum EnumCardinalPoint {

	NORTH(0),EAST(1),SOUTH(2),WEST(3);

	private int value;

	EnumCardinalPoint(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public EnumCardinalPoint getOpposite() {
		return getOrientation(value + 2);
	}
	public EnumCardinalPoint getClockwise() {
		return getOrientation(value + 1);
	}
	public EnumCardinalPoint getAntiClockwise() {
		return getOrientation(value + 3);
	}

	public static EnumCardinalPoint getOrientation(int value) {
		for (EnumCardinalPoint e : values())
			if (e.value == value % 4)
				return e;
		return null;
	}
}
