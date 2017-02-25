package com.example.jeffdeen.snakeproject.GameObjects;

import com.example.jeffdeen.snakeproject.GameView;

/**
 * Created by jeffdeen on 2017/2/12.
 */

public class SnakeBody extends BaseObject{

    public SnakeBody(int x, int y, float r, float g, float b, GameView mv) {
        super(x, y, r, g, b, mv);
        setRadius(50);
    }
}
