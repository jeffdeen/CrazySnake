package com.example.jeffdeen.snakeproject.AI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/14.
 */

public class SGenome implements Comparable{
    public List<Double> vecWeights = new ArrayList<>();
    public double dFitness;

    public SGenome(){
        this.dFitness = 0;
    }

    public SGenome(List<Double> w, double f){
        vecWeights = w;
        dFitness = f;
    }

    @Override
    public int compareTo(Object o) {
        return ((Double)dFitness).compareTo(((SGenome)o).dFitness);
    }
}
