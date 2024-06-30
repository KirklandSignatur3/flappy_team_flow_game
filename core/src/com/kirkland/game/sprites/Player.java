package com.kirkland.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Player {
    private static float Gravity = -15;
    private static float Speed = 100;
    private Vector3 position;
    private Vector3 prev_position;

    private Vector3 velocity;
//    private Rectangle bounds;
    private Rectangle bounds;
    private Boolean colliding;
    private Texture player1;
    private Texture player2;
    private Texture player_grey;

    private Texture player3;

    public Player(int x, int y){
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        player1 = new Texture("player_red2.png");
        player2 = new Texture("player_blue2.png");
        player3 = new Texture("player_purple.png");
        player_grey = new Texture("player_gray.png");

//        bounds = new Rectangle(x,y,player1.getWidth(), player1.getHeight());
//        bounds = new Rectangle(x,y,player1.getWidth(), player1.getHeight());

        bounds = new Rectangle(x,y,25,25);
//        bounds = new Circle(x,y,10);

    }

    public void update(float dt){
        if (position.y > 0)
            velocity.add(0, Gravity, 0);
        velocity.scl(dt); //scale velocity to time between frames
        prev_position = position;
        position.add(Speed *dt, velocity.y, 0); //x velocity
        if (position.y < 0)
            position.y = 0;
        velocity.scl(1/dt);
        bounds.setPosition(position.x, position.y);

    }
    public int getWidth() {
        return player1.getWidth();
    }
    public int getHeight() {
        return player1.getHeight();
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

    public void setSpeed(float new_speed){
        Speed = new_speed;
    }

    public float getSpeed(){return Speed;}


    public void setGravity(float new_gravity){
        Gravity = new_gravity;
    }
    public float getGravity(){return Gravity;}

    public void setVelocity(Vector3 new_Velocity){ velocity.set(new_Velocity);
    }
    public Vector3 getVelocity(){return velocity;}

    public void jump(){
        velocity.y = 250; // set vel so next it jumps up next frame
    }

    public void player_collision(Player other_player) {
        // if the player collides with another player, flip the y velocity
        // "colliding" makes sure that the velocity part is only changed if the
        // // objects touch for the first time
        Rectangle other_player_bounds = other_player.bounds;
        if (other_player_bounds.overlaps(bounds)){
            if (!colliding){
                //set postion to prev position
                position = prev_position;
                Vector3 other_velocity = other_player.getVelocity();
                Vector3 new_vel = new Vector3(other_velocity.x, other_velocity.y + (velocity.y * 0.8f), 0);
                velocity.set(velocity.x, -(velocity.y * 0.3f), 0);

                other_player.setVelocity(new_vel);
            }
            colliding = true;
        } else{
            colliding = false;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public void dispose(){
        player1.dispose();
        player2.dispose();
        player3.dispose();
    } //player is def as a texture
}
