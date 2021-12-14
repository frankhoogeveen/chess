/*
 * License: GPL v3
 * 
 */

package nl.fh.metric;

import nl.fh.chess.Color;
import nl.fh.chess.Field;
import nl.fh.gamestate.GameState;
import nl.fh.player.evalplayer.Metric;

/**
 * Evaluates the state of the board, in line with
 * https://www.pi.infn.it/%7Ecarosi/chess/shannon.txt
 * 
 */
public class ShannonNoisyMetric implements Metric<GameState>{

    @Override
    public double eval(GameState state) {
        double score = 0.;
        
        if(state.getRules().isDrawn(state)){
            return 0.;
        }        
        
        score += 1.0 * materialScore(state);
        score += 1.0 * pawnStructureScore(state);
        score += 0.1 * movesScore(state);
        score += 1.e6 * mateScore(state);
        score += 0.01 * randomScore();
        
        return score;
    }
    
    private double materialScore(GameState state){
        double score = 0.;
        for(Field f : Field.getAll()){
            switch(state.getFieldContent(f)){
                case WHITE_QUEEN:
                    score += 9;
                    break;
                case BLACK_QUEEN:
                    score += -9;
                    break;
                case WHITE_ROOK:
                    score += 5;
                    break;
                case BLACK_ROOK:
                    score += -5;
                    break;
                case WHITE_BISHOP:
                    score += 3;
                    break;
                case BLACK_BISHOP:
                    score += -3;
                    break;
                case WHITE_KNIGHT:
                    score += 3;
                    break;
                case BLACK_KNIGHT:
                    score += -3;
                    break;
                case WHITE_PAWN:
                    score += 1;
                    break;
                case BLACK_PAWN:
                    score += -1;
                    break;  
                default:
            }
        }
        return score;
    }
    
    private double pawnStructureScore(GameState state){
        return 0.;
    }
    
    private double movesScore(GameState state){
       GameState opponent = state.changeColor();
       double score = state.getLegalMoves().size() - opponent.getLegalMoves().size();
       if(state.getToMove() == Color.BLACK){
           score = -score;
       }
       return score;
       
    }    
    
    private double mateScore(GameState state) {
        double result = 0.;
        if(state.getRules().isMate(state)){
            result = 1.e6;
            if(state.getToMove() == Color.BLACK){
                result = - result;
            }            
        }

        return result;
    }   

    private double randomScore() {
        return 2. * Math.random() - 1.;
    }

}