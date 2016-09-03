package com.alexgenio.snake;

import com.alexgenio.snake.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Snake extends Game
{
	public static final int HEIGHT = 810;
	public static final int WIDTH = 480;
	public static final String TITLE = "Snake";

	public SpriteBatch m_Batch;
	public ShapeRenderer m_Renderer;

	@Override
	public void create()
	{
		this.m_Batch = new SpriteBatch();
		this.m_Renderer = new ShapeRenderer();
		this.setScreen(new GameScreen(this));
	}

	@Override
	public void dispose()
	{
		this.m_Batch.dispose();
	}
}
