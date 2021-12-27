/*
 * License: GPL v3
 * 
 */

package nl.fh.metric.negamax;

import nl.fh.metric.*;
import nl.fh.gamestate.GameState;
import nl.fh.metric.minimax.NegaMax;
import nl.fh.metric.minimax.NegaMaxAlphaBeta;
import nl.fh.player.evalplayer.Metric;
import nl.fh.rules.Chess;
import nl.fh.rules.GameDriver;
import nl.fh.rules.MoveGenerator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 
 * 
 */
public class NegaMaxAlphaBetaTest {
    private final double delta = 1.e-9;
    
    private GameDriver gameDriver = Chess.getGameDriver();
    private MoveGenerator moveGenerator = gameDriver.getMoveGenerator();    
    
    Metric<GameState> baseMetric = new MaterialCountMetric(gameDriver);   
    NegaMax<GameState> nega = new NegaMax<GameState>(baseMetric,moveGenerator, 0);
    NegaMaxAlphaBeta<GameState> negaAB = new NegaMaxAlphaBeta<GameState>(baseMetric, moveGenerator,  0);

    @Test
    public void testPruningDepthZero(){
        String fen = "r2qkb1r/pppbpppp/2n5/1B1pN3/3PnB2/4P3/PPP2PPP/RN1QK2R w KQkq - 7 7";
        GameState state  = GameState.fromFEN(fen);
        
        nega.setDepth(0);
        negaAB.setDepth(0);
        assertEquals(nega.eval(state), negaAB.eval(state), delta);
    }   
    
    @Test
    public void testPruningDepthOne(){
        String fen = "r2qkb1r/pppbpppp/2n5/1B1pN3/3PnB2/4P3/PPP2PPP/RN1QK2R w KQkq - 7 7";
        GameState state  = GameState.fromFEN(fen);
        
        nega.setDepth(1);
        negaAB.setDepth(1);
        assertEquals(nega.eval(state), negaAB.eval(state), delta);
    }   

    @Test
    public void testPruningDepthTwo(){
        String fen = "r2qkb1r/pppbpppp/2n5/1B1pN3/3PnB2/4P3/PPP2PPP/RN1QK2R w KQkq - 7 7";
        GameState state  = GameState.fromFEN(fen);
        
        nega.setDepth(2);
        negaAB.setDepth(2);
        assertEquals(nega.eval(state), negaAB.eval(state), delta);
    }  
    
    @Test
    public void testPruningDepthTwoBlackToMove(){
        String fen = "r2qkb1r/pppbpppp/2n5/1B1pN3/3PnB2/4P3/PPP2PPP/RN1QK2R b KQkq - 7 7";
        GameState state  = GameState.fromFEN(fen);
  
        nega.setDepth(2);
        negaAB.setDepth(2);
        assertEquals(nega.eval(state), negaAB.eval(state), delta);
    }       
    
    @Test
    public void testPruningDepthThree(){
        String fen = "r2qkb1r/pppbpppp/2n5/1B1pN3/3PnB2/4P3/PPP2PPP/RN1QK2R w KQkq - 7 7";
        GameState state  = GameState.fromFEN(fen);
        
        nega.setDepth(3);
        negaAB.setDepth(3);
        assertEquals(nega.eval(state), negaAB.eval(state), delta);
    }       
   
}









