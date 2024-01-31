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
import com.kirkland.game.sprites.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class TwoPlayerCoopTimingCoinsState extends State{
    public final int COIN_SPACING = 125;// 125
    public final int START_GAP = 300;// 125
    private static final int COIN_COUNT = 4; //4 //4*125 = 500

    private static final float GAME_DURATION = 30;
    private float time = 0;
    private Player player;
    private Texture bg;
    private int streak = 0;
    private int speed_mod = 0;

    private boolean one_pressed = false;
    private boolean two_pressed = false;
    private float jump_time = 0f;
    private float jump_time_window = 0.1f;

    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int score;
    private String ScoreStr;
    private String jump_time_str;
    private Log log;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;



    public TwoPlayerCoopTimingCoinsState(GameStateManager gsm) {
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


        log = new Log("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/","TwoPlayerCoopTimingCoinsState");
        for(int i = 1; i< COIN_COUNT; i++){ //keep a list of coins. do not reposition them, just
//            coins.add(new Coin(i* (COIN_SPACING + Coin.COIN_WIDTH ), 0, 1));
            coins.add(new Coin(START_GAP + i* (COIN_SPACING + Coin.COIN_WIDTH ), (coins.get(i-1)).getPos().y, 2));
        }
        log.log_event(time,Log.START_GAME);


    }

    @Override
    protected void handleInput() { // check if the keys are pressed within 50 ms of eachother

        if(!one_pressed && !two_pressed){  // if neither key has been pressed
            if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
                one_pressed = true;
                log.log_event(time, Log.P1_JUMP);
            }else if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)) {
                two_pressed = true;
                log.log_event(time, Log.P2_JUMP);
            }
        }

        if(one_pressed && (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT))) {
            log.log_event(time, Log.P1_JUMP);
            one_pressed = false;
            two_pressed = false;
            player.jump();
            jump_time = 0;
        } else if(two_pressed && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            log.log_event(time, Log.P2_JUMP);
            one_pressed = false;
            two_pressed = false;
            player.jump();
            jump_time = 0;
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) && Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            // DOES THIS EVER HAPPEN????
            log.log_event(time, Log.P2_JUMP);
            log.log_event(time, Log.P1_JUMP);
            one_pressed = false;
            two_pressed = false;
            player.jump();
            jump_time = 0;
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
            time += dt;
            if (time>GAME_DURATION){
                log.log_event(time, Log.END_GAME);
                log.close();
                gsm.set(new MenuState(gsm));
            }

            if(one_pressed || two_pressed){
                jump_time += dt;
            }
            if(jump_time > jump_time_window){
                one_pressed = false;
                two_pressed = false;
                jump_time = 0;
            }
            player.update(dt);
            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
            //how to reposition thetubes

            for(int i = 0; i< COIN_COUNT; i++){
                Coin coin = coins.get(i);
                if(cam.position.x - (cam.viewportWidth/2) > coin.getPos().x + coin.getCoin().getWidth()){ //repostition each pipe
                    // normal random reposition
//                    coin.reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT));
                    // reposition closer to last coin
                    if (!coin.isHit()){
                        log.log_event(time, Log.MISS_COIN);
                    }

                    coin.close_reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT),
                                            (coins.get(i==0?coins.size()-1:i-1)).getPos().y );


                }

//                if (!coin.isHit()){
//                    if(player.getPosition().x > coin.getPos().x + coin.getCoin().getWidth()){ //if player is to the right of the coin
//                        log.log_event(time, Log.MISS_COIN);
//                    }
//                }

                if(coin.collides(player.getBounds()) ){ // CHECK IF A PALYER TOUCHES COIN
                    log.log_event(time, Log.HIT_COIN);
                    score++;
                    streak++;
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

        sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y);
        //draw the coins

        for(Coin coin: coins){
            if (!coin.isHit()){
                sb.draw(coin.getCoin(), coin.getPos().x, coin.getPos().y);
            }
        }

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.getData().setScale(2);
        font.draw(sb, ScoreStr + "   " + jump_time_str, player.getPosition().x-100, 400);
        jump_time_str = "Time: " + (jump_time_window - jump_time);
//        System.out.println((jump_time_window - jump_time));
        font.draw(sb, jump_time_str, 500, 500);
        font.draw(sb, "time:"+time, player.getPosition().x-100, 300);

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
