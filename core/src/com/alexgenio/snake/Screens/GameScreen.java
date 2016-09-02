package com.alexgenio.snake.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by AlexGenio on 2016-09-01.
 */
public class GameScreen extends ScreenAdapter
{
    private static final float MOVE_TIME = 0.2F;
    private static final int SNAKE_MOVEMENT = 10;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private float m_Timer = MOVE_TIME;
    private int m_SnakeX = 0, m_SnakeY = 0;
    private int m_SnakeDirection = RIGHT;

    private SpriteBatch m_Batch;
    private Texture m_SnakeHead;
    private Texture m_Apple;

    @Override
    public void show()
    {
        this.m_Batch = new SpriteBatch();
        this.m_SnakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        this.m_Apple = new Texture(Gdx.files.internal("apple.png"));
    }

    @Override
    public void render(float delta)
    {
        pollForInput();

        // deduct time from the last frame
        this.m_Timer -= delta;
        if (this.m_Timer <= 0)
        {
            // frame has completed, move the snake
            this.m_Timer = MOVE_TIME;

            move();
            checkSnakeBounds();
        }

        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.m_Batch.begin();

        // rendering code
        this.m_Batch.draw(this.m_SnakeHead, this.m_SnakeX, this.m_SnakeY);

        this.m_Batch.end();
    }

    private void checkSnakeBounds()
    {
        // reposition snake on left if past right border
        if (this.m_SnakeX >= Gdx.graphics.getWidth())
            this.m_SnakeX = 0;

        // reposition snake on right if past left border
        if (this.m_SnakeX < 0)
            this.m_SnakeX = Gdx.graphics.getWidth() - SNAKE_MOVEMENT;

        // reposition snake on bottom if past top border
        if (this.m_SnakeY >= Gdx.graphics.getHeight())
            this.m_SnakeY = 0;

        // reposition snake on top if past bottom border
        if (this.m_SnakeY < 0)
            this.m_SnakeY = Gdx.graphics.getHeight() - SNAKE_MOVEMENT;
    }

    private void move()
    {
        switch (this.m_SnakeDirection)
        {
            case RIGHT:
            {
                this.m_SnakeX += SNAKE_MOVEMENT;
                break;
            }
            case LEFT:
            {
                this.m_SnakeX -= SNAKE_MOVEMENT;
                break;
            }
            case UP:
            {
                this.m_SnakeY += SNAKE_MOVEMENT;
                break;
            }
            case DOWN:
            {
                this.m_SnakeY -= SNAKE_MOVEMENT;
                break;
            }
        }
    }

    private void pollForInput()
    {
        boolean _LeftPressed  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean _RightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean _UpPressed    = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean _DownPressed  = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        if (_LeftPressed)
            this.m_SnakeDirection = LEFT;
        if (_RightPressed)
            this.m_SnakeDirection = RIGHT;
        if (_UpPressed)
            this.m_SnakeDirection = UP;
        if (_DownPressed)
            this.m_SnakeDirection = DOWN;
    }
}
