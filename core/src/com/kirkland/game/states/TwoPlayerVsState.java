package com.kirkland.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kirkland.game.flappy_game;
import com.kirkland.game.sprites.Pipe;
import com.kirkland.game.sprites.Player;

import java.util.ArrayList;

public class TwoPlayerVsState extends State{
    public static final int PIPE_SPACING = 125;
    private static final int PIPE_COUNT = 4;
    private Player player_one, player_two;
    private ArrayList<Player> players;
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

    public TwoPlayerVsState(GameStateManager gsm) {
        super(gsm);
        player_one = new Player(50,300);
        player_two = new Player(50,300);
        players = new ArrayList<Player>();
        players.add(player_one);
        players.add(player_two);
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
//      if(Gdx.input.justTouched()){
//            player.jump();
//      }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
            player_one.jump();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)){
            player_two.jump();
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
            for(int j = 0; j<2; j++){
                Player player = players.get(j);
                player.update(dt);
                cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
                //how to reposition thetubes
                for(int i = 0; i<PIPE_COUNT; i++){
                    Pipe pipe = pipes.get(i);
                    if(cam.position.x - (cam.viewportWidth/2) > pipe.getPosTopPipe().x + pipe.getTopPipe().getWidth()){ //repostition each pipe
                        pipe.reposition(pipe.getPosTopPipe().x +((Pipe.PIPE_WIDTH + PIPE_SPACING) * PIPE_COUNT));
                    }

                    if(pipe.collides((player.getBounds()))) { //check if hit pipe
                        gsm.set(new TwoPlayerVsState(gsm));
                    }

                    if(pipe.scores(player.getBounds()) ){
                        //check if pass middle
                        score++;
                        ScoreStr = "Score: " + score;
                        System.out.println("SCOREEEE");
                    }

                }
            }
        }


        cam.update();

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0);//OPEN THE BOX UP

        for(int j = 0; j<2; j++) {
            Player player = players.get(j);
            if (j == 0) {
                sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y);
            } else {
                sb.draw(player.getTexture2(), player.getPosition().x, player.getPosition().y);
            }
            //draw the tubes

        }
        for (Pipe pipe : pipes) {
            sb.draw(pipe.getTopPipe(), pipe.getPosTopPipe().x, pipe.getPosTopPipe().y);
            sb.draw(pipe.getBottomPipe(), pipe.getPosBottomPipe().x, pipe.getPosBottomPipe().y);
        }
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.getData().setScale(2);
        font.draw(sb, ScoreStr, player_one.getPosition().x-50, 350);
        sb.end(); //close it...


    }

    @Override
    public void dispose() {
        bg.dispose();
        player_one.dispose();
        player_two.dispose();
        for(Pipe pipe : pipes)
                pipe.dispose();
        System.out.println("play state disposed");
    }
}
