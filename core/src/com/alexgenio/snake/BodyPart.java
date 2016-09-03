package com.alexgenio.snake;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by AlexGenio on 2016-09-01.
 */
public class BodyPart
{
    private int m_X, m_Y;
    private Texture m_Body;

    public BodyPart(Texture texture)
    {
        this.m_Body = texture;
    }

    public void updateBodyPosition(int x, int y)
    {
        this.m_X = x;
        this.m_Y = y;
    }

    public void draw(Batch batch, int snakeX, int snakeY)
    {
        if (!(this.m_X == snakeX && this.m_Y == snakeY))
            batch.draw(this.m_Body, this.m_X, this.m_Y);
    }

    public boolean hasCollided(int snakeX, int snakeY)
    {
        if (this.m_X == snakeX && this.m_Y == snakeY)
            return true;
        return false;
    }
}
