package com.example.jeffdeen.snakeproject.AI;

import com.example.jeffdeen.snakeproject.Util.CParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/14.
 */

public class CNeuralNet {
    private int	m_NumInputs;

    private int	m_NumOutputs;

    private int	m_NumHiddenLayers;

    private int	m_NeuronsPerHiddenLyr;

    //storage for each layer of neurons including the output layer
    private List<SNeuronLayer> m_vecLayers = new ArrayList<>();

    public CNeuralNet()
    {
        m_NumInputs = CParams.iNumInputs;
        m_NumOutputs = CParams.iNumOutputs;
        m_NumHiddenLayers =	CParams.iNumHidden;
        m_NeuronsPerHiddenLyr =	CParams.iNeuronsPerHiddenLayer;
        createNet();
    }

    private void createNet(){
        //create the layers of the network
        if (m_NumHiddenLayers > 0)
        {
            //create first hidden layer
            m_vecLayers.add(new SNeuronLayer(m_NeuronsPerHiddenLyr, m_NumInputs));

            for (int i=0; i<m_NumHiddenLayers-1; ++i)
            {

                m_vecLayers.add(new SNeuronLayer(m_NeuronsPerHiddenLyr,
                        m_NeuronsPerHiddenLyr));
            }

            //create output layer
            m_vecLayers.add(new SNeuronLayer(m_NumOutputs, m_NeuronsPerHiddenLyr));
        }

        else
        {
            //create output layer
            m_vecLayers.add(new SNeuronLayer(m_NumOutputs, m_NumInputs));
        }
    }

    public List<Double> GetWeights() {
        List<Double> weights = new ArrayList<>();
        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {
            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                {
                    weights.add(m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(k));
                }
            }
        }
        return weights;
    }

    //更改神经网络各个权重
    public void PutWeights(List<Double> weights){
        int cWeight = 0;

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {
            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                {
                    int temp = cWeight++;
                    //m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(k) = weights.get(temp);
                    m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.set(k,weights.get(temp));
                }
            }

        }
    }

    public int GetNumberOfWeights(){
        int weights = 0;

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                    weights++ ;
            }
        }
        return weights;
    }

    public List<Double> Update(List<Double> inputs){
        //stores the resultant outputs from each layer
        List<Double> outputs = new ArrayList<>();

        int cWeight = 0;

        //first check that we have the correct amount of inputs
        if (inputs.size() != m_NumInputs)
        {
            return outputs;
        }

        //For each layer....
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {

            if ( i > 0 )
            {
                inputs.clear();
                for(int m=0;m<outputs.size();++m){
                    inputs.add(outputs.get(m));
                }

            }
            outputs.clear();

            cWeight = 0;

            //for each neuron sum the (inputs * corresponding weights).Throw
            //the total at our sigmoid function to get the output.
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                double netInput = 0;

                int	NumInputs = m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs;

                //for each weight
                for (int k=0; k<NumInputs - 1; ++k)
                {
                    //sum the weights x inputs
                    netInput += m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(k) *
                            inputs.get(cWeight++);
                }

                //add in the bias
                netInput += m_vecLayers.get(i).m_vecNeurons.get(j).m_vecWeight.get(NumInputs-1) *
                        CParams.dBias;

                //we can store the outputs from each layer as we generate them.
                //The combined activation is first filtered through the sigmoid
                //function
                outputs.add(CalculateValue(netInput,
                        CParams.dActivationResponse));
                cWeight = 0;
            }
        }
        return outputs;
    }

    private double CalculateValue(double netInput, double response)
    {
        //return ( 1 / ( 1 + Math.exp(-netInput / response)));
        return netInput>0?response:0;
    }

    //杂交运算的切割方法，只在神经细胞的边界上切开
    public List<Integer> CalculateSplitPoints(){
        List<Integer> SplitPoints = new ArrayList<>();

        int WeightCounter = 0;

        //for each layer
        for (int i=0; i<m_NumHiddenLayers + 1; ++i)
        {
            //for each neuron
            for (int j=0; j<m_vecLayers.get(i).m_NumNeurons; ++j)
            {
                //for each weight
                for (int k=0; k<m_vecLayers.get(i).m_vecNeurons.get(j).m_NumInputs; ++k)
                {
                    ++WeightCounter;
                }

                SplitPoints.add(WeightCounter - 1);
            }
        }
        return SplitPoints;
    }



}
