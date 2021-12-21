/*
 * License: GPL v3
 * 
 */

package nl.fh.metric.minimax;

import java.util.Set;
import nl.fh.chess.Colored;
import nl.fh.player.evalplayer.Metric;

/**
 *  This wraps around a metric to create the negamax metric with alpha beta pruning
 *  and explicit commands to forget the daughters of a node after processing
 *     // https://en.wikipedia.org/wiki/Negamax
 * 
 * 
 */
public class NegaMaxGen3<T extends Parent<T> & Colored> implements Metric<T> {
    private Metric<T> baseMetric;
    private int depth;
    
    /**
     * 
     * @param baseMetric
     * @param depth
     * The mode is the default maximin. 
     * 
     */    
    public NegaMaxGen3 (Metric<T> baseMetric, int depth){
        this.baseMetric = baseMetric;
        this.depth = depth;
    }    
    
    @Override
    public double eval(T state) {
        int sign = state.getColor().getSign();
        double alpha = - Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;
        return  sign * iteration(state, this.depth, sign, alpha, beta);  
    }  

    private double iteration(T state, int depth, int sign, double alpha, double beta) {
        if(depth == 0){
            return sign * baseMetric.eval(state);
        } 
        
        Set<T> daughters = state.getChildren();
        if(daughters.isEmpty()){
            return sign * baseMetric.eval(state);
        }
        
        double currentValue = - Double.MAX_VALUE;
        for(T daughter : daughters){
            double nextValue = - iteration(daughter, depth-1,-sign, -beta, -alpha);
            
            if(nextValue > currentValue){
                currentValue = nextValue;
            }
            
            if(currentValue > alpha){
                alpha = currentValue;
            }
            
            if(alpha > beta){
                break;
            }
        }
        
        //release objects so that we do not have to drag the entire gametree to
        //the heap
        state.forgetChildren();
        
        return currentValue;
    }

    public Metric<T> getBaseMetric() {
        return baseMetric;
    }

    public void setBaseMetric(Metric<T> baseMetric) {
        this.baseMetric = baseMetric;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public String getDescription() {
        return "Negamax Gen3: depth "+ depth + " " + this.baseMetric.getDescription();
    }
}