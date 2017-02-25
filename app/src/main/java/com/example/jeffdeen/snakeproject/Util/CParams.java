package com.example.jeffdeen.snakeproject.Util;

/**
 * Created by jeffdeen on 2017/2/14.
 */

public class CParams {
    public static int i_bg_width = 5000;
    public static int i_bg_height = 1500;
    //-------------------------------------used for the neural network
    public static int iNumInputs = 2;
    public static int iNumHidden = 1;
    public static int iNeuronsPerHiddenLayer = 15;
    public static int iNumOutputs = 2;
    public static int dBias = -1;
    public static double dStartFitness = 20;
    public static int dActivationResponse = 1;
    public static float dMaxPerturbation = 0.3f;
    public static int iNumCopiesElite = 1;
    public static int iNumElite = 4;
    public static double dMutationRate = 0.1;
    public static double dCrossoverRate = 0.7;
}
