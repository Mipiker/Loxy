package fr.mipiker.game.tiles;

public enum EnumTiles {

						EMPTY((byte) 0, "empty.png"),
						WIRE((byte) 1, new String[] { "wire/0_off.png", "wire/0_on.png", "wire/1_off.png", "wire/1_on.png", "wire/2_0_off.png", "wire/2_0_on.png", "wire/2_1_off.png", "wire/2_1_on.png", "wire/3_off.png", "wire/3_on.png", "wire/4_0_off.png", "wire/4_0_on.png", "wire/4_1_off.png", "wire/4_1_on.png", "wire/4_2_off.png", "wire/4_2_on.png" }),
						POWER((byte) 2, new String[] { "power/power_off.png", "power/power_on.png" }),
						OR_GATE((byte) 3, "logic_gates/or.png"),
						XOR_GATE((byte) 4, "logic_gates/xor.png"),
						AND_GATE((byte) 5, "logic_gates/and.png"),
						INV_GATE((byte) 6, "logic_gates/inv.png");

	private byte tileValue;
	private String[] textureNames;
	private static final String TEXTURE_PATH = "resources/tiles/";

	EnumTiles(byte tileValue, String textureName) {
		this(tileValue, new String[] { textureName });
	}

	EnumTiles(byte tileValue, String[] textureNames) {
		this.tileValue = tileValue;
		this.textureNames = textureNames;
	}

	public byte getTileValue() {
		return tileValue;
	}

	public String getDefaultTexturePath() {
		return TEXTURE_PATH + textureNames[0];
	}

	public String[] getTexturesPath() {
		String[] texturesPath = new String[textureNames.length];
		for (int i = 0; i < texturesPath.length; i++)
			texturesPath[i] = TEXTURE_PATH + textureNames[i];
		return texturesPath;
	}

	public boolean hasMultipleTexture() {
		return textureNames.length != 1;
	}
}
