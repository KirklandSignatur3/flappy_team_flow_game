package com.kirkland.game.sprites;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;


public class Coin {
    // contains a top pipe, a bottom pipe, and a line thru the middle for the score line
    // points are added
    private static final int FLUCTUATION = 350;
    private static final int PIPE_GAP = 100; //gap between top and bottom
    private static final int LOWEST_SPAWN = 50; //
    public static final int COIN_WIDTH = 50;
    private Texture coin;
    private Vector2 pos, posBottomPipe;
    private Rectangle boundsTop, boundsBottom, bounds;
    private Random rand;
    private boolean hit;
    public Coin(float x){
        coin = new Texture("coin.png"); //pipe_top.png
        rand = new Random();
        hit = false; //whether or not the player has hit it yet

        pos = new Vector2(x, rand.nextInt(FLUCTUATION) + LOWEST_SPAWN);
        //the bottom right of the image is where the image spawns
        bounds = new Rectangle(pos.x, pos.y, COIN_WIDTH, COIN_WIDTH);
    }

    public Vector2 getPos() {
        return pos;
    }

    public Boolean isHit() { return  hit;}
    public Texture getCoin() {return coin;}
    public void reposition(float x){
        // moves the pipes to the right again, but at a new height
        pos = new Vector2(x, rand.nextInt(FLUCTUATION)  + LOWEST_SPAWN);
        bounds.setPosition(pos.x, pos.y);
        hit = false;
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
