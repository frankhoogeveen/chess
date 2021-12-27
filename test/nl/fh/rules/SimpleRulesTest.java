/*
 * License: GPL v3
 * 
 */
package nl.fh.rules;

import nl.fh.gamestate.GameState;
import java.util.Set;
import nl.fh.chess.Color;
import nl.fh.chess.Field;
import nl.fh.chess.PieceKind;
import nl.fh.move.Move;
import nl.fh.move.PieceMove;
import nl.fh.move.Promotion;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class SimpleRulesTest {
    
    private GameDriver gameDriver = Chess.getGameDriver();
    private MoveGenerator moveGenerator = gameDriver.getMoveGenerator();
      
    @Test
    public void testLegalMove1(){
        GameState state = Chess.getInitialState();
        
        Field from = Field.getInstance("b1");
        Field to   = Field.getInstance("a3");
        Move move = PieceMove.getInstance(from, to);
        
        assertTrue(moveGenerator.calculateAllLegalMoves(state).contains(move));      
    }   

    @Test
    public void testLegalMove2(){

        GameState state = Chess.getInitialState();
        state.increment();
        
        Field from = Field.getInstance("g8");
        Field to   = Field.getInstance("h6");
        Move move = PieceMove.getInstance(from, to);
        
        assertTrue(moveGenerator.calculateAllLegalMoves(state).contains(move));    
    }  
    
    @Test
    public void testLegalMoveGenerator(){

        GameState state = Chess.getInitialState();
        
        Field from = Field.getInstance("e2");
        Field to   = Field.getInstance("e4");
        Move move = PieceMove.getInstance(from, to);
        
        state = state.apply(move);
        
        Set<Move> moves = moveGenerator.calculateAllLegalMoves(state);        
        assertEquals(20, moves.size());
    }
    
    @Test
    public void testIsCovered(){
        String fen = "4k3/6b1/8/8/2R3r1/3n4/8/4K3 w - - 0 1";
        
        GameState state = GameState.fromFEN(fen);
        
        assertTrue(!Field.isCovered(Field.getInstance("a4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("b4"),state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("c4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("d4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("e4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("f4"), state, Color.BLACK));
        assertTrue(!Field.isCovered(Field.getInstance("g4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("h4"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("g5"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("g6"), state, Color.BLACK));
        assertTrue( Field.isCovered(Field.getInstance("g7"), state, Color.BLACK));
        assertTrue(!Field.isCovered(Field.getInstance("g8"), state, Color.BLACK));        
    }
    
    @Test
    public void testCannotLeaveKingInCheck(){
        
        String fen = "1r5k/8/8/8/8/8/8/K7 w - - 0 1";
        GameState state = GameState.fromFEN(fen);

        
        Set<Move> moves = moveGenerator.calculateAllLegalMoves(state);
        assertEquals(1, moves.size());
    }
    
    @Test
    public void testFindLegalMoves(){
        
        String fen = "rnbqk3/ppppp1P1/8/8/8/8/PPPPPP1P/RNBQKBNR w KQq - 0 1";
        GameState state = GameState.fromFEN(fen);
        
        Set<Move> set = moveGenerator.calculateAllLegalMoves(state);
        
        Field from = Field.getInstance("g7");        
        Field to = Field.getInstance("g8");
        
        Move movePawn = PieceMove.getInstance(from, to);
        Move movePromotion = Promotion.getInstance(from, to, PieceKind.KNIGHT);
        
        assertTrue(!moveGenerator.calculateAllLegalMoves(state).contains(movePawn));
        assertTrue(moveGenerator.calculateAllLegalMoves(state).contains(movePromotion));        
    }      
}
