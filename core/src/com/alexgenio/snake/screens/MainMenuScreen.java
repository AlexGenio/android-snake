package com.alexgenio.snake.screens;

import com.alexgenio.snake.Snake;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by AlexGenio on 2016-09-03.
 */
public class MainMenuScreen extends ScreenAdapter
{
    private static final int MENU_OFFSET = 30;

    private final Snake m_Game;
    private Texture m_Background;
    private BitmapFont m_Font;

    private String m_ClassicText;
    private GlyphLayout m_ClassicLayout;
    private Rectangle m_ClassicBounds;

    public MainMenuScreen(final Snake Game)
    {
        this.m_Game = Game;

        // set viewport to bottom left quadrant of screen
        this.m_Game.m_Camera.setToOrtho(false, Snake.WIDTH / 2, Snake.HEIGHT / 2);
        this.m_Font = new BitmapFont();
        this.m_Background = new Texture(Gdx.files.internal("mainmenubackground.jpg"));

        this.m_ClassicText = "CLASSIC";
        this.m_ClassicLayout = new GlyphLayout();
        this.m_ClassicBounds = new Rectangle(this.m_Game.m_Camera.position.x - (Snake.BUTTON_WIDTH / 2), this.m_Game.m_Camera.position.y - (Snake.BUTTON_HEIGHT / 2) - MENU_OFFSET, Snake.BUTTON_WIDTH, Snake.BUTTON_HEIGHT);
    }

    @Override
    public void render(float delta)
    {
        pollForInput();
        clearScreen();
        draw();
    }

    @Override
    public void dispose()
    {
        this.m_Background.dispose();
        this.m_Font.dispose();
    }

    /**********************************************************************
     *
     *                     RENDER HELPER FUNCTIONS
     *
     *********************************************************************/

    private void pollForInput()
    {
        boolean _ClassicPressed;

        if (Gdx.input.justTouched())
        {
            Vector3 _InputCoordinates= new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            // make touch input relative to the camera coordinate region
            this.m_Game.m_Camera.unproject(_InputCoordinates);

            _ClassicPressed  = this.m_ClassicBounds.contains(_InputCoordinates.x, _InputCoordinates.y);

            if (_ClassicPressed)
            {
                this.m_Game.setScreen(new GameScreen(this.m_Game));
            }
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
        this.m_Game.m_Batch.setProjectionMatrix(this.m_Game.m_Camera.combined);

        // draw main menu background first
        this.m_Game.m_Batch.begin();
        this.m_Game.m_Batch.draw(this.m_Background, 0, 0, this.m_Game.m_Camera.viewportWidth, this.m_Game.m_Camera.viewportHeight);
        this.m_Game.m_Batch.end();

        drawClassicMode();
    }

    private void drawClassicMode()
    {
        // draw the classic button
        this.m_Game.m_Renderer.setProjectionMatrix(this.m_Game.m_Camera.combined);
        this.m_Game.m_Renderer.begin(ShapeRenderer.ShapeType.Filled);
        this.m_Game.m_Renderer.setColor(Color.BLUE);
        this.m_Game.m_Renderer.rect(this.m_ClassicBounds.x, this.m_ClassicBounds.y, this.m_ClassicBounds.width, this.m_ClassicBounds.height);
        this.m_Game.m_Renderer.end();

        this.m_Game.m_Renderer.begin(ShapeRenderer.ShapeType.Line);
        this.m_Game.m_Renderer.setColor(Color.WHITE);
        this.m_Game.m_Renderer.rect(this.m_ClassicBounds.x, this.m_ClassicBounds.y, this.m_ClassicBounds.width, this.m_ClassicBounds.height);
        this.m_Game.m_Renderer.end();

        // draw classic button text
        this.m_Game.m_Batch.begin();
        this.m_Font.setColor(Color.BLACK);
        this.m_ClassicLayout.setText(this.m_Font, this.m_ClassicText);

        Vector2 _ClassicCenter = new Vector2();
        this.m_ClassicBounds.getCenter(_ClassicCenter);

        this.m_Font.draw(this.m_Game.m_Batch, this.m_ClassicLayout, _ClassicCenter.x - this.m_ClassicLayout.width / 2, _ClassicCenter.y + this.m_ClassicLayout.height / 2);
        this.m_Game.m_Batch.end();
    }
}
