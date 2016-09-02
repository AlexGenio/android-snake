package com.alexgenio.snake;

import com.alexgenio.snake.screens.GameScreen;
import com.badlogic.gdx.Game;

public class Snake extends Game
{
	public static final int HEIGHT = 800;
	public static final int WIDTH = 480;
	public static final String TITLE = "Snake";

	@Override
	public void create ()
	{
		setScreen(new GameScreen());
	}
}
