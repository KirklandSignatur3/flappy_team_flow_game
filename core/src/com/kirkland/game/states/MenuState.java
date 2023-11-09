package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;

public class MenuState extends State{
    private Texture background;
//    private Texture start_button;

    public MenuState (GameStateManager gsm){
        super (gsm);
        background = new Texture("title_screen.jpg");
//        start_button = new Texture("start_button.png");

    }
    @Override
    protected void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            gsm.set(new PlayState(gsm)); //creates a new Play State at the top of the stack
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

    }

    @Override
    public void render(SpriteBatch sb) {
        //open, put in, the nclose
        sb.begin();
        sb.draw(background, 0,0, flappy_game.WIDTH, flappy_game.HEIGHT);
        //starts drawing at bottom left
//        sb.draw(start_button , (flappy_game.WIDTH/2) - (start_button.getWidth()/2),(flappy_game.HEIGHT/2) - (start_button.getHeight()/2));
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
//        start_button.dispose();
        System.out.println("menu state disposed");
    }
}
