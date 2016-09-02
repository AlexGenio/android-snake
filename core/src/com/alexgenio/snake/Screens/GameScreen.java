package com.alexgenio.snake.screens;

import com.alexgenio.snake.BodyPart;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

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
    private int m_SnakeXBeforeUpdate = 0, m_SnakeYBeforeUpdate = 0;
    private int m_SnakeX = 0, m_SnakeY = 0;
    private int m_AppleX = 0, m_AppleY = 0;
    private int m_SnakeDirection = RIGHT;

    private SpriteBatch m_Batch;
    private Texture m_SnakeHead;
    private Texture m_SnakeBody;
    private Texture m_Apple;
    private Random m_Rand;
    private Array<BodyPart> m_BodyParts;

    private boolean m_PlacedApple;

    @Override
    public void show()
    {
        this.m_Batch = new SpriteBatch();
        this.m_SnakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        this.m_SnakeBody = new Texture(Gdx.files.internal("snakebody.png"));
        this.m_Apple = new Texture(Gdx.files.internal("apple.png"));
        this.m_Rand = new Random();
        this.m_BodyParts = new Array<BodyPart>();
        this.m_PlacedApple = false;
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
            updateBodyPartsPosition();
        }
        checkForCollision();
        positionApple();
        clearScreen();
        draw();
    }

    /**********************************************************************
     *
     *                     RENDER HELPER FUNCTIONS
     *
     *********************************************************************/

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

    private void move()
    {
        // keep track of prior coordinates for body parts
        this.m_SnakeXBeforeUpdate = this.m_SnakeX;
        this.m_SnakeYBeforeUpdate = this.m_SnakeY;

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

    private void updateBodyPartsPosition()
    {
        if (this.m_BodyParts.size > 0)
        {
            BodyPart _BodyPart = this.m_BodyParts.removeIndex(0);
            _BodyPart.updateBodyPosition(this.m_SnakeXBeforeUpdate, this.m_SnakeYBeforeUpdate);
            this.m_BodyParts.add(_BodyPart);
        }
    }

    private void checkForCollision()
    {
        if (this.m_PlacedApple && this.m_AppleX == this.m_SnakeX && this.m_AppleY == this.m_SnakeY)
        {
            BodyPart _BodyPart = new BodyPart(this.m_SnakeBody);
            _BodyPart.updateBodyPosition(this.m_SnakeX, this.m_SnakeY);
            this.m_BodyParts.insert(0, _BodyPart);

            this.m_PlacedApple = false;
        }
    }

    private void positionApple()
    {
        if (!this.m_PlacedApple)
        {
            do
            {
                this.m_AppleX = this.m_Rand.nextInt(Gdx.graphics.getWidth() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                this.m_AppleY = this.m_Rand.nextInt(Gdx.graphics.getHeight() / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT;
                this.m_PlacedApple = true;
            }
            while (this.m_AppleX == this.m_SnakeX && this.m_AppleY == this.m_SnakeY);
        }
    }

    private void clearScreen()
    {
        // clear the screen (black)
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw()
    {
        this.m_Batch.begin();

        this.m_Batch.draw(this.m_SnakeHead, this.m_SnakeX, this.m_SnakeY);

        for (BodyPart _BodyPart : this.m_BodyParts) {
            _BodyPart.draw(this.m_Batch, this.m_SnakeX, this.m_SnakeY);
        }

        if (this.m_PlacedApple)
            this.m_Batch.draw(this.m_Apple, this.m_AppleX, this.m_AppleY);

        this.m_Batch.end();
    }
}
