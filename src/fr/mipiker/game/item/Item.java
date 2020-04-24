package fr.mipiker.game.item;

import java.util.HashMap;
import fr.mipiker.isisEngine.Texture;

public abstract class Item {

	private Texture texture;
	protected static HashMap<EnumItem, Texture> itemtexture;
	public final EnumItem ITEM_TYPE;

	public Item(EnumItem item) {
		ITEM_TYPE = item;
		this.texture = itemtexture.get(item);
	}

	public static void loadItem() {
		if (itemtexture == null) {
			itemtexture = new HashMap<>();
			for (EnumItem item : EnumItem.values())
				itemtexture.put(item, new Texture(item.getTexturePath()));
		} else {
			System.out.println("[INFO] Item texture already loaded");
		}
	}

	public Texture getTexture() {
		return texture;
	}

}
