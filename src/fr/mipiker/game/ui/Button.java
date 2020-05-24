package fr.mipiker.game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import org.joml.Vector2f;
import org.joml.Vector4f;
import fr.mipiker.isisEngine.Hud;
import fr.mipiker.isisEngine.HudTextComponent;
import fr.mipiker.isisEngine.Input;

class Button {

	private String text;
	private HudTextComponent component;
	private ButtonHoveredLeftClickCallback onHoveredLeftClick;
	private ButtonAlignedLeftClickCallback onAlignedLeftClick;
	private ButtonAlignedRightClickCallback onAlignedRightClick;
	private ButtonMouseAlignedCallback onMouseAligned;
	private ButtonMouseHoveredCallback onMouseHovered;
	private Vector2f pos;
	private boolean selected;

	Button(String text, Hud hud) {
		pos = new Vector2f(0);
		this.text = text;
		resetComponent(hud);
	}
	Button(Button b) {
		text = b.text;
		component = new HudTextComponent(b.component);
		onHoveredLeftClick = b.onHoveredLeftClick;
		onAlignedLeftClick = b.onAlignedLeftClick;
		onAlignedRightClick = b.onAlignedRightClick;
		onMouseAligned = b.onMouseAligned;
		onMouseHovered = b.onMouseHovered;
		pos = new Vector2f(b.pos);
	}

	void update(Input input, Vector2f windowSize, Vector2f pos) {
		this.pos = pos;
		component.getTransformation().scaling(1f);
		component.setPos(new Vector2f(pos));
		if (input.getMousePosY() < windowSize.y - pos.y && input.getMousePosY() > windowSize.y - pos.y - component.getSize().y) {
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT) && onAlignedLeftClick != null)
				onAlignedLeftClick.onLeftClick();
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT) && onAlignedRightClick != null)
				onAlignedRightClick.onRightClick();
			if (onMouseAligned != null)
				onMouseAligned.onMouseAligned();
			if (input.getMousePosX() > pos.x && input.getMousePosX() < pos.x + component.getSize().x) {
				if (onMouseHovered != null)
					onMouseHovered.onHovered();
				if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT) && onHoveredLeftClick != null)
					onHoveredLeftClick.onLeftClick();
			}
		}
			component.setColor(selected ? new Vector4f(0.4f, 0.69f, .19f, 1) : new Vector4f(1));
	}

	void setHoveredLeftClickCallback(ButtonHoveredLeftClickCallback onHoveredLeftClick) {
		this.onHoveredLeftClick = onHoveredLeftClick;
	}
	void setAlignedLeftClickCallback(ButtonAlignedLeftClickCallback onAlignedLeftClick) {
		this.onAlignedLeftClick = onAlignedLeftClick;
	}
	void setAlignedRightClickCallback(ButtonAlignedRightClickCallback onAlignedRightClick) {
		this.onAlignedRightClick = onAlignedRightClick;
	}
	void setMouseAlignedCallback(ButtonMouseAlignedCallback onMouseAligned) {
		this.onMouseAligned = onMouseAligned;
	}
	void setHoveredCallback(ButtonMouseHoveredCallback onMouseHovered) {
		this.onMouseHovered = onMouseHovered;
	}

	void show(Hud hud) {
		hud.replaceComponent(component);
	}
	void unShow(Hud hud) {
		hud.removeComponent(component);
	}

	void resetComponent(Hud hud) {
		unShow(hud);
		component = new HudTextComponent(PageManager.font, text);
	}
	HudTextComponent getComponent() {
		return component;
	}

	String getText() {
		return text;
	}
	void setText(String text, Hud hud) {
		this.text = text;
		resetComponent(hud);
	}

	Vector2f getPos() {
		return pos;
	}
	void setPos(Vector2f pos) {
		this.pos = pos;
	}

	void setSelected(boolean selected) {
		this.selected = selected;
	}
}
