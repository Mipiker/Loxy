package fr.mipiker.game.tiles;

public enum EnumCardinalPoint {

	NORTH(0),EST(1),SOUTH(2),WEST(3);

	private int value;

	EnumCardinalPoint(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static EnumCardinalPoint getOrientation(int value) {
		for (EnumCardinalPoint e : values())
			if (e.value == value % 4)
				return e;
		return null;
	}
}
