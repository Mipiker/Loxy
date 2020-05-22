package fr.mipiker.game.ui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import org.joml.Vector2f;
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

	Button(String text, Hud hud) {
		this.text = text;
		resetComponent(hud);
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
	void setHoveredCallBack(ButtonMouseHoveredCallback onMouseHovered) {
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

}
