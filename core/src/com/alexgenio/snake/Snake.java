package com.alexgenio.snake;

import com.alexgenio.snake.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Snake extends Game
{
	public static final int HEIGHT = 800;
	public static final int WIDTH = 480;
	public static final String TITLE = "Snake";

	public SpriteBatch m_Batch;

	@Override
	public void create()
	{
		this.m_Batch = new SpriteBatch();
		this.setScreen(new GameScreen(this));
	}

	@Override
	public void dispose()
	{
		this.m_Batch.dispose();
	}
}
