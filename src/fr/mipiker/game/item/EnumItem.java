package fr.mipiker.game.item;


public enum EnumItem {

	WIRE((byte) 0, "wire.png"),
	POWER((byte) 1, "power.png"),
	AND_GATE((byte) 2, "and_gate.png"),
	OR_GATE((byte) 3, "or_gate.png"),
	XOR_GATE((byte) 4, "xor_gate.png"),
	INV_GATE((byte) 5, "inv_gate.png");
	
	private byte value;
	private String textureName;
	
	EnumItem(byte value, String textureName) {
		this.value = value;
		this.textureName = textureName;
	}

	
	public byte getValue() {
		return value;
	}
	public String getTextureName() {
		return textureName;
	}
	public String getTexturePath() {
		return "resources/items/" + textureName;
	}
}
