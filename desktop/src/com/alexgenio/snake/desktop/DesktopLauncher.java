package com.alexgenio.snake.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.alexgenio.snake.Snake;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = Snake.TITLE;
        config.height = Snake.HEIGHT;
        config.width = Snake.WIDTH;

		new LwjglApplication(new Snake(), config);
	}
}
