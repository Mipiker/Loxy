package fr.mipiker.game.ui;

import fr.mipiker.game.item.Item;
import fr.mipiker.isisEngine.Hud;

public class SlotBar {

	private Slot[] slots;
	private int selectedSlot;

	public SlotBar(int size) {
		slots = new Slot[size];
		for (int i = 0; i < size; i++)
			slots[i] = new Slot();
	}
	public SlotBar(Slot[] slots) {
		setSlot(slots);
	}
	
	public void show(Hud hud) {
		for (Slot slot : getSlots()) {
			hud.replaceComponent(slot.getComponentSlot());
			if (slot.hasItem())
				hud.replaceComponent(slot.getComponentItem());
		}
	}
	public void unShow(Hud hud) {
		for (Slot slot : getSlots()) {
			hud.removeComponent(slot.getComponentSlot());
			if (slot.hasItem())
				hud.removeComponent(slot.getComponentItem());
		}
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
		for(Slot slot : slots) {
			if(slot != null && slot.getItem() == null) {
				slot.setItem(item);
				return this;
			}
		}
		return this;
	}
}
