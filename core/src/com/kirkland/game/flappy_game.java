package com.kirkland.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.states.GameStateManager;
import com.badlogic.gdx.graphics.Texture;
import com.kirkland.game.states.MenuState;

public class flappy_game extends ApplicationAdapter {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final float SCALE = 0.5f;
	public static final String TITLE = "FlappyBird";
	private GameStateManager gsm;
	private SpriteBatch batch; //only need one of

	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();
		gsm.push(new MenuState(gsm));
		img = new Texture("badlogic.jpg");
		Gdx.gl.glClearColor(1,0,0,1); //wipes screen, re-draws it fresh

	}

	@Override
	public void render () {
//		ScreenUtils.clear(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
