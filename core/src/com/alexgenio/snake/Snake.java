package com.alexgenio.snake;

import com.alexgenio.snake.screens.GameScreen;
import com.badlogic.gdx.Game;

public class Snake extends Game
{

	@Override
	public void create ()
	{
		setScreen(new GameScreen());
	}
}
