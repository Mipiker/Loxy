package fr.mipiker.game.ui;

import fr.mipiker.game.item.Item;
import fr.mipiker.isisEngine.*;

public class Slot {

	private static final Texture TEXTURE = new Texture("resources/hud/slot2b.png");
	public static final int SIZE = 50;
	private Item item;
	private HudComponent componentSlot, componentItem;

	public Slot() {
		this(null);
	}

	public Slot(Item item) {
		this.item = item;
		componentSlot = new HudComponent(TEXTURE).setWidth(SIZE).setHeight(SIZE);
		if (item != null)
			componentItem = new HudComponent(item.getTexture()).setWidth(SIZE).setHeight(SIZE);
	}

	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		if (this.item == null)
			componentItem = new HudComponent(item.getTexture()).setWidth(SIZE).setHeight(SIZE);
		else
			componentItem.getMaterial().setTexture(item.getTexture());
		this.item = item;
	}

	public HudComponent getComponentSlot() {
		return componentSlot;
	}

	public HudComponent getComponentItem() {
		return componentItem;
	}

	public boolean hasItem() {
		return item != null;
	}
}
