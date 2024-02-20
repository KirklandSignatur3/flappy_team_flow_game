package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;
import com.kirkland.game.sprites.Coin;
import com.kirkland.game.sprites.Log;
import com.kirkland.game.sprites.Pipe;
import com.kirkland.game.sprites.Player;
import com.kirkland.game.sprites.Log;

import java.util.ArrayList;

public class TwoPlayerCoopCoinsState extends State{
    public final int COIN_SPACING = 125;
    private static final float GAME_DURATION = 180;
    private float time = 0;

    private static final int COIN_COUNT = 4;
    private int default_player_speed = 100;
    private Player player;
    private Texture bg;
    private float streak = 0f;
    private int speed_mod = 0;

    private int TURN = 1;
    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int score;
    private String ScoreStr;
    private Log log;
    public final int START_GAP = 300;// 125


    BitmapFont font;

//    private FreeTypeFontGenerator fontGenerator;



    public TwoPlayerCoopCoinsState(GameStateManager gsm) {
        super(gsm);
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        bg = new Texture("800_bg.png");
        score = 0;
        ScoreStr = "Score: 0";

        font = new BitmapFont();
        font.setUseIntegerPositions(false);

        coins = new ArrayList<Coin>();
        coins.add(new Coin(START_GAP, 1, 1));
        log = new Log("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/","TwoPlayerCoopCoinsState");

        for(int i = 0; i< COIN_COUNT; i++){ //keep a list of coins. do not reposition them, just
//            coins.add(new Coin(i* (COIN_SPACING + Coin.COIN_WIDTH ),0,1));
            coins.add(new Coin(START_GAP + i* (COIN_SPACING + Coin.COIN_WIDTH ), (coins.get(i-1)).getPos().y, 2));

        }
        log.log_event(time,Log.START_GAME, 0);


    }

    @Override
    protected void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
           if(TURN == 1){
               player.jump();
               log.log_event(time, Log.P1_JUMP, player.getPosition().y);
               TURN = 2;
           } else{
               log.log_event(time, Log.P1_OFF_TIME_PRESS, player.getPosition().y);
           }

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            if(TURN == 2){
                player.jump();
                log.log_event(time, Log.P2_JUMP, player.getPosition().y);
                TURN = 1;
            } else{
                log.log_event(time, Log.P2_OFF_TIME_PRESS, player.getPosition().y);
            }

        }


        //
//        if(TURN==1 && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
//            player.jump();
//            log.log_event(time, Log.P1_JUMP);
//            TURN = 2;
//        }
//        if(TURN==2 && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
//            player.jump();
//            log.log_event(time, Log.P2_JUMP);
//            TURN = 1;
//        }


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
            time += dt;
            if (time>GAME_DURATION){
                log.log_event(time, Log.END_GAME, 0);
                log.close();
                gsm.set(new MenuState(gsm));
            }
            // streak toggle //
            player.setSpeed(default_player_speed*( 1f + (streak / 10f) ));
//            player.update(dt );

            player.update(dt);
            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
            //how to reposition thetubes
            for(int i = 0; i< COIN_COUNT; i++){
                Coin coin = coins.get(i);
                if(cam.position.x - (cam.viewportWidth/2) > coin.getPos().x + coin.getCoin().getWidth()){ //repostition each coin when it's offscreen
                    // if
                    if (!coin.isHit()){
                        System.out.println("coin missed");
                        streak = 0f;
                    }
//                    coin.reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT));
                    coin.close_reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT),
                            (coins.get(i==0?coins.size()-1:i-1)).getPos().y );
                }

                if(coin.collides(player.getBounds()) ){ // CHECK IF A PALYER TOUCHES COIN
                    score++;
                    streak++;
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
        ScoreStr = "Score: " + score + "    Streak: " + streak;
        font.draw(sb, ScoreStr, player.getPosition().x-100, 390);
        font.draw(sb, "Total Time: "+ String.format("%.1f", time), player.getPosition().x-100, 375);

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
