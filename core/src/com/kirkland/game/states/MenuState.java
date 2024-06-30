package com.kirkland.game.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;
import org.apache.commons.csv.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Date;
import com.badlogic.gdx.audio.Sound;

////

import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.labjack.LJUD;
import com.labjack.LJUDException;



public class MenuState extends State{
    private Texture background;
    private Texture syncBG;
    private Sound testSound;
    private Sound blip;
    private float BGSyncTimer = 0f;
    private final float BGSyncDuration = 4f; // split between white and dark
    private float minBGDarkness = 0f; // 0-1f
    private float maxBGDarkness = 1f; // 0-1f
    private float currBGDarkness = minBGDarkness;
    private int gameSelected;
    private Boolean gameStarted = false;



//    private Texture start_button;

    public MenuState (GameStateManager gsm){
        super (gsm);
        background = new Texture("title_screen_big.png");
        syncBG = new Texture("white_bg.png");
//        start_button = new Texture("start_button.png");
        testSound = Gdx.audio.newSound(Gdx.files.internal("Bruh sound effect.mp3"));
        blip = Gdx.audio.newSound(Gdx.files.internal("pongBlip.wav"));
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        cam.position.x = 0;
        gameStarted = false;
        System.out.println(gameStarted);
    }

    @Override
    protected void handleInput() {
        // SWITCH TO SWITCH STATEMENT?
        // 1,2 are old versions that use the pipes. 3,4,5 are
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){//creates a new Play State at the top of the stack
            gsm.set(new TwoPlayerCoopState(gsm)); //pipes alternating game
        } //
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            gsm.set(new TwoPlayerVsState(gsm)); // both players on screen at same time
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){ // for the TwoPlayerCoopCoinsState
            gsm.set(new TwoPlayerCoopCoinsStateV2(gsm)); //
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){ // for the TwoPlayerCoopTimingCoinsState
//            gsm.set(new TwoPlayerCoopTimingCoinsState(gsm)); //
            gsm.set(new TeamFlowPipeTurnsState(gsm));

        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){ // for the TwoPlayerCoopTimingCoins TeamOnlyState
            gameStarted = true;
            gameSelected = 5;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){ // for the TwoPlayerCoopTimingCoins TeamOnlyState
//            gsm.set(new TeamOnlyPipeTurnsState(gsm)); //
            gameStarted = true;
            gameSelected = 6;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
//            testSound.play(1.0f);
            blip.play(1.0f);
            System.out.println("bruh");
        }

        // testing the labjack
        if(Gdx.input.isKeyJustPressed(Input.Keys.L)){
            try {
                int intHandle = 0;
                IntByReference refHandle =
                        new IntByReference(0);

                DoubleByReference refVoltage =
                        new DoubleByReference(0.0);
                //Positive channel = 1 (AIN1)
                int posChannel = 1;
                //Negative channel = 199 (single-ended)
                int negChannel = 199;
                //Range = +/- 10V
                int range = LJUD.Constants.rgBIP10V;
                //Resolution Index = 8 (Effective resolution with
                //                      +/- 10V range = 16 bits)
                int resolutionIndex = 1;
                //Settling = 0 (Auto)
                int settling = 0;

                //Open the first found LabJack U3.
                LJUD.openLabJack(LJUD.Constants.dtU3,
                        LJUD.Constants.ctUSB, "1", 1, refHandle);
                intHandle = refHandle.getValue();

                //Take a measurement from AIN1 and display it.
                LJUD.eDO(intHandle, 7, 1);
                LJUD.eDO(intHandle, 7, 0);
//                LJUD.ePut(intHandle, LJUD.Constants.ioPUT_ANALOG_ENABLE_BIT,0, 2.50, 0);

//                for (int i = 0; i < 16; i++){
//                    System.out.println(i);
//                    LJUD.eDAC(intHandle, i, 2.50,0,0,0);
//                }
//                LJUD.eDAC(intHandle, 0, 2.50,0,0,0);

                System.out.println("AIN" + posChannel + " = " +
                        refVoltage.getValue() + " volts");
            }
            catch(LJUDException le) {
                le.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // tesgint hte csv writng
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            // Test for writing csv files
            LocalDateTime date = java.time.LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_H.m.s");
            String formatted_date = date.format(formatter);
            String outputFile = "C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Gameplay data/bruh_" +formatted_date +".csv";
            try (
                    // Create a FileWriter with the specified file path
                    FileWriter fileWriter = new FileWriter(outputFile);
                    // Create a CSVPrinter with the FileWriter and the CSVFormat
                    CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Header1", "Header2", "Header3"))

            ) {
                // Write data to the CSV file
                csvPrinter.printRecord(1, "Value1B", "Value1C");
                csvPrinter.printRecord("Value2A", 0, 300);

                System.out.println("CSV file written successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        // 1 sec white, 1 s dask
        // syncTimer
        if (gameStarted){ // begin the synb seq before starting the game
            BGSyncTimer += dt;
        }

        if (BGSyncTimer > 5f){
            // set background var ( var used when rendering)
            currBGDarkness = maxBGDarkness; //white
        } if (BGSyncTimer > 7f){
//            gsm.set(new TeamOnlyPipeTurnsState(gsm));
//            gsm.set(new TeamOnlyPipeTurnsState(gsm));
            switch (gameSelected){
                case 5:
                    gsm.set(new TeamFlowPipeTurnsState(gsm));
                case 6:
                    gsm.set(new TeamOnlyPipeTurnsState(gsm));

            }
        }


        cam.position.x = 0;
    }

    @Override
    public void render(SpriteBatch sb) {
        //open, put in, the nclose
        sb.setProjectionMatrix(cam.combined);

        sb.begin();
        //starts drawing at bottom left
//        sb.draw(background,0,0);'
//        sb.setColor(0.5f, 0.5f, 0.5f, 1f);
        if(gameStarted) {
//            System.out.println("syncing the bg render");

            sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 1f); // Sets the background. a=1f because it is solid
            sb.draw(syncBG, 0, 0, 711, 400);
            //        sb.setColor(currBGDarkness, currBGDarkness, currBGDarkness, 0f); // turn off the layer
//            sb.setColor(Color.WHITE);
        }

        if(!gameStarted){
//            System.out.println("redner noraml");
            sb.draw(background, 0, 0, 711, 400);
        }


//        sb.draw(background, cam.position.x - (cam.viewportWidth/2), 0,800,800);
//        sb.draw(start_button , (flappy_game.WIDTH/2) - (start_button.getWidth()/2),(flappy_game.HEIGHT/2) - (start_button.getHeight()/2));
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        testSound.dispose();
        blip.dispose();
//        start_button.dispose();
        System.out.println("menu state disposed");
    }
}
