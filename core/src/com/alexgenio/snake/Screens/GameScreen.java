package com.alexgenio.snake.screens;

import com.alexgenio.snake.BodyPart;
import com.alexgenio.snake.Snake;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by AlexGenio on 2016-09-01.
 */
public class GameScreen extends ScreenAdapter
{
    private static final float MOVE_TIME = 0.2F;
    private static final int SNAKE_MOVEMENT = 10;
    private static final int POINT_INCREMENT = 10;
    private static final int MAP_OFFSET = 10;
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private float m_Timer = MOVE_TIME;
    private int m_MapBorderLeft = 0, m_MapBorderRight = 0, m_MapBorderTop = 0, m_MapBorderBottom = 0;
    private Rectangle m_LeftArrowBounds, m_RightArrowBounds, m_UpArrowBounds, m_DownArrowBounds;
    private int m_SnakeXBeforeUpdate = 0, m_SnakeYBeforeUpdate = 0;
    private int m_SnakeX = 0, m_SnakeY = 0;
    private int m_AppleX = 0, m_AppleY = 0;
    private int m_SnakeDirection = RIGHT;

    private final Snake m_Game;
    private Texture m_SnakeHead;
    private Texture m_SnakeBody;
    private Texture m_Apple;
    private Texture m_LeftArrow, m_RightArrow, m_UpArrow, m_DownArrow;
    private Random m_Rand;
    private Array<BodyPart> m_BodyParts;
    private OrthographicCamera m_Camera;

    private int m_Score;
    private String m_ScoreText;
    private BitmapFont m_ScoreFont;
    private static GlyphLayout m_ScoreLayout;

    private Preferences m_Prefs;
    private int m_BestScore;
    private String m_BestScoreText;
    private static GlyphLayout m_BestScoreLayout;

    private boolean m_PlacedApple;
    private boolean m_GameOver;

    public GameScreen(final Snake Game)
    {
        this.m_Game = Game;

        // set viewport to bottom left quadrant of screen
        this.m_Camera = new OrthographicCamera();
        this.m_Camera.setToOrtho(false, Snake.WIDTH / 2, Snake.HEIGHT / 2);

        this.m_SnakeHead = new Texture(Gdx.files.internal("snakehead.png"));
        this.m_SnakeBody = new Texture(Gdx.files.internal("snakebody.png"));
        this.m_Apple = new Texture(Gdx.files.internal("apple.png"));
        this.m_Rand = new Random();
        this.m_BodyParts = new Array<BodyPart>();

        this.m_Score = 0;
        this.m_ScoreText = "Score: ";
        this.m_ScoreFont = new BitmapFont();
        this.m_ScoreLayout = new GlyphLayout();

        this.m_Prefs = Gdx.app.getPreferences("snake");

        this.m_BestScore  = this.m_Prefs.getInteger("highscore", 0);
        this.m_BestScoreText = "Best Score: ";
        this.m_BestScoreLayout = new GlyphLayout();

        this.m_PlacedApple = false;
        this.m_GameOver = false;

        this.m_MapBorderLeft = MAP_OFFSET;
        this.m_MapBorderBottom = (int)(this.m_Camera.viewportHeight / 3) + MAP_OFFSET * 2;
        this.m_MapBorderTop = (int)(this.m_MapBorderBottom + ((this.m_Camera.viewportHeight / 3 * 2) - (MAP_OFFSET * 6)));
        this.m_MapBorderRight = (int)(this.m_Camera.viewportWidth - MAP_OFFSET);

        this.m_LeftArrow = new Texture(Gdx.files.internal("leftarrow.png"));
        this.m_RightArrow = new Texture(Gdx.files.internal("rightarrow.png"));
        this.m_UpArrow = new Texture(Gdx.files.internal("uparrow.png"));
        this.m_DownArrow = new Texture(Gdx.files.internal("downarrow.png"));

        this.m_LeftArrowBounds = new Rectangle((this.m_MapBorderRight - this.m_MapBorderLeft) / 3 - 25, this.m_MapBorderBottom / 2 - 25, 50, 45);
        this.m_RightArrowBounds = new Rectangle((this.m_MapBorderRight - this.m_MapBorderLeft) / 3 * 2, this.m_MapBorderBottom / 2 - 25, 50, 45);
        this.m_UpArrowBounds = new Rectangle(this.m_Camera.viewportWidth / 2 - 25, (this.m_LeftArrowBounds.y + this.m_LeftArrowBounds.height), 50, 45);
        this.m_DownArrowBounds = new Rectangle(this.m_Camera.viewportWidth / 2 - 25, (this.m_LeftArrowBounds.y - this.m_LeftArrowBounds.height), 50, 45);


        this.m_SnakeX = this.m_MapBorderLeft;
        this.m_SnakeY = this.m_MapBorderBottom;
    }

    @Override
    public void render(float delta)
    {
        if (!this.m_GameOver)
        {
            pollForInput();

            // deduct time from the last frame
            this.m_Timer -= delta;
            if (this.m_Timer <= 0) {
                // frame has completed, move the snake
                this.m_Timer = MOVE_TIME;

                move();
                checkForBodyCollision();
                checkSnakeBounds();
                updateBodyPartsPosition();
            }
            checkForAppleCollision();
            positionApple();
        }
        else
        {
            updateHighScore();
        }
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
        boolean _LeftPressed;
        boolean _RightPressed;
        boolean _UpPressed;
        boolean _DownPressed;

        if (Gdx.input.justTouched())
        {
            Vector3 _InputCoordinates= new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            // make touch input relative to the camera coordinate region
            this.m_Camera.unproject(_InputCoordinates);

            _LeftPressed  = this.m_LeftArrowBounds.contains(_InputCoordinates.x, _InputCoordinates.y);
            _RightPressed = this.m_RightArrowBounds.contains(_InputCoordinates.x, _InputCoordinates.y);
            _UpPressed    = this.m_UpArrowBounds.contains(_InputCoordinates.x, _InputCoordinates.y);
            _DownPressed  = this.m_DownArrowBounds.contains(_InputCoordinates.x, _InputCoordinates.y);
        }
        else
        {
            _LeftPressed  = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            _RightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            _UpPressed    = Gdx.input.isKeyPressed(Input.Keys.UP);
            _DownPressed  = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        }


        if (_LeftPressed)
        {
            this.m_SnakeDirection = LEFT;
        }

        if (_RightPressed)
        {
            this.m_SnakeDirection = RIGHT;
        }

        if (_UpPressed)
        {
            this.m_SnakeDirection = UP;
        }

        if (_DownPressed)
        {
            this.m_SnakeDirection = DOWN;
        }
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

    private void checkForBodyCollision()
    {
        for (BodyPart _BodyPart : this.m_BodyParts)
        {
            if (_BodyPart.hasCollided(this.m_SnakeX, this.m_SnakeY))
                this.m_GameOver = true;
        }
    }

    private void checkSnakeBounds()
    {
        // reposition snake on left if past right border
        if (this.m_SnakeX >= this.m_MapBorderRight)
            this.m_SnakeX = this.m_MapBorderLeft;

        // reposition snake on right if past left border
        if (this.m_SnakeX < this.m_MapBorderLeft)
            this.m_SnakeX = this.m_MapBorderRight - MAP_OFFSET;

        // reposition snake on bottom if past top border
        if (this.m_SnakeY >= this.m_MapBorderTop)
            this.m_SnakeY = this.m_MapBorderBottom;

        // reposition snake on top if past bottom border
        if (this.m_SnakeY < this.m_MapBorderBottom)
            this.m_SnakeY = this.m_MapBorderTop - MAP_OFFSET;
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

    private void checkForAppleCollision()
    {
        if (this.m_PlacedApple && this.m_AppleX == this.m_SnakeX && this.m_AppleY == this.m_SnakeY)
        {
            BodyPart _BodyPart = new BodyPart(this.m_SnakeBody);
            _BodyPart.updateBodyPosition(this.m_SnakeX, this.m_SnakeY);
            this.m_BodyParts.insert(0, _BodyPart);

            this.m_PlacedApple = false;
            this.m_Score += POINT_INCREMENT;
        }
    }

    private void positionApple()
    {
        if (!this.m_PlacedApple)
        {
            do
            {
                this.m_AppleX = this.m_Rand.nextInt(this.m_MapBorderRight / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT + this.m_MapBorderLeft;
                this.m_AppleY = this.m_Rand.nextInt((this.m_MapBorderTop - this.m_MapBorderBottom) / SNAKE_MOVEMENT - 1) * SNAKE_MOVEMENT + this.m_MapBorderBottom;
                this.m_PlacedApple = true;
            }
            while (this.m_AppleX == this.m_SnakeX && this.m_AppleY == this.m_SnakeY);
        }
    }

    private void updateHighScore()
    {
        if (this.m_Score > this.m_BestScore)
        {
            this.m_Prefs.putInteger("highscore", this.m_Score);
            this.m_Prefs.flush();
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
        // scale position of bird in relation to the viewport
        this.m_Game.m_Batch.setProjectionMatrix(this.m_Camera.combined);

        // draw map border first
        // this avoids two objects drawing at the same time (i.e. sprite batch and shape renderer)
        this.m_Game.m_Renderer.setProjectionMatrix(this.m_Camera.combined);
        this.m_Game.m_Renderer.begin(ShapeRenderer.ShapeType.Line);
        this.m_Game.m_Renderer.setColor(Color.WHITE);
        this.m_Game.m_Renderer.rect(this.m_MapBorderLeft, this.m_MapBorderBottom, this.m_Camera.viewportWidth - (MAP_OFFSET * 2), this.m_Camera.viewportHeight / 3 * 2 - (MAP_OFFSET * 6));
        this.m_Game.m_Renderer.end();

        this.m_Game.m_Batch.begin();

        // draw snake head
        this.m_Game.m_Batch.draw(this.m_SnakeHead, this.m_SnakeX, this.m_SnakeY);

        // draw snake body
        for (BodyPart _BodyPart : this.m_BodyParts) {
            _BodyPart.draw(this.m_Game.m_Batch, this.m_SnakeX, this.m_SnakeY);
        }

        // draw apple
        if (this.m_PlacedApple)
            this.m_Game.m_Batch.draw(this.m_Apple, this.m_AppleX, this.m_AppleY);

        // draw high score
        this.m_ScoreFont.setColor(Color.WHITE);
        this.m_BestScoreLayout.setText(this.m_ScoreFont, (this.m_BestScoreText + this.m_BestScore));
        this.m_ScoreFont.draw(this.m_Game.m_Batch, this.m_BestScoreLayout, MAP_OFFSET, this.m_Camera.viewportHeight - this.m_BestScoreLayout.height);

        // draw current score
        this.m_ScoreLayout.setText(this.m_ScoreFont, (this.m_ScoreText + this.m_Score));
        this.m_ScoreFont.draw(this.m_Game.m_Batch, this.m_ScoreLayout, this.m_Camera.viewportWidth - this.m_ScoreLayout.width - MAP_OFFSET, this.m_Camera.viewportHeight - this.m_ScoreLayout.height);

        // draw control pad
        this.m_Game.m_Batch.draw(this.m_LeftArrow, this.m_LeftArrowBounds.x, this.m_LeftArrowBounds.y, this.m_LeftArrowBounds.width, this.m_LeftArrowBounds.height);
        this.m_Game.m_Batch.draw(this.m_RightArrow, this.m_RightArrowBounds.x, this.m_RightArrowBounds.y, this.m_RightArrowBounds.width, this.m_RightArrowBounds.height);
        this.m_Game.m_Batch.draw(this.m_UpArrow, this.m_UpArrowBounds.x, this.m_UpArrowBounds.y, this.m_UpArrowBounds.width, this.m_UpArrowBounds.height);
        this.m_Game.m_Batch.draw(this.m_DownArrow, this.m_DownArrowBounds.x, this.m_DownArrowBounds.y, this.m_DownArrowBounds.width, this.m_DownArrowBounds.height);

        this.m_Game.m_Batch.end();
    }
}
