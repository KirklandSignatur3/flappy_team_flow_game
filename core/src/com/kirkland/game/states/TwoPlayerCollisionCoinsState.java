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

public class TwoPlayerCollisionCoinsState extends State{
    public final int COIN_SPACING = 125;
    private static final int COIN_COUNT = 4;

    private Player player_one, player_two;
    private ArrayList<Player> players;
    private Texture bg;
    private int streak = 0;
    private int speed_mod = 0;

    private int TURN = 1;
    private boolean PAUSE = false;

//    private Array<Pipe> pipes;
    private ArrayList<Coin> coins;
    private int p1_score;
    private int p2_score;
    private String ScoreStr;
    BitmapFont font;
//    private FreeTypeFontGenerator fontGenerator;



    public TwoPlayerCollisionCoinsState(GameStateManager gsm) {
        super(gsm);
        player_one = new Player(50,300);
        player_two = new Player(50,200);
        players = new ArrayList<Player>();
        players.add(player_one);
        players.add(player_two);
        cam.setToOrtho(false, flappy_game.WIDTH/2f, flappy_game.HEIGHT/2f);
        bg = new Texture("800_bg.png");
        p1_score = 0;
        p2_score = 0;
        ScoreStr = "Score: 0";

        font = new BitmapFont();
        font.setUseIntegerPositions(false);

        coins = new ArrayList<Coin>();

        for(int i = 0; i< COIN_COUNT; i++){ //keep a list of coins. do not reposition them, just
            coins.add(new Coin(i* (COIN_SPACING + Coin.COIN_WIDTH ),0,1) );
        }

    }

    @Override
    protected void handleInput() {

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
                player_one.player_collision(player_two);
                player_two.player_collision(player_one);
                player.update(dt);
                cam.position.x = player.getPosition().x+80; //offset cam a bit in front of player
                //how to reposition thetubes
                for(int i = 0; i< COIN_COUNT; i++) {
                    Coin coin = coins.get(i);
                    if (cam.position.x - (cam.viewportWidth / 2) > coin.getPos().x + coin.getCoin().getWidth()) { //repostition each pipe
                        // if
                        coin.reposition(coin.getPos().x + ((Pipe.PIPE_WIDTH + COIN_SPACING) * COIN_COUNT));
                    }

                    if (coin.collides(player.getBounds())) { // CHECK IF A PALYER TOUCHES COIN
                        if (j==0){
                            p1_score +=1;
                        } else {
                            p2_score +=1;
                        }
                        streak++;
                        ScoreStr = "P1 Score: " + p1_score +"    P2 Score: " + p2_score;
                        System.out.println("SCOREEEE");

                    }
                }
                //still in platers loop




            }


            cam.update();
        }


    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0);//OPEN THE BOX UP
        // draw the players
        for(int j = 0; j<2; j++) {
            Player player = players.get(j);
            if (j == 0) {
                sb.draw(player.getTexture1(), player.getPosition().x, player.getPosition().y);
            } else {
                sb.draw(player.getTexture2(), player.getPosition().x, player.getPosition().y);
            }
        }
        //draw the coins
        for(Coin coin: coins){
            if (!coin.isHit()){
                sb.draw(coin.getCoin(), coin.getPos().x, coin.getPos().y);
            }
        }

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.getData().setScale(2);
        font.draw(sb, ScoreStr, player_one.getPosition().x-100, 400);

        sb.end(); //close it...


    }

    @Override
    public void dispose() {
        bg.dispose();
        player_one.dispose();
        player_two.dispose();

        for(Coin coin : coins)
            coin.dispose();
        System.out.println("play state disposed");
    }
}
