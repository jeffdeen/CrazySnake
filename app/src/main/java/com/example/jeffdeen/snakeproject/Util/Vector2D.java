package com.example.jeffdeen.snakeproject.Util;

/**
 * Created by jeffdeen on 2017/2/16.
 */

public class Vector2D {
    public double x;
    public double y;
    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double length(){
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }
    public static void Vec2DNormalize(Vector2D vector2D){
        double lth = vector2D.length();
        vector2D.x /= lth;
        vector2D.y /= lth;
    }
    public static int Vec2DSign(Vector2D vector2D1,Vector2D vector2D2){
        if (vector2D1.y*vector2D2.x > vector2D1.x*vector2D2.y)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
    public static double Vec2DDot(Vector2D vector2D1,Vector2D vector2D2){
        return vector2D1.x*vector2D2.x+vector2D1.y*vector2D2.y;
    }
    public static double Vec2DLength(Vector2D vector2D1,Vector2D vector2D2){
        return Math.sqrt(Math.pow(vector2D1.x-vector2D2.x,2)+Math.pow(vector2D1.y-vector2D2.y,2));
    }
}
