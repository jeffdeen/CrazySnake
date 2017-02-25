package com.example.jeffdeen.snakeproject.AI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/14.
 */

public class SNeuronLayer {
    //the number of neurons in this layer
    public int m_NumNeurons;

    //the layer of neurons
    public List<SNeuron> m_vecNeurons = new ArrayList<>();

    public SNeuronLayer(int NumNeurons,
                 int NumInputsPerNeuron){
        this.m_NumNeurons = NumNeurons;
        for(int i = 0;i<this.m_NumNeurons;++i){
            m_vecNeurons.add(new SNeuron(NumInputsPerNeuron));
        }
    }
}
