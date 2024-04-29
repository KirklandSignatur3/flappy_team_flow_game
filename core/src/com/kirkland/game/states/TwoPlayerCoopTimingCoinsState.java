package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
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

public class TwoPlayerCoopTimingCoinsState extends State{
    public final int COIN_SPACING = 75;// original : 125
    public final int START_GAP = 300;// 125
    private static final int COIN_COUNT = 8; //4 //4*125 = 500

    private static final float GAME_DURATION = 180; // 180 = 3 minutes
    private float time = 0;

    private int bg_change_streak = 5;

    private float player_pos_cooldown  = 0; // timer for recording player pos
    private final float player_pos_rate  = .1f; // records player postion every 50 ms (
    private int default_player_speed = 100;
    private Player player;
    private Texture bg;
    private int streak = 0;
    private float speed_mod = 0;
    private float score_popup_duration = 0.7f;
    private float score_popup_time = 0f;
    private int popupscore = 0;

    private boolean one_pressed = false;
    private boolean two_pressed = false;
    private float jump_time = 0f;
    private float jump_time_window = 0.1f;

    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int score;
    private float  curr_speed;
    private String ScoreStr;
    private String jump_time_str;
    private Log log;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;

    /// SOUND ///
    private Sound p1_press_sound;
    private Sound p2_press_sound;
    private Sound blip;

    private int curr_bg = 0;
    private float minBGDarkness = 0.1f; // 0-255

    private float maxBGDarkness = 1f; // 0-255

    private float BGChangeRate = 0.01f; // 0-1
    private float currBGDarkness = minBGDarkness; // 0-255



    public TwoPlayerCoopTimingCoinsState(GameStateManager gsm) {
        super(gsm);
        System.out.println("1");

        /// SOUNDS
//        p1_press_sound = Gdx.audio.newSound(Gdx.files.internal("Bruh sound effect.mp3"));
//        p2_press_sound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));
        blip = Gdx.audio.newSound(Gdx.files.internal("bounceTwo.wav"));

        // TEXTURES
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        bg = new Texture("white_bg.png");
//        bg = new Texture("800_bg_gray.png");

        score_popup_duration = ((COIN_SPACING/ player.getSpeed())/2) ;
        // NUMBERS
        score = 0;
        ScoreStr = "Score: 0";
        System.out.println("2");

        font = new BitmapFont(Gdx.files.internal("commodore_22.fnt"));
        font.setUseIntegerPositions(false);
//        font.setColor(0f, 0f, 0f, 1.0f);
        font.setColor(1f, 1f, 1f, 1.0f);

        coins = new ArrayList<Coin>();

        coins.add(new Coin(START_GAP, 1, 1));
        System.out.println("3");

        log = new Log("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/","TwoPlayerCoopTimingCoinsState");
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){ //check if p1
            blip.play(0.5f);
            if(!one_pressed && !two_pressed){
                one_pressed = true;
            }else if (one_pressed){ //p1 alr pressed
                log.log_event(time, Log.P1_OFF_TIME_PRESS, player.getPosition().y);
                if (streak>0){ streak--; }
//                one_pressed = true;
            } else if (two_pressed){
                log.log_event(time, Log.P1_JUMP, player.getPosition().y);
                one_pressed = false;
                two_pressed = false;
                player.jump();
                jump_time = 0;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            blip.play(0.5f, 2.0f, 0);
            if(!one_pressed && !two_pressed){
                two_pressed = true;
            }else if(two_pressed){ //p2 alr pressed
                log.log_event(time, Log.P2_OFF_TIME_PRESS, player.getPosition().y);
                if (streak>0){ streak--; }
//                two_pressed = true;
            } else if (one_pressed){
                log.log_event(time, Log.P2_JUMP, player.getPosition().y);
                one_pressed = false;
                two_pressed = false;
                player.jump();
                jump_time = 0;
            }
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
            log.log_event(time, Log.END_GAME, 0);
            log.close();
            gsm.set(new MenuState(gsm));
            return;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){ bg.dispose(); bg = new Texture("white_bg.png");
            font.setColor(0f, 0f, 0f, 1.0f);}
        if(Gdx.input.isKeyJustPressed(Input.Keys.X)){ bg.dispose(); bg = new Texture("black_bg.png");
            font.setColor(1f, 1f, 1f, 1.0f);}
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){ bg.dispose(); bg = new Texture("800_bg.png");
            font.setColor(1f, 1f, 1f, 1.0f);}
        if(Gdx.input.isKeyJustPressed(Input.Keys.V)){ bg.dispose(); bg = new Texture("800_bg_gray.png");
            font.setColor(1f, 1f, 1f, 1.0f);}

    }

    @Override
    public void update(float dt) {

        handleInput();
        if (time > GAME_DURATION){
            log.log_event(time, Log.END_GAME, 0);
            log.close();
            gsm.set(new MenuState(gsm));
            return;
        }
        if (!PAUSE){
            time += dt;

            /// LOGGING PLAYER POSITION
            player_pos_cooldown += dt;
            if (score_popup_time > 0f){
                score_popup_time -= dt;
            }
            if (player_pos_cooldown > player_pos_rate){ // every 50ms, log players position
                log.log_event(time, Log.PLAYER_Y, player.getPosition().y);
                player_pos_cooldown = 0;
            }

            // JUMPING TIMING
            if(one_pressed || two_pressed){ // if either player presses, then start timer
                jump_time += dt;
            }
            if(jump_time > jump_time_window){ //reset the presses if the other button is not pressed in time
                if (one_pressed){
                    log.log_event(time-jump_time,Log.P1_OFF_TIME_PRESS, (float) (player.getPosition().y - player.getVelocity().y * jump_time + 0.5 * (player.getGravity()) * Math.pow(jump_time, 2f)) );
                    one_pressed = false;
                    if (streak>0){ streak--; }
                } else if (two_pressed){
                    log.log_event(time-jump_time,Log.P2_OFF_TIME_PRESS, (float) (player.getPosition().y - player.getVelocity().y * jump_time + 0.5 * (player.getGravity()) * Math.pow(jump_time, 2f)) );
                    two_pressed = false;
                    if (streak>0){ streak--; }
                }
                jump_time = 0;
            }

            /////// STREAK TOGGLE
            // turn speed toggle on and
            curr_speed = default_player_speed*( 1+ ((float)Math.log((streak+2.))/4 ) );
            player.setSpeed(curr_speed); //move to coin collision event
//            System.out.println(curr_speed);
            player.update(dt );
//            player.update(dt*(1f+(streak/2f));
            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player

            /// UPDATING THE COINS, COLLISIONS WITH COINS
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
                    coin.close_reposition(coin.getPos().x +((Coin.COIN_WIDTH + COIN_SPACING) * COIN_COUNT),
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
                    if ( (streak % bg_change_streak == 0) && (streak != 0) ){
                        if(curr_bg == 0){ // if bg is white
//                            bg.dispose(); bg = new Texture("black_bg.png");
//                            font.setColor(1f, 1f, 1f, 1.0f);
                            curr_bg = 1 ;
                            log.log_event(time, Log.BG_CHANGE_BLACK,0);
                        } else{
//                            bg.dispose(); bg = new Texture("white_bg.png");
//                            font.setColor(0f, 0f, 0f, 1.0f);
                            log.log_event(time, Log.BG_CHANGE_WHITE,0);
                            curr_bg = 0;
                        }

                    }
                    System.out.println("SCORE");
                    score_popup_time += score_popup_duration;
                }



            }
            /// UPDATING BACKGROUND COLOR
            // if 0, then subtract from currbgdarkness if > min. if 1, add unwil cbgm > max
            if (curr_bg ==1) { // to black
                if (currBGDarkness > minBGDarkness){
                    currBGDarkness -= (BGChangeRate);
                }
            } else if (curr_bg == 0) { //to white
                if (currBGDarkness < maxBGDarkness){
                    currBGDarkness += (BGChangeRate);
                }
            }


            cam.update();
        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        //draw the background
        sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 1f); // Sets the background. a=1f because it is solid
        font.setColor(1f-currBGDarkness, 1f-currBGDarkness, 1f-currBGDarkness, 1f);
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0, 800, 800);

//        sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 0f); // turn off the layer
        sb.setColor(Color.WHITE);

        //draw the player

        sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y);
        //draw the coins
        for(Coin coin: coins){
            if (!coin.isHit()){
                sb.draw(coin.getCoin(), coin.getPos().x, coin.getPos().y);
            }
        }
        font.getData().setScale(0.8f);
        jump_time_str = "Time: " + (jump_time_window - jump_time);
        ScoreStr = "Pts: " + score;

        if (score_popup_time > 0f){ // render the score if recently collided with a coin
//            newscore_str = "   + " + popupscore;
            font.draw(sb, ("+" + popupscore), player.getPosition().x+player.getWidth(), player.getPosition().y+ 2*player.getHeight());
        }

        font.draw(sb, "Time: "+String.format("%.1f", time), player.getPosition().x-110, 370);
//        System.out.println((jump_time_window - jump_time));
        font.draw(sb, ScoreStr+ " Stk: " + streak, player.getPosition().x-110, 395);
        sb.end(); //close it...


    }

    @Override
    public void dispose() {
        cam.position.x = 0;
        bg.dispose();
        player.dispose();

        blip.dispose();
//        p2_press_sound.dispose();
        font.dispose();
        for(Coin coin : coins)
            coin.dispose();
        System.out.println("TwoPlayerCoopTimingCoinsState state disposed");
    }
}
