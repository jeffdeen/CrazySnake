package com.example.jeffdeen.snakeproject.AI;

import android.os.Environment;
import android.util.Log;

import com.example.jeffdeen.snakeproject.Util.CParams;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/16.
 */

public class AI_result {
    private List<Double> m_weights = new ArrayList<>();
    private List<Integer> m_layers_neuron_num = new ArrayList<>();
    private List<Integer> m_neuron_inputs = new ArrayList<>();
    public AI_result(String path){
        m_layers_neuron_num.add(CParams.iNeuronsPerHiddenLayer);
        m_layers_neuron_num.add(CParams.iNumOutputs);
        m_neuron_inputs.add(CParams.iNumInputs+1);
        m_neuron_inputs.add(CParams.iNeuronsPerHiddenLayer+1);
        String strFileName = Environment.getExternalStorageDirectory()+path;
        try {
            InputStream instream = new FileInputStream(strFileName);
            if (instream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while (( line = buffreader.readLine()) != null) {
                    m_weights.add(Double.parseDouble(line));
                }
                instream.close();
            }
        }
        catch (java.io.FileNotFoundException e)
        {
            Log.d("TestFile", "The File doesn't not exist.");
        }
        catch (IOException e)
        {
            Log.d("TestFile", e.getMessage());
        }

    }


    public List<Double> Update(List<Double> inputs){
        //stores the resultant outputs from each layer
        List<Double> outputs = new ArrayList<>();
        int index = 0;
        int cWeight;

        //first check that we have the correct amount of inputs
        if (inputs.size() != CParams.iNumInputs)
        {
            return outputs;
        }

        //For each layer....
        for (int i=0; i<CParams.iNumHidden + 1; ++i)
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
            for (int j=0; j<m_layers_neuron_num.get(i); ++j)
            {
                double netInput = 0;

                int	NumInputs = m_neuron_inputs.get(i);

                //for each weight
                for (int k=0; k<NumInputs - 1; ++k)
                {
                    //sum the weights x inputs
                    netInput += m_weights.get(index++)*inputs.get(cWeight++);
                }

                //add in the bias
                netInput += m_weights.get(index++) * CParams.dBias;

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
}
