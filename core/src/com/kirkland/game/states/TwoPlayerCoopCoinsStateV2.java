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
import com.badlogic.gdx.audio.Sound;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class TwoPlayerCoopCoinsStateV2 extends State{
    public final int COIN_SPACING = 125;// 125
    public final int START_GAP = 300;// 125
    private static final int COIN_COUNT = 8; //4 //4*125 = 500


    private static final float GAME_DURATION = 180;
    private float time = 0;
    private float player_pos_cooldown  = 0; // timer for recording player pos
    private final float player_pos_rate  = .1f; // records player postion every 50 ms (
    private int default_player_speed = 100;
    private Player player;
    private Texture bg;
    private int streak = 0;
    private final float score_popup_duration = 0.7f;
    private float score_popup_time = 0f;
    private float speed_mod = 0;


    private boolean PAUSE = false;

    //    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int score;
    private int TURN = 1;
    private int popupscore = 0;

    private float  curr_speed;
    private String ScoreStr;
    private String jump_time_str;
    private Log log;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;

    /// SOUND ///
    private Sound p1_press_sound;
    private Sound p2_press_sound;


    public TwoPlayerCoopCoinsStateV2(GameStateManager gsm) {
        super(gsm);
        System.out.println("1");

        /// SOUNDS
        p1_press_sound = Gdx.audio.newSound(Gdx.files.internal("Bruh sound effect.mp3"));

//        p2_press_sound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));

        // TEXTURES
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        bg = new Texture("800_bg.png");

        // NUMBERS
        score = 0;
        ScoreStr = "Score: 0";
        System.out.println("2");


        font = new BitmapFont(Gdx.files.internal("ds3.fnt"));
        font.setUseIntegerPositions(false);

        coins = new ArrayList<Coin>();

        coins.add(new Coin(START_GAP, 1, 1));
        System.out.println("3");

        log = new Log("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/","TwoPlayerCoopCoinsStateV2");
        for(int i = 1; i< COIN_COUNT; i++){ //keep a list of coins. do not reposition them, just
//            coins.add(new Coin(i* (COIN_SPACING + Coin.COIN_WIDTH ), 0, 1));
            coins.add(new Coin(START_GAP + i* (COIN_SPACING + Coin.COIN_WIDTH ), (coins.get(i-1)).getPos().y, 2));
        }
        System.out.println("4");

//
        log.log_event(time,Log.START_GAME, 0);


    }

    @Override
    protected void handleInput() { // check if the keys are pressed within 50 ms of eachother

        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
            if(TURN == 1){
                player.jump();
                log.log_event(time, Log.P1_JUMP, player.getPosition().y);
                TURN = 2;
            } else{
                log.log_event(time, Log.P1_OFF_TIME_PRESS, player.getPosition().y);
                if (streak>0){
                    streak--;
                }
            }

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            if(TURN == 2){
                player.jump();
                log.log_event(time, Log.P2_JUMP, player.getPosition().y);
                TURN = 1;
            } else{
                log.log_event(time, Log.P2_OFF_TIME_PRESS, player.getPosition().y);
                streak--;
            }

        }

        if (PAUSE){ // Pausing game
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                PAUSE = false;
            }
        } else{
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
                PAUSE = true;
            }
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){ //Exiting game
            log.log_event(time, Log.END_GAME, 0);
            log.close();
            gsm.set(new MenuState(gsm));
        }

    }

    @Override
    public void update(float dt) {
        handleInput();

        if (!PAUSE){
            time += dt;
            player_pos_cooldown += dt;

            if (score_popup_time > 0f){
                score_popup_time -= dt;
            }

            if (time>GAME_DURATION){
                log.log_event(time, Log.END_GAME, 0);
                log.close();
                gsm.set(new MenuState(gsm));
            }

            if (player_pos_cooldown > player_pos_rate){ // every 50ms, log players position
                log.log_event(time, Log.PLAYER_Y, player.getPosition().y);
                player_pos_cooldown = 0;
            }

            /////// STREAK TOGGLE
            curr_speed = default_player_speed*( 1+ ((float)Math.log((streak+2.))/4 ) );
            player.setSpeed(curr_speed); //move to coin collision event
//            System.out.println(curr_speed);
            player.update(dt );
//            player.update(dt*(1f+(streak/2f));


            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
            //how to reposition thetubes

            for(int i = 0; i< COIN_COUNT; i++){
                Coin coin = coins.get(i);

                if(player.getPosition().x > coin.getPos().x + coin.getCoin().getWidth()){ //if player is to the right of the coin
                    if (!coin.isHit() && !coin.isPassed()){ // and they didnt hit the coin yet
                        log.log_event(time, Log.MISS_COIN, coin.getPos().y); //log the miss
                        streak = 0;
                        coin.setPassed(); // setting this prevents the function from running again

                        float time_from_player = (coin.getCoin().getWidth()/curr_speed);
                        log.log_event(time - time_from_player, Log.COIN_Y, coin.getPos().y);
                    }
                }

                if(cam.position.x - (cam.viewportWidth/2) > coin.getPos().x + coin.getCoin().getWidth()){ //repostition each pipe
                    // normal random reposition
//                    coin.reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT));
                    // reposition closer to last coin
                    coin.close_reposition(coin.getPos().x +((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT),
                            (coins.get(i==0?coins.size()-1:i-1)).getPos().y );
                }

                if(coin.collides(player.getBounds()) ){ // CHECK IF A PALYER TOUCHES COIN
                    float time_from_player = (coin.getPos().x- player.getPosition().x) /curr_speed;
                    log.log_event(time + time_from_player, Log.COIN_Y, coin.getPos().y);
//                    System.out.println(coin.getPos().x);
//                    System.out.println(player.getPosition().x);
//                    System.out.println(curr_speed);
//                    System.out.println(time_from_player);

                    log.log_event(time, Log.HIT_COIN, coin.getPos().y);
                    popupscore = (10 * (streak + 1));
                    score +=+ popupscore;
                    streak++;
//                    speed_mod
                    ScoreStr = "Score: " + score;
                    System.out.println("SCORE");
                    score_popup_time += score_popup_duration; // for displaying added score when hitting coin



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

//        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.setColor(0f, 0f, 0f, 1.0f);

        font.getData().setScale(0.8f);

        String newscore_str = "";
        if (score_popup_time > 0f){ // render the score if recently collided with a coin
            newscore_str = "   + " + popupscore;
        }
        font.draw(sb, ScoreStr + newscore_str, player.getPosition().x-110, 395);
//        System.out.println((jump_time_window - jump_time));
        font.draw(sb, "Time: "+String.format("%.1f", time)+ "   Streak: " + streak, player.getPosition().x-110, 370);

        sb.end(); //close it
    }

    @Override
    public void dispose() {
        bg.dispose();
        player.dispose();
        p1_press_sound.dispose();
//        p2_press_sound.dispose();
        font.dispose();
        for(Coin coin : coins)
            coin.dispose();
        System.out.println("play state disposed");
    }
}
