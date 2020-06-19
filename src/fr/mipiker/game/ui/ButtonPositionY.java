package fr.mipiker.game.ui;

enum ButtonPositionY {

	LINE0(0), LINE1(1), LINE2(2), LINE3(3), LINE4(4), BOTTOM;

	private int line;

	private ButtonPositionY(int line) {
		this.line = line;
	}
	private ButtonPositionY() {
		this(-1);
	}

	int getLine() {
		return line;
	}
	static ButtonPositionY getLine(int line) {
		for (ButtonPositionY value : ButtonPositionY.values())
			if (value.line == line)
				return value;
		return null;
	}
}
