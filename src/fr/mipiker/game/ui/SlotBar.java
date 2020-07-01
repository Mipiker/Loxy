package fr.mipiker.game.ui;

import fr.mipiker.game.item.Item;
import fr.mipiker.isisEngine.Hud;

public class SlotBar {

	private Slot[] slots;
	private int selectedSlot;
	private Hud hud;

	public SlotBar(int size, Hud hud) {
		slots = new Slot[size];
		for (int i = 0; i < size; i++)
			slots[i] = new Slot();
		this.hud = hud;
	}
	public SlotBar(Slot[] slots, Hud hud) {
		setSlot(slots);
		this.hud = hud;
	}

	public void show() {
		for (Slot slot : getSlots()) {
			hud.replaceComponent(slot.getComponentSlot());
			if (slot.hasItem())
				hud.replaceComponent(slot.getComponentItem());
		}
	}
	public void unShow() {
		for (Slot slot : getSlots()) {
			hud.removeComponent(slot.getComponentSlot());
			if (slot.hasItem())
				hud.removeComponent(slot.getComponentItem());
		}
	}

	public void resetPos(int windowWidth) {
		int spacebtw = 10;
		float posX = (windowWidth / 2f) - (((Slot.SIZE + spacebtw) * slots.length) / 2f);
		for (int i = 0; i < slots.length; i++) {
			Slot slot = slots[i];
			slot.getComponentSlot().getTransformation().setTranslation(posX, spacebtw, 0);
			if (slot.hasItem())
				slot.getComponentItem().getTransformation().setTranslation(posX, spacebtw, 0.2f);
			posX += Slot.SIZE + spacebtw;
		}
		slots[selectedSlot].getComponentSlot().getTransformation().translate(0, Slot.SIZE, 0);
		if (slots[selectedSlot].hasItem())
			slots[selectedSlot].getComponentItem().getTransformation().translate(0, Slot.SIZE, 0);
	}

	public Slot getSelectedSlot() {
		return slots[selectedSlot];
	}
	public int getPosSelectedSlot() {
		return selectedSlot;
	}

	public void selectSlot(int slot) {
		selectedSlot = slot;
	}
	public void moveSelectSlot(int offset) {
		selectedSlot += offset;
		if (selectedSlot >= slots.length)
			selectedSlot = 0;
		else if (selectedSlot < 0)
			selectedSlot = slots.length - 1;
	}

	public void setSlot(int slotPos, Slot slot) {
		this.slots[slotPos] = slot;
	}
	public void setSlot(Slot[] slots) {
		this.slots = slots;
		if (selectedSlot >= slots.length)
			selectedSlot = slots.length - 1;
	}

	public Slot[] getSlots() {
		return slots;
	}
	public Slot getSlot(int i) {
		return slots[i];
	}

	public int getSize() {
		return slots.length;
	}

	public SlotBar addItem(Item item) {
		for (Slot slot : slots) {
			if (slot != null && slot.getItem() == null) {
				slot.setItem(item);
				return this;
			}
		}
		return this;
	}
}
