package com.kirkland.game.states;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;
public class GameStateManager {
    // manages the states of the game, which state is active
    private Stack<State> states;

    public GameStateManager(){
        states = new Stack<State>();
    }

    public void push(State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(State state){ //pops then pushes
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch Sprites){
        states.peek().render(Sprites);
    }
}