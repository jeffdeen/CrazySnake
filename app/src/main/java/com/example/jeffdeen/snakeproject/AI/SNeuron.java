package com.example.jeffdeen.snakeproject.AI;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by jeffdeen on 2017/2/14.
 */

public class SNeuron {
    public int m_NumInputs; //输入的个数
    public List<Double> m_vecWeight = new ArrayList<>();
    public SNeuron(int m_NumInputs){
        this.m_NumInputs = m_NumInputs + 1;
        for (int i=0; i<this.m_NumInputs; ++i)
        {
            m_vecWeight.add(Math.random()-Math.random());
        }
    }
}
