package com.example.jeffdeen.snakeproject.AI;

import com.example.jeffdeen.snakeproject.Util.CParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jeffdeen on 2017/2/14.
 */

public class CGenAlg {
    //this holds the entire population of chromosomes
    private List<SGenome> m_vecPop = new ArrayList<>();

    //size of population
    private int m_iPopSize;

    //amount of weights per chromo
    private int m_iChromoLength;

    //this holds the positions of the split points in the genome for use
    //in our modified crossover operator
    private List<Integer> m_vecSplitPoints = new ArrayList<>();

    //total fitness of population
    private double m_dTotalFitness;

    //best fitness this population
    private double m_dBestFitness;

    //average fitness
    private double m_dAverageFitness;

    //worst
    private double m_dWorstFitness;

    //keeps track of the best genome
    private int m_iFittestGenome;

    //probability that a chromosones bits will mutate.
    //Try figures around 0.05 to 0.3 ish
    private double m_dMutationRate;

    //probability of chromosones crossing over bits
    //0.7 is pretty good
    private double m_dCrossoverRate;

    //generation counter
    private int m_cGeneration;

    public CGenAlg(int	  popsize,
                   double	MutRat,
                   double	CrossRat,
                   int	  numweights,
                   List<Integer> splits)
    {
        m_iPopSize = popsize;
        m_dMutationRate = MutRat;
        m_dCrossoverRate = CrossRat;
        m_iChromoLength = numweights;
        m_dTotalFitness = 0;
        m_cGeneration = 0;
        m_iFittestGenome = 0;
        m_dBestFitness = 0;
        m_dWorstFitness = 99999999;
        m_dAverageFitness = 0;
        m_vecSplitPoints = splits;

        //initialise population with chromosomes consisting of random
        //weights and all fitnesses set to zero
        for (int i=0; i<m_iPopSize; ++i)
        {
            m_vecPop.add(new SGenome());

//            String strFileName = Environment.getExternalStorageDirectory()+"/sGenome.txt";
//            try {
//                InputStream instream = new FileInputStream(strFileName);
//                if (instream != null)
//                {
//                    InputStreamReader inputreader = new InputStreamReader(instream);
//                    BufferedReader buffreader = new BufferedReader(inputreader);
//                    String line;
//                    int index = 0;
//                    //分行读取
//                    while (( line = buffreader.readLine()) != null) {
//                        double yy= Double.parseDouble(line);
//                        m_vecPop.get(i).vecWeights.add(Double.parseDouble(line));
//                        ++index;
//                    }
//                    instream.close();
//                }
//            }
//            catch (java.io.FileNotFoundException e)
//            {
//                Log.d("TestFile", "The File doesn't not exist.");
//            }
//            catch (IOException e)
//            {
//                Log.d("TestFile", e.getMessage());
//            }
            for (int j=0; j<m_iChromoLength; ++j)
            {
                m_vecPop.get(i).vecWeights.add(RandomClamped());
            }
        }
    }

    public List<SGenome> GetChromos(){return m_vecPop;}
    public double AverageFitness(){return m_dAverageFitness;}
    public double BestFitness() {return m_dBestFitness;}
    double RandomClamped()
    {
        return Math.random() - Math.random();
    }

    //基因变异
    private void Mutate(List<Double> chromo){
        //traverse the chromosome and mutate each weight dependent
        //on the mutation rate
        for (int i=0; i<chromo.size(); ++i)
        {
            //do we perturb this weight?
            if (Math.random() < m_dMutationRate)
            {
                //add or subtract a small value to the weight
                chromo.set(i,chromo.get(i)+(RandomClamped() * CParams.dMaxPerturbation));
            }
        }
    }

    //通过赌轮选择法选出进行进行繁殖的基因
    private SGenome GetChromoRoulette(){
        //generate a random number between 0 & total fitness count
        double Slice = (Math.random() * m_dTotalFitness);
        //this will be set to the chosen chromosome
        SGenome TheChosenOne = new SGenome();

        //go through the chromosones adding up the fitness so far
        double FitnessSoFar = 0;

        for (int i=0; i<m_iPopSize; ++i)
        {
            FitnessSoFar += m_vecPop.get(i).dFitness;

            //if the fitness so far > random number return the chromo at
            //this point
            if (FitnessSoFar >= Slice)
            {
                TheChosenOne = m_vecPop.get(i);
                break;
            }
        }
        //int rand = (int)Math.random()*m_vecPop.size();
        return TheChosenOne;
    }

    //杂交
    private void Crossover(
           List<Double> mum,
           List<Double> dad,
           List<Double> baby1,
           List<Double> baby2){
        if ( Math.random() > m_dCrossoverRate || (mum == dad))
        {
            baby1 = mum;
            baby2 = dad;
            return;
        }

        //determine a crossover point
        int cp =(int) (Math.random()*m_iChromoLength);

        //create the offspring
        for (int i=0; i<cp; ++i)
        {
            baby1.add(mum.get(i));
            baby2.add(dad.get(i));
        }
        for (int i=cp; i<mum.size(); ++i)
        {
            baby1.add(dad.get(i));
            baby2.add(mum.get(i));
        }
    }

    //从神经细胞边界进行杂交
    private void CrossoverAtSplits(
            List<Double> mum,
            List<Double> dad,
            List<Double> baby1,
            List<Double> baby2
    ){
        //just return parents as offspring dependent on the rate
        //or if parents are the same
        if ( Math.random() > m_dCrossoverRate || (mum == dad))
        {
            for(int i=0;i<mum.size();++i){
                baby1.add(mum.get(i));
                baby2.add(dad.get(i));
            }
            //baby1 = mum;
            //baby2 = dad;
            return;
        }

        //determine two crossover points
        int temp1 = (int)(Math.random()*(m_vecSplitPoints.size()-1));
        int cp1 = m_vecSplitPoints.get(temp1);
        int temp2 = (int)(Math.random()*(m_vecSplitPoints.size()-temp1))+temp1;
        int cp2 = m_vecSplitPoints.get(temp2);


        //create the offspring
        for (int i=0; i<mum.size(); ++i)
        {
            if ( (i<cp1) || (i>=cp2) )
            {
                //keep the same genes if outside of crossover points
                baby1.add(mum.get(i));
                baby2.add(dad.get(i));
            }
            else
            {
                //switch over the belly block
                baby1.add(dad.get(i));
                baby2.add(mum.get(i));
            }
        }
    }

    public List<SGenome> Epoch(List<SGenome> old_pop){
        //assign the given population to the classes population
        m_vecPop = old_pop;

        //reset the appropriate variables
        Reset();

        //sort the population (for scaling and elitism)
        Collections.sort(m_vecPop);

        //calculate best, worst, average and total fitness
        CalculateBestWorstAvTot();

        //create a temporary vector to store new chromosones
        List <SGenome> vecNewPop = new ArrayList<>();

        //Now to add a little elitism we shall add in some copies of the
        //fittest genomes. Make sure we add an EVEN number or the roulette
        //wheel sampling will crash
        if ((CParams.iNumCopiesElite * CParams.iNumElite % 2)==0)
        {
            GrabNBest(CParams.iNumElite, CParams.iNumCopiesElite, vecNewPop);
        }


        //now we enter the GA loop

        //repeat until a new population is generated
        while (vecNewPop.size() < m_iPopSize)
        {
            //grab two chromosones
            SGenome mum = GetChromoRoulette();
            SGenome dad = GetChromoRoulette();

            //create some offspring via crossover
            List<Double> baby1 = new ArrayList<>();
            List<Double> baby2 = new ArrayList<>();

            CrossoverAtSplits(mum.vecWeights, dad.vecWeights, baby1, baby2);

            //now we mutate
            Mutate(baby1);
            Mutate(baby2);

            //now copy into vecNewPop population
            vecNewPop.add( new SGenome(baby1, 0) );
            vecNewPop.add( new SGenome(baby2, 0) );
        }

        //finished so assign new pop back into m_vecPop
        m_vecPop = vecNewPop;
        //FitnessScaleRank();
        return m_vecPop;
    }

    //按照适应性分数的排名重新给适应性分数赋值
    public void FitnessScaleRank(){
        final int FitnessMultiplier = 1;

        //assign fitness according to the genome's position on
        //this new fitness 'ladder'
        for (int i=0; i<m_iPopSize; i++)
        {
            m_vecPop.get(i).dFitness = i * FitnessMultiplier;
        }

        //recalculate values used in selection
        CalculateBestWorstAvTot();
    }

    private void Reset()
    {

        m_dTotalFitness		= 0;
        m_dBestFitness		= 0;
        m_dWorstFitness		= 9999999;
        m_dAverageFitness	= 0;

    }

    private void CalculateBestWorstAvTot(){
        m_dTotalFitness = 0;

        double HighestSoFar = 0;
        double LowestSoFar  = 9999999;

        for (int i=0; i<m_iPopSize; ++i)
        {
            //update fittest if necessary
            if (m_vecPop.get(i).dFitness > HighestSoFar)
            {
                HighestSoFar	 = m_vecPop.get(i).dFitness;
                m_iFittestGenome = i;
                m_dBestFitness	 = HighestSoFar;
            }
            //update worst if necessary
            if (m_vecPop.get(i).dFitness < LowestSoFar)
            {
                LowestSoFar = m_vecPop.get(i).dFitness;
                m_dWorstFitness = LowestSoFar;
            }

            m_dTotalFitness	+= m_vecPop.get(i).dFitness;

        }//next chromo
        m_dAverageFitness = m_dTotalFitness / m_iPopSize;
    }

    private void GrabNBest(
            int	            NBest,
            int	      NumCopies,
            List<SGenome>	Pop
    ){
        while(NBest-->0)
        {
            for (int i=0; i<NumCopies; ++i)
            {
                Pop.add(m_vecPop.get((m_iPopSize - 1) - NBest));
            }
        }
    }

    public double getM_dBestFitness() {
        return m_dBestFitness;
    }

    public double getM_dAverageFitness(){
        return m_dAverageFitness;
    }

    public SGenome getBestGenome(){
        return m_vecPop.get(m_iFittestGenome);
    }

    public int getM_iFittestGenome(){
        return m_iFittestGenome;
    }
}
