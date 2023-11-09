package com.kirkland.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private static final int GRAVITY = -15;
    private static final int SPEED = 100;
    private Vector3 position;
    private Vector3 velocity;
//    private Rectangle bounds;
    private Rectangle bounds;

    private Texture player1;
    private Texture player2;

    public Player(int x, int y){
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        player1 = new Texture("player_red.png");
        player2 = new Texture("player_blue.png");

        bounds = new Rectangle(x,y,player1.getWidth(), player1.getHeight());
//        bounds = new Circle(x,y,player1.getWidth());
//        bounds = new Circle(x,y,10);

    }

    public void update(float dt){
        if (position.y > 0)
            velocity.add(0, GRAVITY, 0);
        velocity.scl(dt); //scale velocity to time between frames
        position.add(SPEED *dt, velocity.y, 0); //x velocity
        if (position.y < 0)
            position.y = 0;
        velocity.scl(1/dt);
        bounds.setPosition(position.x, position.y);

    }

    public Vector3 getPosition() {
        return position;
    }
    public Texture getTexture1() {
        return player1;
    }
    public Texture getTexture2() {
        return player2;
    }

    public void jump(){
        velocity.y = 250; // set vel so next it jumps up next frame
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void dispose(){
        player1.dispose();
        player2.dispose();
    } //player is def as a texture
}
;