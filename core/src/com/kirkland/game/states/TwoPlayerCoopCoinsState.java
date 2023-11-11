package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;
import com.kirkland.game.sprites.Coin;
import com.kirkland.game.sprites.Pipe;
import com.kirkland.game.sprites.Player;

import java.util.ArrayList;

public class TwoPlayerCoopCoinsState extends State{
    public final int COIN_SPACING = 125;
    private static final int COIN_COUNT = 4;

    private Player player;
    private Texture bg;
    private int streak = 0;
    private int speed_mod = 0;

    private int TURN = 1;
    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int score;
    private String ScoreStr;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;



    public TwoPlayerCoopCoinsState(GameStateManager gsm) {
        super(gsm);
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2, flappy_game.HEIGHT/2);
        bg = new Texture("800_bg.png");
        score = 0;
        ScoreStr = "Score: 0";

        font = new BitmapFont();
        font.setUseIntegerPositions(false);

        coins = new ArrayList<Coin>();

        for(int i = 0; i< COIN_COUNT; i++){ //keep a list of coins. do not reposition them, just
            coins.add(new Coin(i* (COIN_SPACING + Coin.COIN_WIDTH )));
        }

    }

    @Override
    protected void handleInput() {
//      if(Gdx.input.justTouched()){
//            player.jump();
//      }
        if(TURN==1 && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
            player.jump();
            TURN = 2;
        }
        if(TURN==2 && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            player.jump();
            TURN = 1;
        }


        if (PAUSE){
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                PAUSE = false;
            }
        } else{
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                PAUSE = true;
            }
        }

    }

    @Override
    public void update(float dt) {
        handleInput();
        if (!PAUSE){
            player.update(dt);
            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
            //how to reposition thetubes
            for(int i = 0; i< COIN_COUNT; i++){
                Coin coin = coins.get(i);
                if(cam.position.x - (cam.viewportWidth/2) > coin.getPos().x + coin.getCoin().getWidth()){ //repostition each pipe
                    coin.reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT));
                }

                if(coin.collides(player.getBounds()) ){
                    //check if pass middle
                    score++;
                    ScoreStr = "Score: " + score;
                    System.out.println("SCOREEEE");
                }

            }

            cam.update();
        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0);//OPEN THE BOX UP
        if(TURN ==1){
            sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y);
        } else{
            sb.draw(player.getTexture2(), player.getPosition().x, player.getPosition().y);
        }
        //draw the coins
        for(Coin coin: coins){
            if (!coin.isHit()){
                sb.draw(coin.getCoin(), coin.getPos().x, coin.getPos().y);
            }
        }

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.getData().setScale(2);
        font.draw(sb, ScoreStr, player.getPosition().x, 350);

        sb.end(); //close it...


    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
        for(Coin coin : coins)
            coin.dispose();
        System.out.println("play state disposed");
    }
}
