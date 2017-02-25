package com.example.jeffdeen.snakeproject.GameObjects;

import com.example.jeffdeen.snakeproject.AI.AI_result;
import com.example.jeffdeen.snakeproject.AI.CNeuralNet;
import com.example.jeffdeen.snakeproject.GameView;
import com.example.jeffdeen.snakeproject.Util.CParams;
import com.example.jeffdeen.snakeproject.Util.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/18.
 */

public class AI_snake {
    public List<BaseObject> snakeBodyList = new ArrayList<>();
    private GameView view;
    private CNeuralNet m_ItsBrain;
    private double m_dFitness = CParams.dStartFitness;
    private int direction_flag = (int)(Math.random()*4);

    private float r;
    private float g;
    private float b;
    private Vector2D snakeDirectionVec;
    private AI_result AIresult;
    private int total_length = 20;
    private int m_iClosest_food = 0;
    private int m_iClosest_obstacle = 0;
    private int temp_length = 0;
    //private AI_result avoidObstacle2;
    public AI_snake(int x,int y,float r,float g,float b, GameView view){
        this.view = view;
        this.r = r;
        this.g = g;
        this.b = b;
        m_ItsBrain = new CNeuralNet();
        //AIresult =new AI_result("/sGenome_avoid.txt");
        AIresult = new AI_result("/sGenome1.txt");
        snakeDirectionVec = new Vector2D(0,1);
        snakeBodyList.add(new SnakeBody(x,y,r,g,b, view));
        snakeBodyList.add(generateBody(snakeBodyList.get(0),snakeBodyList.get(0)));

        //snakeBodyList.add(generateBody(snakeBodyList.get(0),snakeBodyList.get(1)));
        //snakeBodyList.add(generateBody(snakeBodyList.get(1),snakeBodyList.get(2)));
        //addBody();
        //addBody();
        //addBody();
    }

    private SnakeBody generateBody(BaseObject object1, BaseObject object2){
        if(object1.getX()==object2.getX() && object1.getY()==object2.getY()){
            return new SnakeBody(object1.getX()-100,object2.getY(),
                    object1.getColor()[0],object1.getColor()[1],object1.getColor()[2],view);
        }else{
            int x = object2.getX()-object1.getX()+object2.getX();
            int y = object2.getY()-object1.getY()+object2.getY();
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
        UpdateDirectionVec();
        List<Double> inputs = new ArrayList<>();

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
            ++temp_length;
            if(vClosestFood.getRadius()==30){
                addBody();
                total_length+=10;
                foods.remove(m_iClosest_food);
            }else{
                if(temp_length==10){
                    addBody();
                    temp_length = 0;
                }
                foods.get(m_iClosest_food).setCoordinate(Random(CParams.i_bg_width),Random(CParams.i_bg_height));
            }

        }
        Vector2D.Vec2DNormalize(vClosestFoodCord);
        int sign   = Vector2D.Vec2DSign(snakeDirectionVec,vClosestFoodCord);
        double dot = Vector2D.Vec2DDot(snakeDirectionVec,vClosestFoodCord);

        //obstacle相对坐标方向
        //get vector to closest mine
        BaseObject vClosestObstacle = GetClosestObstacles(obstacles);

        Vector2D vClosestObstacleCord = new Vector2D(vClosestObstacle.getX() - getHead().getX(),
                vClosestObstacle.getY() - getHead().getY());
        double temp_distance = Math.sqrt(Math.pow(vClosestObstacleCord.x,2) +
                Math.pow(vClosestObstacleCord.y,2));
        Vector2D.Vec2DNormalize(vClosestObstacleCord);

        int sign2   = Vector2D.Vec2DSign(snakeDirectionVec,vClosestObstacleCord);
        double dot2 = Vector2D.Vec2DDot(snakeDirectionVec,vClosestObstacleCord);
        List<Double> output = new ArrayList<>();
        if(temp_distance<120){
            dead(foods);
        }
        if(temp_distance<350) {
            //增加行进的不确定性，防止snake原地不动
            if(Math.random()>0.4){
                inputs.add(dot2);
                inputs.add((double)sign2);
                //inputs.add(distance / 5000);
                output = AIresult.Update(inputs);
                UpdateDirection2(output);
            }
        }
        else{
            //最近的食物与蛇前进方向的夹角
            if(Math.random()>0.3){
                inputs.add(dot);
                inputs.add((double)sign);
                //inputs.add((double)sign);
                output = AIresult.Update(inputs);
                UpdateDirection(output);
            }

        }
        //update the brain and get feedback
        //List<Double> output = m_ItsBrain.Update(inputs);
        if(Math.random()>0.1){
            if(snakeBodyList.get(0).getX()<-4950){
                //snakeBodyList.get(0).setX(Random(5000));
                //.get(0).setY(Random(1500));
                direction_flag = 3;
            }else if(snakeBodyList.get(0).getX()>4950){
                direction_flag = 2;
            }else if(snakeBodyList.get(0).getX()==CParams.i_bg_width
                    || snakeBodyList.get(0).getX() == -CParams.i_bg_width){
                dead(foods);
            }
            if(snakeBodyList.get(0).getY()<-1450){
                //snakeBodyList.get(0).setX(Random(5000));
                //snakeBodyList.get(0).setY(Random(1500));
                direction_flag = 0;
            }else if(snakeBodyList.get(0).getY()>1450){
                direction_flag = 1;
            }else if(snakeBodyList.get(0).getY()==CParams.i_bg_height
                    || snakeBodyList.get(0).getY()== -CParams.i_bg_height){
                dead(foods);
            }
        }
        Move();
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
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setY(snakeBodyList.get(0).getY()+45);
        //snakeBodyList.get(0).Up();
    }
    public void Down(){
        for(int i=(length()-1);i>0;--i){
            snakeBodyList.get(i).setCoordinate(snakeBodyList.get(i-1).getX(),snakeBodyList.get(i-1).getY());
        }
        snakeBodyList.get(0).setY(snakeBodyList.get(0).getY()-45);
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
    private void UpdateDirectionVec(){
        switch (direction_flag){
            //上
            case 0:
                snakeDirectionVec.y=1;
                snakeDirectionVec.x=0;
                break;
            //下
            case 1:
                snakeDirectionVec.y=-1;
                snakeDirectionVec.x=0;
                break;
            //左
            case 2:
                snakeDirectionVec.y=0;
                snakeDirectionVec.x=-1;
                break;
            //右
            case 3:
                snakeDirectionVec.y=0;
                snakeDirectionVec.x=1;
                break;
        }
    }
    private void Move(){
        switch (direction_flag){
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
    private void UpdateDirection(List<Double> inputs){
        if(inputs.get(0)==0 && inputs.get(1)==0){
            //输出都为0，方向倒转
            //direction_flag = 0;
            //Up();

        }
        else if(inputs.get(0)==0 && inputs.get(1)==1){

            //向右转
            switch (direction_flag){
                case 0:
                    direction_flag = 3;
                    break;
                case 1:
                    direction_flag = 2;
                    break;
                case 2:
                    direction_flag = 0;
                    break;
                case 3:
                    direction_flag = 1;
                    break;
            }
        }
        else if(inputs.get(0)==1 && inputs.get(1)==0){
            //向左转
            switch (direction_flag){
                case 0:
                    direction_flag = 2;
                    break;
                case 1:
                    direction_flag = 3;
                    break;
                case 2:
                    direction_flag = 1;
                    break;
                case 3:
                    direction_flag = 0;
                    break;
            }
        }
        else if(inputs.get(0)==1 && inputs.get(1)==1){
            switch (direction_flag){
                case 0:
                    direction_flag = 1;
                    break;
                case 1:
                    direction_flag = 0;
                    break;
                case 2:
                    direction_flag = 3;
                    break;
                case 3:
                    direction_flag = 2;
                    break;
            }
        }
    }
    //遇到障碍是调用,与遇到foods相反
    private void UpdateDirection2(List<Double> inputs){
        if(inputs.get(0)==0 && inputs.get(1)==0){
            //输出都为0，方向倒转
            //direction_flag = 0;
            //Up();
            switch (direction_flag){
                case 0:
                    direction_flag = 1;
                    break;
                case 1:
                    direction_flag = 0;
                    break;
                case 2:
                    direction_flag = 3;
                    break;
                case 3:
                    direction_flag = 2;
                    break;
            }
        }
        else if(inputs.get(0)==0 && inputs.get(1)==1){
            //向左转
            switch (direction_flag){
                case 0:
                    direction_flag = 2;
                    break;
                case 1:
                    direction_flag = 3;
                    break;
                case 2:
                    direction_flag = 1;
                    break;
                case 3:
                    direction_flag = 0;
                    break;
            }
        }
        else if(inputs.get(0)==1 && inputs.get(1)==0){
            //向右转
            switch (direction_flag){
                case 0:
                    direction_flag = 3;
                    break;
                case 1:
                    direction_flag = 2;
                    break;
                case 2:
                    direction_flag = 0;
                    break;
                case 3:
                    direction_flag = 1;
                    break;
            }


        }
        else if(inputs.get(0)==1 && inputs.get(1)==1){


        }
    }
    public int GetNumberOfWeights(){
        return m_ItsBrain.GetNumberOfWeights();
    }

    public void PutWeights(List<Double> w){
        m_ItsBrain.PutWeights(w);
    }

    public List<Integer> CalculateSplitPoints(){
        return m_ItsBrain.CalculateSplitPoints();
    }
    public void IncrementFitness(){
        ++m_dFitness;
    }

    public void DecrementFitness(){
        m_dFitness /= 4;
    }

    public double Fitness(){
        return m_dFitness;
    }

    public void Reset(int x,int y){
        snakeBodyList.get(0).setX(x);
        snakeBodyList.get(0).setY(y);
        direction_flag = (int)(Math.random()*4);
        m_dFitness = CParams.dStartFitness;
    }

    private int Random(int temp){
        int value = (int)(Math.random()*temp);
        return Math.random()>0.5?value:-value;
    }
    private void dead(List<BaseObject> foods){
        for(int i=0;i<length();++i){
            int x = snakeBodyList.get(i).getX();
            int y = snakeBodyList.get(i).getY();
            snakeBodyList.get(i).setCoordinate(x+Random(10),y+Random(10));
            BaseObject object = snakeBodyList.get(i);
            object.setRadius(30);
            foods.add(object);
        }
        snakeBodyList.clear();
        snakeBodyList.clear();
        snakeBodyList.add(new SnakeBody(Random(CParams.i_bg_width),CParams.i_bg_height,r,g,b, view));
        snakeBodyList.add(generateBody(snakeBodyList.get(0),snakeBodyList.get(0)));
    }
}
