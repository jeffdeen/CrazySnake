package com.example.jeffdeen.snakeproject.GameObjects;

import android.util.Log;

import com.example.jeffdeen.snakeproject.GameView;
import com.example.jeffdeen.snakeproject.Util.CParams;
import com.example.jeffdeen.snakeproject.Util.Vector2D;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jeffdeen on 2017/2/12.
 */

public class Snake {
    public List<SnakeBody> snakeBodyList = new ArrayList<>();
    private GameView view;

    private float r;
    private float g;
    private float b;
    private int total_length = 20;
    private int m_iClosest_food = 0;
    private int m_iClosest_obstacle = 0;
    private int temp_length = 0;
    private GameView gameView;
    private final int UP_FLAG = 0;
    private final int DOWN_FLAG = 1;
    private final int LEFT_FLAG = 2;
    private final int RIGHT_FLAG = 3;

    //private AI_result avoidObstacle2;
    public Snake(int x, int y, float r, float g, float b, GameView view){
        this.view = view;
        this.r = r;
        this.g = g;
        this.b = b;
        gameView = view;
        snakeBodyList.add(new SnakeBody(x,y,r,g,b, view));
        snakeBodyList.add(generateBody(snakeBodyList.get(0),snakeBodyList.get(0)));
        addBody();
        addBody();
        addBody();
    }

    private SnakeBody generateBody(BaseObject object1, BaseObject object2){
        if(object1.getX()==object2.getX() && object1.getY()==object2.getY()){
            return new SnakeBody(object1.getX()-100,object2.getY(),
                    object1.getColor()[0],object1.getColor()[1],object1.getColor()[2],view);
        }else{
            float x = object2.getX()-object1.getX()+object2.getX();
            float y = object2.getY()-object1.getY()+object2.getY();
            Log.d("YValue",""+(object2.getY()-object1.getY()));
            return new SnakeBody(x,y,object1.getColor()[0],object1.getColor()[1],object1.getColor()[2],view);
        }

    }
    public void addBody(){
        int length = length();
        snakeBodyList.add(generateBody(snakeBodyList.get(length-2),snakeBodyList.get(length-1)));
    }

    public void drawSnake(float[] _MVPMatrix){
        for(int i =0;i<snakeBodyList.size();++i){
            snakeBodyList.get(i).draw(_MVPMatrix);
        }
    }

    public boolean Update(List<BaseObject> foods,List<BaseObject> obstacles){
        //++m_dFitness;
        //更新方向向量

        //get vector to closest mine
        BaseObject vClosestFood = GetClosestFoods(foods);
        //foods相对坐标方向
        Vector2D vClosestFoodCord = new Vector2D(vClosestFood.getX() - getHead().getX(),
                vClosestFood.getY() - getHead().getY());
        double temp_distance2 = Math.sqrt(Math.pow(vClosestFoodCord.x,2) +
                Math.pow(vClosestFoodCord.y,2));
        //遇到foods
        if(temp_distance2<=80){
            ++total_length;
            if(vClosestFood.getRadius()==30){
                addBody();
                total_length+=10;
                foods.remove(m_iClosest_food);
            }else{
                ++temp_length;
                if(temp_length==10){
                    addBody();
                    temp_length = 0;
                }
                foods.get(m_iClosest_food).setCoordinate(Random(CParams.i_bg_width),Random(CParams.i_bg_height));
            }

        }
        gameView.setLengthTextView(total_length);

        //obstacle相对坐标方向
        //get vector to closest mine
        BaseObject vClosestObstacle = GetClosestObstacles(obstacles);
        Vector2D vClosestObstacleCord = new Vector2D(vClosestObstacle.getX() - getHead().getX(),
                vClosestObstacle.getY() - getHead().getY());
        double temp_distance = Math.sqrt(Math.pow(vClosestObstacleCord.x,2) +
                Math.pow(vClosestObstacleCord.y,2));

        if(temp_distance<100){
            dead(foods);
        }

        if(snakeBodyList.get(0).getX()>=CParams.i_bg_width
                || snakeBodyList.get(0).getX() <= -CParams.i_bg_width){
            dead(foods);
        }
        if(snakeBodyList.get(0).getY()>=CParams.i_bg_height
                || snakeBodyList.get(0).getY()<= -CParams.i_bg_height){
            dead(foods);
        }

        return true;
    }
    public float getSnakeGL_X(){
        return snakeBodyList.get(0).getGl_x();
    }
    public float getSnakeGL_Y(){
        return snakeBodyList.get(0).getGl_y();
    }
    public int length(){
        return snakeBodyList.size();
    }
    public void Up(){
        for(int i=(length()-1);i>0;--i){
//            int x1 = snakeBodyList.get(i).getX();
//            int y1 = snakeBodyList.get(i).getY();
//            int x2 = snakeBodyList.get(i-1).getX();
//            int y2 = snakeBodyList.get(i-1).getY();
//            if(y1==y2 || (Math.abs(x1-x2)<100&&Math.abs(x1-x2)>0)){
//                int add = x2>x1 ? 2 : -2;
//                snakeBodyList.get(i).setCoordinate(x1+add,y1);
//            }else {
//                int add = y2>y1 ? 1 : -1;
//                snakeBodyList.get(i).setCoordinate(x1,y1+add);
//            }
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setY(snakeBodyList.get(0).getY()+(80/CParams.ratio));
        //m_iDirection_flag = UP_FLAG;
        //snakeBodyList.get(0).Up();
    }

    public void move(int flag){
        snakeBodyList.get(0).setM_iDirection(flag);
        snakeBodyList.get(0).Move();
        for(int i=1;i<snakeBodyList.size();++i){
            MoveIteration(snakeBodyList.get(i-1),snakeBodyList.get(i));
        }
    }

    public void MoveIteration(SnakeBody body1,SnakeBody body2){
        switch (body1.getM_iDirection()){
            case UP_FLAG:
                UpDown(body1,body2);
                break;
            case DOWN_FLAG:
                UpDown(body1,body2);
                break;
            case LEFT_FLAG:
                LeftRight(body1,body2);
                break;
            case RIGHT_FLAG:
                LeftRight(body1,body2);
                break;
        }

    }

    private void UpDown(SnakeBody body1,SnakeBody body2){
        if(body1.getM_iDirection()!=body2.getM_iDirection()){
            if(Math.abs(body1.getY()-body2.getY())==56){
                body2.setM_iDirection(body1.getM_iDirection());
                body2.Move();
            }else{
                if(body2.getM_iDirection()==RIGHT_FLAG){
                    if(body2.getX()<=body1.getX())
                        body2.Move();
                }else if(body2.getM_iDirection()==LEFT_FLAG){
                    if(body2.getX()>=body1.getX())
                        body2.Move();
                }
            }
        }else{
            if(Math.abs(body1.getX()-body2.getX())>=100 ||
                    Math.abs(body1.getY()-body2.getY())>=56)
                body2.Move();
        }
    }
    private void LeftRight(SnakeBody body1,SnakeBody body2){
        if(body1.getM_iDirection()!=body2.getM_iDirection()){
            if(Math.abs(body1.getX()-body2.getX())>=100){
                body2.setM_iDirection(body1.getM_iDirection());
                body2.Move();
            }else{
                if(body2.getM_iDirection()==UP_FLAG){
                    if(body2.getY()<=body1.getY())
                        body2.Move();
                }else if(body2.getM_iDirection()==DOWN_FLAG){
                    if(body2.getY()>=body1.getY())
                        body2.Move();
                }
            }
        }else{
            if(Math.abs(body1.getX()-body2.getX())>=100 ||
                    Math.abs(body1.getY()-body2.getY())>=56)
                body2.Move();
        }
    }


    public void Up2(){
        snakeBodyList.get(0).setM_iDirection(UP_FLAG);
        snakeBodyList.get(0).Move();
        for(int i = 1;i<snakeBodyList.size();++i){
            if(snakeBodyList.get(i-1).getM_iDirection()!=snakeBodyList.get(i).getM_iDirection()){
                if(Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())==56){
                    snakeBodyList.get(i).setM_iDirection(UP_FLAG);
                    snakeBodyList.get(i).Move();
                }else{
                    if(snakeBodyList.get(i).getM_iDirection()==RIGHT_FLAG){
                        if(snakeBodyList.get(i).getX()<=snakeBodyList.get(i-1).getX())
                            snakeBodyList.get(i).Move();
                    }else if(snakeBodyList.get(i).getM_iDirection()==LEFT_FLAG){
                        if(snakeBodyList.get(i).getX()>=snakeBodyList.get(i-1).getX())
                            snakeBodyList.get(i).Move();
                    }else if(snakeBodyList.get(i).getM_iDirection()==UP_FLAG){

                    }
                }

            }else{
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80 ||
                        Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())>=(80/CParams.ratio))
                    snakeBodyList.get(i).Move();
            }
        }

    }
    public void Down2(){
        snakeBodyList.get(0).setM_iDirection(DOWN_FLAG);
        snakeBodyList.get(0).Move();
        for(int i = 1;i<snakeBodyList.size();++i){
            if(snakeBodyList.get(i-1).getM_iDirection()!=snakeBodyList.get(i).getM_iDirection()){
                if(Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())==(80/CParams.ratio)){
                    snakeBodyList.get(i).setM_iDirection(DOWN_FLAG);
                    snakeBodyList.get(i).Move();
                }
                else{
                    if(snakeBodyList.get(i).getM_iDirection()==RIGHT_FLAG){
                        if(snakeBodyList.get(i).getX()<=snakeBodyList.get(i-1).getX())
                            snakeBodyList.get(i).Move();
                    }else if(snakeBodyList.get(i).getM_iDirection()==LEFT_FLAG){
                        if(snakeBodyList.get(i).getX()>=snakeBodyList.get(i-1).getX())
                            snakeBodyList.get(i).Move();
                    }
                }
            }else{
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80 ||
                        Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())>=(80/CParams.ratio))
                    snakeBodyList.get(i).Move();
            }

        }
    }
    public void Left2(){
        snakeBodyList.get(0).setM_iDirection(LEFT_FLAG);
        snakeBodyList.get(0).Move();
        for(int i = 1;i<snakeBodyList.size();++i){
            if(snakeBodyList.get(i-1).getM_iDirection()!=snakeBodyList.get(i).getM_iDirection()){
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80){
                    snakeBodyList.get(i).setM_iDirection(LEFT_FLAG);
                    snakeBodyList.get(i).Move();
                }
                else{
                    if(snakeBodyList.get(i).getM_iDirection()==UP_FLAG){
                        if(snakeBodyList.get(i).getY()<=snakeBodyList.get(i-1).getY())
                            snakeBodyList.get(i).Move();
                    }else if(snakeBodyList.get(i).getM_iDirection()==DOWN_FLAG){
                        if(snakeBodyList.get(i).getY()>=snakeBodyList.get(i-1).getY())
                            snakeBodyList.get(i).Move();
                    }
                }
            }else{
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80 ||
                        Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())>=(80/CParams.ratio))
                    snakeBodyList.get(i).Move();
            }
        }
    }
    public void Right2(){
        snakeBodyList.get(0).setM_iDirection(RIGHT_FLAG);
        snakeBodyList.get(0).Move();
        for(int i = 1;i<snakeBodyList.size();++i){
            if(snakeBodyList.get(i-1).getM_iDirection()!=snakeBodyList.get(i).getM_iDirection()){
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80){
                    snakeBodyList.get(i).setM_iDirection(RIGHT_FLAG);
                    snakeBodyList.get(i).Move();
                }
                else{
                    if(snakeBodyList.get(i).getM_iDirection()==UP_FLAG){
                        if(snakeBodyList.get(i).getY()<=snakeBodyList.get(i-1).getY())
                            snakeBodyList.get(i).Move();
                    }else if(snakeBodyList.get(i).getM_iDirection()==DOWN_FLAG){
                        if(snakeBodyList.get(i).getY()>=snakeBodyList.get(i-1).getY())
                            snakeBodyList.get(i).Move();
                    }
                }
            }else{
                if(Math.abs(snakeBodyList.get(i-1).getX()-snakeBodyList.get(i).getX())>=80 ||
                        Math.abs(snakeBodyList.get(i-1).getY()-snakeBodyList.get(i).getY())>=(80/CParams.ratio))
                    snakeBodyList.get(i).Move();
            }
        }
    }
    public void Down(){

        for(int i=(length()-1);i>0;--i){
//            int x1 = snakeBodyList.get(i).getX();
//            int y1 = snakeBodyList.get(i).getY();
//            int x2 = snakeBodyList.get(i-1).getX();
//            int y2 = snakeBodyList.get(i-1).getY();
//            if(y1==y2 || (Math.abs(x1-x2)<100&&Math.abs(x1-x2)>0)){
//                int add = x2>x1 ? 2 : -2;
//                snakeBodyList.get(i).setCoordinate(x1+add,y1);
//            }else {
//                int add = y2>y1 ? 1 : -1;
//                snakeBodyList.get(i).setCoordinate(x1,y1+add);
//            }
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setY(snakeBodyList.get(0).getY()-(80/CParams.ratio));
        //snakeBodyList.get(0).Down();
    }
    public void Left(){
        for(int i=(length()-1);i>0;--i){
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setX(snakeBodyList.get(0).getX()-80);
        //snakeBodyList.get(0).Left();
    }
    public void Right(){
        for(int i=(length()-1);i>0;--i){
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setX(snakeBodyList.get(0).getX()+80);
        //snakeBodyList.get(0).Right();
    }
    public BaseObject getHead(){
        return snakeBodyList.get(0);
    }
    private BaseObject GetClosestFoods(List<BaseObject> objects){
        double	closest_so_far = 99999;
        Vector2D snakeVec = new Vector2D(getHead().getX(),getHead().getY());
        //BaseObject vClosestObject = new BaseObject(0,0,0.0f,0.0f,0.0f,view);
        //cycle through mines to find closest
        for (int i=0; i<objects.size(); i++)
        {
            Vector2D food = new Vector2D(objects.get(i).getX(),objects.get(i).getY());
            double len_to_object = Vector2D.Vec2DLength(snakeVec,food);
            if (len_to_object < closest_so_far)
            {
                closest_so_far	= len_to_object;
                m_iClosest_food = i;
            }
        }
        return objects.get(m_iClosest_food);
    }
    private BaseObject GetClosestObstacles(List<BaseObject> objects){
        double	closest_so_far = 99999;
        Vector2D snakeVec = new Vector2D(getHead().getX(),getHead().getY());
        //BaseObject vClosestObject = new BaseObject(0,0,0.0f,0.0f,0.0f,view);
        //cycle through mines to find closest
        for (int i=0; i<objects.size(); i++)
        {
            Vector2D food = new Vector2D(objects.get(i).getX(),objects.get(i).getY());
            double len_to_object = Vector2D.Vec2DLength(snakeVec,food);
            if (len_to_object!=0 && len_to_object < closest_so_far)
            {
                closest_so_far	= len_to_object;
                m_iClosest_obstacle = i;
            }
        }
        return objects.get(m_iClosest_obstacle);
    }

    private int Random(int temp){
        int value = (int)(Math.random()*temp);
        return Math.random()>0.5?value:-value;
    }
    public void dead(List<BaseObject> foods){
        gameView.rthread.flag = false;
        for(int i=0;i<length();++i){
            float x = snakeBodyList.get(i).getX();
            float y = snakeBodyList.get(i).getY();
            snakeBodyList.get(i).setCoordinate(x+Random(10),y+Random(10));
            BaseObject object = snakeBodyList.get(i);
            object.setRadius(30);
            foods.add(object);
        }
        gameView.dead(total_length);
        snakeBodyList.clear();
        snakeBodyList.add(new SnakeBody(Random(CParams.i_bg_width),Random(CParams.i_bg_height),r,g,b, view));
        snakeBodyList.add(generateBody(snakeBodyList.get(0),snakeBodyList.get(0)));
        total_length = 20;
    }


}
