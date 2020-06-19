package fr.mipiker.game.ui;

import static fr.mipiker.game.ui.ButtonPositionY.BOTTOM;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.*;

import fr.mipiker.isisEngine.*;

class Button {

	protected String text;
	protected HudTextComponent textComponent;
	protected ButtonHoveredLeftClickCallback onHoveredLeftClick;
	private ButtonHoveredRightClickCallback onHoveredRightClick;
	private ButtonAlignedLeftClickCallback onAlignedLeftClick;
	private ButtonAlignedRightClickCallback onAlignedRightClick;
	private ButtonMouseAlignedCallback onMouseAligned;
	private ButtonMouseHoveredCallback onMouseHovered;
	protected Vector2f pos;
	protected boolean selected;
	private Vector4f selectedColor;
	protected Hud hud;
	private ButtonPositionX posX;
	private ButtonPositionY posY;

	Button(String text, Hud hud, ButtonPositionX posX, ButtonPositionY posY) {
		this.posX = posX;
		this.posY = posY;
		this.text = text;
		this.hud = hud;
		pos = new Vector2f(-1000); // Better way to do this
		resetTextComponent();
	}

	void update(Input input, Vector2f windowSize, float margin, float fontHeight) {
		textComponent.getTransformation().scaling(1f);
		setPosition(windowSize, margin, fontHeight);
		if (input.getMousePosY() < windowSize.y - pos.y
				&& input.getMousePosY() > windowSize.y - pos.y - textComponent.getSize().y) {
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT) && onAlignedLeftClick != null)
				onAlignedLeftClick.onLeftClick();
			if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT) && onAlignedRightClick != null)
				onAlignedRightClick.onRightClick();
			if (onMouseAligned != null)
				onMouseAligned.onMouseAligned();
			if (input.getMousePosX() > pos.x && input.getMousePosX() < pos.x + textComponent.getSize().x) {
				if (onMouseHovered != null)
					onMouseHovered.onHovered();
				if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT) && onHoveredLeftClick != null)
					onHoveredLeftClick.onLeftClick();
				if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_RIGHT) && onHoveredRightClick != null)
					onHoveredRightClick.onRightClick();
			}
		}
		textComponent.setColor(selected ? selectedColor : new Vector4f(1));
	}

	protected void setPosition(Vector2f windowSize, float margin, float fontHeight) {
		switch (posX) {
		case LEFT:
			pos.x = margin;
			break;
		case MIDDLE:
			pos.x = windowSize.x / 2;
			break;
		case RIGHT:
			pos.x = windowSize.x - margin;
		}
		if (posY.name().contains("LINE"))
			pos.y = windowSize.y - margin - fontHeight * posY.getLine();
		else if (posY == BOTTOM)
			pos.y = margin;
		textComponent.setPos(pos);
	}

	void setHoveredLeftClickCallback(ButtonHoveredLeftClickCallback onHoveredLeftClick) {
		this.onHoveredLeftClick = onHoveredLeftClick;
	}
	void setHoveredRightClickCallback(ButtonHoveredRightClickCallback onHoveredRightClick) {
		this.onHoveredRightClick = onHoveredRightClick;
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

	void show() {
		hud.replaceComponent(textComponent);
	}
	void unShow() {
		hud.removeComponent(textComponent);
	}

	void resetTextComponent() {
		unShow();
		textComponent = new HudTextComponent(PageManager.font, text);
		textComponent.setPos(pos);
	}
	HudTextComponent getComponent() {
		return textComponent;
	}

	String getText() {
		return text;
	}
	void setText(String text) {
		this.text = text;
		resetTextComponent();
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
	void setSelectedColor(Vector4f color) {
		selectedColor = color;
	}

	ButtonPositionY getPosY() {
		return posY;
	}
}
