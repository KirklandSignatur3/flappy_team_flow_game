
package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;
import com.kirkland.game.sprites.Log;
import com.kirkland.game.sprites.Pipe;
import com.kirkland.game.sprites.Player;

import java.util.ArrayList;
import java.util.Random;


public class TeamOnlyPipeTurnsState extends State{
    private Random rand;
    public final int PIPE_SPACING = 90;// original : 125, 100
    public final int START_GAP = 300;// 125
    /// other pipe params like the gap height are changed in the pipe class
    private static final int PIPE_COUNT = 8; //4 //4*125 = 500
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
//    pricate float noJumpChance = .3f;
    private boolean PAUSE = false;

    //    private Array<Pipe> pipes;
    private ArrayList<Pipe> pipes;
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

/// background ///
    private int curr_bg = 0; // starting background, var decides the bg
    private float minBGDarkness = 0.3f; // 0-1f
    private float maxBGDarkness = 0.7f; // 0-1f
    private float BGChangeRate = 1f; // 0-1f
    private float currBGDarkness = minBGDarkness; // Starting darkness
    private float BGChangeTimer = 0f; // do not change
    private float BGChangeTimeFluctuation = 2f;
    private float BGChangeTimeBase = 5f;
//    private float BGChangeFrequency = BGChangeTimeBase + rand.nextFloat(BGChangeTimeFluctuation);
    private float BGChangeFrequency = 0f; // will be set with random number when class is instated


    private int TURN = 1;




    public TeamOnlyPipeTurnsState(GameStateManager gsm) {
        super(gsm);


        System.out.println("1");



        rand = new Random();
        /// SOUNDS
//        p1_press_sound = Gdx.audio.newSound(Gdx.files.internal("Bruh sound effect.mp3"));
//        p2_press_sound = Gdx.audio.newSound(Gdx.files.internal("sound.mp3"));
        blip = Gdx.audio.newSound(Gdx.files.internal("bounceTwo.wav"));
        BGChangeFrequency = BGChangeTimeBase + (rand.nextFloat() * BGChangeTimeFluctuation);
        // TEXTURES
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        bg = new Texture("white_bg.png");

//        bg = new Texture("800_bg_gray.png");

        score_popup_duration = ((PIPE_SPACING/ player.getSpeed())/2) ;
        // NUMBERS
        score = 0;
        ScoreStr = "Score: 0";
        System.out.println("2");

        font = new BitmapFont(Gdx.files.internal("commodore_22.fnt"));
        font.setUseIntegerPositions(false);
//        font.setColor(0f, 0f, 0f, 1.0f);
        font.setColor(1f, 1f, 1f, 1.0f);

        System.out.println("3");

        log = new Log("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/","TeamOnlyPipeTurnsState");

        pipes = new ArrayList<Pipe>();
        pipes.add(new Pipe(START_GAP,1,140, 150, 50));
//        pipes.get(0).set_difficulty(1,140, 150, 50);
        for(int i = 1; i<PIPE_COUNT; i++){
            pipes.add(new Pipe(START_GAP+ i* (PIPE_SPACING + Pipe.PIPE_WIDTH),1,140, 150, 50 ))            ;
//            pipes.get(i).set_difficulty(1,140, 150, 50);
        }
        System.out.println("4");
        log.log_event(time,Log.START_GAME, 0);
        curr_bg = 1;



    }

    @Override
    protected void handleInput() { // check if the keys are pressed within 50 ms of eachother

        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
            blip.play(0.5f, 0.5f,0);
            if(TURN==1){
//                log.log_event(time, Log.P1_JUMP, player.getPosition().y);
//                if(Math.random()>noJumpChance){ // log no jump?
//                    player.jump();
//                }
                player.jump();
                TURN = 2;
            } else {
                log.log_event(time, Log.P1_OFF_TIME_PRESS, player.getPosition().y);
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            blip.play(0.5f, 1f,0);
            if(TURN==2){
                log.log_event(time, Log.P2_JUMP, player.getPosition().y);
//                if(Math.random()>no_jump_chance){ // log no jump?
//                    player.jump();
//                }
                player.jump();
                TURN = 1;
            } else {
                log.log_event(time, Log.P2_OFF_TIME_PRESS, player.getPosition().y);
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
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)){ curr_bg = 0;}
        if(Gdx.input.isKeyJustPressed(Input.Keys.X)){ curr_bg = 1;}

    }
//    public void pupil_sync(float dt){
//        curr_bg = 1;
//        log.log_event(time, Log.BG_CHANGE_BLACK, 0);
//        curr_bg = 0 ;
//        log.log_event(time, Log.BG_CHANGE_WHITE,0);
//    }
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
            //            curr_speed = default_player_speed*( 1+ ((float)Math.log((streak+2.))/4 ) );
            curr_speed = default_player_speed;
            player.setSpeed(curr_speed); //move to pipe collision event
//            System.out.println(curr_speed);
            player.update(dt );
//            player.update(dt*(1f+(streak/2f));
            cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player

            /// UPDATING THE COINS, COLLISIONS WITH COINS
            for(int i = 0; i< PIPE_COUNT; i++){
                Pipe pipe = pipes.get(i);
                //if player is to the right of the pipe (passes it)
                if(player.getPosition().x > pipe.getPosBottomPipe().x + pipe.getBottomPipe().getWidth()){
                    if (!pipe.isHit() && !pipe.isPassed()){ // and they didnt hit the pipe
                        popupscore = (10 * (streak + 1));
                        score += popupscore;
                        streak++;
                        score_popup_time += score_popup_duration;
      /// /// /// /// /// Change the background for getting 5 in a row. ////
//                        if ( (streak % bg_change_streak == 0) && (streak != 0) ){
//                            if(curr_bg == 0){ // if bg is white
////                            bg.dispose(); bg = new Texture("black_bg.png");
////                            font.setColor(1f, 1f, 1f, 1.0f);
//                                curr_bg = 1 ;
//                                log.log_event(time, Log.BG_CHANGE_BLACK,0);
//                            } else{
////                            bg.dispose(); bg = new Texture("white_bg.png");
////                            font.setColor(0f, 0f, 0f, 1.0f);
//                                log.log_event(time, Log.BG_CHANGE_WHITE,0);
//                                curr_bg = 0;
//                            }
//                        }
                        log.log_event(time, Log.PASS_PIPE, pipe.getPosBottomPipe().y); //log the miss
//                        streak = 0;
                        pipe.setPassed(); // setting this prevents the function from running again

                    }
                }

                if(cam.position.x - (cam.viewportWidth/2) > pipe.getPosBottomPipe().x + pipe.getBottomPipe().getWidth()){ //repostition each pipe
                    // normal random reposition
                    pipe.reposition(pipe.getPosBottomPipe().x + ((Pipe.PIPE_WIDTH + PIPE_SPACING) * PIPE_COUNT));
                    // reposition closer to last pipe
//                    pipe.close_reposition(pipe.getPos().x +((Coin.COIN_WIDTH + COIN_SPACING) * COIN_COUNT),
//                            (pipes.get(i==0?pipes.size()-1:i-1)).getPos().y );
                }


                // CHECK IF A PALYER TOUCHES pipe
                if(!pipe.isHit()){
                    if(pipe.collides(player.getBounds()) ){
//                        System.out.println("ouwch  :(");

                        float time_from_player = (pipe.getPosBottomPipe().x- player.getPosition().x) /curr_speed;
                        log.log_event(time + time_from_player, Log.COIN_Y, pipe.getPosBottomPipe().y);
    //                    System.out.println(pipe.getPos().x);
                        // pause the screen and show a game over?
                        // play a sound
                        log.log_event(time, Log.HIT_PIPE, pipe.getPosBottomPipe().y);
//                        System.out.println("ouwch  :(");
                        score_popup_time += score_popup_duration;
                        popupscore = -10; // make red?
                        streak = 0;

                    }
                }


            }




            /// UPDATING BACKGROUND COLOR
            // if 0, then subtract from currbgdarkness if > min. if 1, add unwil cbgm > max
            if (curr_bg == 1) { // to black
                currBGDarkness = maxBGDarkness;
//                if (currBGDarkness > minBGDarkness){
//                    currBGDarkness -= (BGChangeRate);
//                }
            } else if (curr_bg == 0) { // to black
                currBGDarkness = minBGDarkness;
//                if (currBGDarkness < maxBGDarkness){
//                    currBGDarkness += (BGChangeRate);
//                }
            }
            BGChangeTimer += dt;
            if (BGChangeTimer > BGChangeFrequency){
                BGChangeTimer = 0;
                if(curr_bg == 0) {
                    curr_bg = 1;
                    log.log_event(time, Log.BG_CHANGE_WHITE, 0);
                }else if (curr_bg == 1) {
                    curr_bg = 0 ;
                    log.log_event(time, Log.BG_CHANGE_BLACK,0);
                }
//              BGChangeFrequency = BGChangeTimeBase + (rand.nextFloat() * (2*BGChangeTimeFluctuation) - BGChangeTimeFluctuation);
                BGChangeFrequency = BGChangeTimeBase + (rand.nextFloat() * BGChangeTimeFluctuation);

                // -BGChangeTimeFluctuation,
            }

            cam.update();




        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        font.setColor(1f, 1f, 1f, 1f);


        //draw the background
        sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 1f); // Sets the background. a=1f because it is solid
//        font.setColor(1f-currBGDarkness, 1f-currBGDarkness, 1f-currBGDarkness, 1f);
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0, 800, 800);
//        sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 0f); // turn off the layer
        sb.setColor(Color.WHITE);

        ///draw the player
        if(TURN == 1 ){
            sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y,30,30);
        } else {
            sb.draw(player.getTexture2(), player.getPosition().x, player.getPosition().y,30,30);
        }

        /// draw the pipes
        for(Pipe pipe: pipes){
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBottomPipe().x, pipe.getPosBottomPipe().y);
        }

        font.getData().setScale(0.8f);
        jump_time_str = "Time: " + (jump_time_window - jump_time);



        /// draw the score
        if (score_popup_time > 0f){ // render the score if recently collided with a pipe
//            newscore_str = "   + " + popupscore;
            if (popupscore < 0){
                font.setColor(1f, 0f, 0f, 1f);
                font.draw(sb, ("X"), player.getPosition().x+player.getWidth()*3, player.getPosition().y+ (2.5f *player.getHeight()) );
                font.setColor(Color.WHITE);
            } else {
                font.draw(sb, ("+" + popupscore), player.getPosition().x+player.getWidth()*2, player.getPosition().y+ (2.5f *player.getHeight()) );
            }
             // Sets the background. a=1f because it is solid



        }


        ScoreStr = "Pts: " + score +"  ";
        font.draw(sb, "Time: "+String.format("%.1f", time), player.getPosition().x-250, 370); //-110
//        System.out.println((jump_time_window - jump_time));
        font.draw(sb, ScoreStr+ " Stk: " + streak, player.getPosition().x-250, 395);
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
        for(Pipe pipe : pipes)
            pipe.dispose();
        System.out.println("TwoPlayerCoopTimingCoinsState state disposed");
    }

}
