package fr.mipiker.game.ui;

import static fr.mipiker.game.ui.PageManager.font;
import static org.lwjgl.glfw.GLFW.*;

import org.joml.*;

import fr.mipiker.isisEngine.*;

class TextInput extends Button {

	private int width;
	private HudFrameComponent frame;

	TextInput(Hud hud, String defaultText, ButtonPositionX posX, ButtonPositionY posY) {
		super(defaultText, hud, posX, posY);
		resetFrameComponent();
	}

	@Override
	void update(Input input, Vector2f windowSize, float margin, float fontHeight) {
		width = (int) (windowSize.x * 0.4);
		textComponent.getTransformation().scaling(1f);
		setPosition(windowSize, margin, fontHeight);
		frame.setSize(new Vector2i(width + font.getSize().y / 4 * 2, font.getSize().y));
		frame.setPos(new Vector2f(pos.x - font.getSize().y / 4, pos.y));
		if (input.isLastMouseButtonPress(GLFW_MOUSE_BUTTON_LEFT)) {
			if (input.getMousePosY() < windowSize.y - pos.y
					&& input.getMousePosY() > windowSize.y - pos.y - font.getSize().y && input.getMousePosX() > pos.x
					&& input.getMousePosX() < pos.x + width)
				selected = true;
			else
				selected = false;
		}
		if (selected) {
			if (input.isLastKeyPress(GLFW_KEY_BACKSPACE) && text.length() > 0) {
				text = text.substring(0, text.length() - 1);
				setText(text);
			}
			for (Character c : input.getKeyCharPressed())
				if (font.getCharTextureWidth(c) != null
						&& textComponent.getSize().x + font.getCharTextureWidth(c).getWidth()
								* textComponent.getTransformation().getScale(new Vector3f()).x < width)
					setText(text + c);
		}
		frame.setRoundCornerSize(frame.getSize().y / 3f);
		textComponent.setColor(selected ? new Vector4f(1) : new Vector4f(0.5f, 0.5f, 0.5f, 1));
	}

	void resetFrameComponent() {
		unShow();
		frame = new HudFrameComponent(new Vector2i(width, font.getSize().y), 20);
		frame.setBackground(true);
		frame.setColor(new Vector4f(0));
	}

	@Override
	void unShow() {
		super.unShow();
		hud.removeComponent(frame);
	}

	@Override
	void show() {
		hud.replaceComponent(frame); // 1
		super.show(); // 2
		// This order is critical otherwise opengl don't blend well the alpha
	}

}
