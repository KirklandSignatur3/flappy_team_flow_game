package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.kirkland.game.flappy_game;
import com.kirkland.game.sprites.Pipe;
import com.kirkland.game.sprites.Player;

import java.util.ArrayList;

public class TwoPlayerCoopState extends State{
    public static final int PIPE_SPACING = 125;
    private static final int PIPE_COUNT = 4;
    private Player player;
    private Texture bg;
    private int TURN = 1;
    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Pipe> pipes;
    private int score;
    private String ScoreStr;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;



//    private Texture player;

    public TwoPlayerCoopState(GameStateManager gsm) {
        super(gsm);
        player = new Player(50,300);
        cam.setToOrtho(false, flappy_game.WIDTH/2, flappy_game.HEIGHT/2);
        bg = new Texture("800_bg.png");
        score = 0;
        ScoreStr = "Score: 0";
//        FileHandle fontFile = Gdx.files.internal("C:/Users/maxwe/Documents/SCHOOL/SURF/Team flow/Games/flappy_game/assets/3-bit/3-bit.ttf");
//        FileHandle fontFile = Gdx.files.internal("C:\\Users\\maxwe\\Documents\\SCHOOL\\SURF\\Team flow\\Games\\flappy_game\\assets\\vertopal.com_3-bit");
        font = new BitmapFont();
        font.setUseIntegerPositions(false);

        pipes = new ArrayList<Pipe>();

        for(int i = 0; i<PIPE_COUNT; i++){
            pipes.add(new Pipe(i* (PIPE_SPACING + Pipe.PIPE_WIDTH )));
        }

    }

    @Override
    protected void handleInput() {

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
            for(int i = 0; i<PIPE_COUNT; i++){
                Pipe pipe = pipes.get(i);
                if(cam.position.x - (cam.viewportWidth/2) > pipe.getPosTopPipe().x + pipe.getTopPipe().getWidth()){ //repostition each pipe
                    pipe.reposition(pipe.getPosTopPipe().x +((Pipe.PIPE_WIDTH + PIPE_SPACING) * PIPE_COUNT));
                }

                if(pipe.collides((player.getBounds()))) { //check if hit pipe
                    gsm.set(new TwoPlayerCoopState(gsm));
                }

                if(pipe.scores(player.getBounds()) ){
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
        //draw the tubes
        for(Pipe pipe: pipes){
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBottomPipe().x, pipe.getPosBottomPipe().y);
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
        for(Pipe pipe : pipes)
                pipe.dispose();
        System.out.println("play state disposed");
    }
}
