package com.kirkland.game.sprites;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;


public class Coin {
    // contains a top pipe, a bottom pipe, and a line thru the middle for the score line
    // points are added
    private static final int FLUCTUATION = 300;
    private static final int CLOSE_FLUCTUATION = 250;
    private static final int LOWEST_SPAWN = 35; //
    public static final int COIN_WIDTH = 50;
    private Texture coin;
    private Vector2 pos, posBottomPipe;
    private Rectangle boundsTop, boundsBottom, bounds;
    private Random rand;
    private boolean hit;
    private boolean passed;
    public Coin(float x, float y, int type){
        coin = new Texture("coin.png"); //pipe_top.png
        rand = new Random();
        hit = false; //whether or not the player has hit it yet
        passed = false;

        switch (type){
            case 1:
                pos = new Vector2(x, rand.nextInt(FLUCTUATION) + LOWEST_SPAWN);
            case 2: // close to previous
                float new_y = y + (rand.nextInt(CLOSE_FLUCTUATION) -(CLOSE_FLUCTUATION/2f));
                if (new_y < LOWEST_SPAWN){
                    new_y = LOWEST_SPAWN;
                } else if (new_y > 800) {
                    new_y = 800-COIN_WIDTH - CLOSE_FLUCTUATION/2f;
                }

                pos = new Vector2(x, new_y);
        }

        //the bottom right of the image is where the image spawns
        bounds = new Rectangle(pos.x, pos.y, COIN_WIDTH, COIN_WIDTH);
    }

    public Vector2 getPos() {
        return pos;
    }

    public Boolean isHit() { return  hit;}
    public void setPassed() {passed = true;}
    public Boolean isPassed() { return  passed;}

    public Texture getCoin() {return coin;}
    public void reposition(float x){
        // moves the pipes to the right again, but at a new height
        pos = new Vector2(x, rand.nextInt(FLUCTUATION)  + LOWEST_SPAWN);
        bounds.setPosition(pos.x, pos.y);
        hit = false;
        passed = false;    }
    public void close_reposition(float x, float y){
        // moves the pipes to the right again, but at a new height
        float new_y = y + (rand.nextInt(CLOSE_FLUCTUATION) -(CLOSE_FLUCTUATION/2f) );
        if (new_y < LOWEST_SPAWN){
            new_y = LOWEST_SPAWN;
        } else if (new_y > 400 -COIN_WIDTH) {
            new_y = 400 - CLOSE_FLUCTUATION - COIN_WIDTH;
        }
        pos = new Vector2(x, new_y);
        System.out.println("new y =" + new_y);
        bounds.setPosition(pos.x, pos.y);
        hit = false;
        passed = false;
    }

    public boolean collides(Rectangle player){
        if (player.overlaps(bounds)){
            if (!hit){
                hit = true;
                return true;
            }
        }
        return false;
    }

    public void dispose(){
        coin.dispose();
        //no disposal for the score box bc its just a rectangle
    }
}
