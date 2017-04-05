package com.example.jeffdeen.snakeproject.GameObjects;

import com.example.jeffdeen.snakeproject.GameView;
import com.example.jeffdeen.snakeproject.Util.CParams;


/**
 * Created by jeffdeen on 2017/2/12.
 */

public class SnakeBody extends BaseObject{
    private int m_iDirection = 3;
    public SnakeBody(float x, float y, float r, float g, float b, GameView mv) {
        super(x, y, r, g, b, mv);
        setRadius(50);
    }
    public void Move(){
        switch (m_iDirection){
            case 0:
                Up();
                break;
            case 1:
                Down();
                break;
            case 2:
                Left();
                break;
            case 3:
                Right();
                break;
        }
    }

    public int getM_iDirection() {
        return m_iDirection;
    }

    public void setM_iDirection(int m_iDirection) {
        this.m_iDirection = m_iDirection;
    }

    public void Up(){
        setY(getY()+1);
    }
    public void Down(){
        setY(getY()-1);
    }
    public void Left(){
        setX(getX()- CParams.ratio);
    }
    public void Right(){
        setX(getX()+CParams.ratio);
    }
}
