package com.kirkland.game.sprites;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Circle;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.w3c.dom.css.Rect;

import java.util.Random;


public class Pipe {
    // contains a top pipe, a bottom pipe, and a line thru the middle for the score line
    // points are added
    private static int FLUCTUATION = 130;
    private static int PIPE_GAP = 140; //gap between top and bottom
    private static int LOWEST_SPAWN = 90; //
    public static int PIPE_WIDTH = 50;
    private Texture topPipe, bottomPipe;
    private Vector2 posTopPipe, posBottomPipe;
    private Rectangle boundsTop, boundsBottom, boundsScore;
    private Random rand;
    private boolean hit = false;
    private boolean passed;

    public Pipe(float x, int fluctuation, int gap, int lowestSpawn,int width){
        FLUCTUATION = fluctuation;
        PIPE_GAP = gap; //gap between top and bottom
        LOWEST_SPAWN = lowestSpawn; //
        PIPE_WIDTH = width;
        topPipe = new Texture("pipe_top.png"); //pipe_top.png
        bottomPipe = new Texture("pipe_bottom.png"); //pipe_bottom.png
        rand = new Random();
        hit = false;
        passed = false;

        posTopPipe = new Vector2(x, rand.nextInt(FLUCTUATION) + PIPE_GAP + LOWEST_SPAWN);
        posBottomPipe = new Vector2(x, posTopPipe.y - PIPE_GAP - bottomPipe.getHeight()); /* usually spawns images such that :3
        the bottom right of the image is where the image spawns */
        boundsTop = new Rectangle(posTopPipe.x, posTopPipe.y, topPipe.getWidth(), topPipe.getHeight());
        boundsBottom = new Rectangle(posBottomPipe.x, posBottomPipe.y, bottomPipe.getWidth(), bottomPipe.getHeight());
        boundsScore = new Rectangle(posTopPipe.x+topPipe.getWidth(), posTopPipe.y-PIPE_GAP, 2, PIPE_GAP); // is in the

    }

    public void set_difficulty(int fluctuation, int gap, int lowestSpawn,int width){
        FLUCTUATION = fluctuation;
        PIPE_GAP = gap; //gap between top and bottom
        LOWEST_SPAWN = lowestSpawn; //
        PIPE_WIDTH = width;
    }

    public Texture getTopPipe() {
        return topPipe;
    } //returns the image used for rendering
    public Texture getBottomPipe() {
        return bottomPipe;
    }
    public Vector2 getPosTopPipe() {
        return posTopPipe;
    }
    public Vector2 getPosBottomPipe() {return posBottomPipe;}



    public void reposition(float x){
        // moves the pipes to the right again, but at a new height
        posTopPipe = new Vector2(x, rand.nextInt(FLUCTUATION) + PIPE_GAP + LOWEST_SPAWN);
        posBottomPipe = new Vector2(x, posTopPipe.y - PIPE_GAP - bottomPipe.getHeight()); //usualyy spawns images such that
        boundsTop.setPosition(posTopPipe.x, posTopPipe.y);
        boundsBottom.setPosition(posBottomPipe.x, posBottomPipe.y);
        boundsScore.setPosition(posTopPipe.x, posTopPipe.y-PIPE_GAP);
        hit = false;
        passed = false;
    }
    public boolean collides(Rectangle player){
        boolean result = player.overlaps(boundsTop) || player.overlaps(boundsBottom);
        if (result){
            hit = true;
        }
        return result;
    }
    public boolean scores(Rectangle player){
        if (player.overlaps(boundsScore)){
            if (!hit){
                passed = true;
                return true;
            }
        }
        return false;
    }
    public Boolean isHit() { return  hit;}
    public void setPassed() {passed = true;}
    public Boolean isPassed() {return  passed;}
//    public Vector2 getPos() {
//        return posBottomPipe;
//    }


    public void dispose(){
        topPipe.dispose();
        bottomPipe.dispose();
        //no disposal for the score box bc its just a rectangle
    }
}
