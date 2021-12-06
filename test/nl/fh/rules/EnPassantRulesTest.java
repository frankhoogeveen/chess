/*
 * License: GPL v3
 * 
 */
package nl.fh.rules;

import nl.fh.gamestate.GameState;
import java.util.Set;
import nl.fh.chess.Field;
import nl.fh.move.EnPassantCapture;
import nl.fh.move.Move;
import nl.fh.move.PieceMove;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * 
 */
public class EnPassantRulesTest {
    private static final Rules rules = new SimpleRules();    
    
    @Test
    public void testEnPassantWhite1(){
    String fen = "4k3/8/8/1pP5/8/8/8/4K3 w - b6 0 2";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("b6")));
        }

    }
    
    assertEquals(1, count);
    assertEquals(5+2, moves.size());
    }
    
    @Test
    public void testEnPassantWhite2(){
    String fen = "4k3/8/8/PpP5/8/8/8/4K3 w - b6 0 2";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("b6")));
        }

    }
    
    assertEquals(2, count);
    assertEquals(5+4, moves.size());
    }    
    
    @Test
    public void testEnPassantWhite3(){
    String fen = "4k3/8/1P6/pP6/8/8/8/4K3 w - a6 0 4";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("a6")));
        }

    }
    
    assertEquals(1, count);
    assertEquals(5+2, moves.size());
    }
    
    
    @Test
    public void testEnPassantBlack1(){
    String fen = "4k3/8/8/8/1Pp5/8/8/4K3 b - b3 0 1";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("b3")));
        }

    }
    
    assertEquals(1, count);
    assertEquals(5+2, moves.size());
    }
    
    @Test
    public void testEnPassantBlack2(){
    String fen = "4k3/8/8/8/pPp5/8/8/4K3 b - b3 0 1";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("b3")));
        }

    }
    
    assertEquals(2, count);
    assertEquals(5+4, moves.size());
    }    
    
    @Test
    public void testEnPassantBlack3(){
    String fen = "4k3/8/8/8/6pP/6p1/8/4K3 b - h3 0 4";
    Rules rules = new SimpleRules();
    
    GameState state = GameState.fromFEN(fen,rules);
    Set<Move> moves = state.getLegalMoves();
    
    int count = 0;
    for(Move m : moves){
        if(m instanceof EnPassantCapture){
            count += 1;
            assertTrue(m.getTo().equals(Field.getInstance("h3")));
        }

    }
    
    assertEquals(1, count);
    assertEquals(5+2, moves.size());
    }
        
    @Test
    public void testEnPassantFlag(){
        // note that this behaviour (showing the e.p. flag,
        // even if there is no opposing pawn that can capture e.p.
        // is in line with https://www.chessclub.com/help/PGN-spec
        //, but that lichess behaves differently
        
        
        String fen = "4k3/6p1/8/8/8/8/6P1/4K3 w - - 0 1";
        GameState state = GameState.fromFEN(fen,rules);
        
        Move m;
        Field g2 = Field.getInstance("g2");
        Field g4 = Field.getInstance("g4");
        Field g5 = Field.getInstance("g5");
        Field g7 = Field.getInstance("g7");
        Field e1 = Field.getInstance("e1");
        Field e2 = Field.getInstance("e2");
        Field e3 = Field.getInstance("e3");
        Field e7 = Field.getInstance("e7");
        Field e8 = Field.getInstance("e8");        
        
        assertTrue(! state.allowsEnPassant());
        
        m = PieceMove.getInstance(g2, g4);
        state = state.apply(m);
        assertTrue( state.allowsEnPassant());
        
        // test for the lichess behaviour
        // String target = "4k3/6p1/8/8/6P1/8/8/4K3 b - - 0 1"; lichess
        String target = "4k3/6p1/8/8/6P1/8/8/4K3 b - g3 0 1";    // pgn standard
        assertEquals(target, state.toFEN());        
        
        m = PieceMove.getInstance(e8, e7);
        state = state.apply(m);
        assertTrue(!state.allowsEnPassant());

        m = PieceMove.getInstance(e1, e2);
        state = state.apply(m);
        assertTrue(!state.allowsEnPassant());

        m = PieceMove.getInstance(g7, g5);
        state = state.apply(m);
        assertTrue( state.allowsEnPassant());

        m = PieceMove.getInstance(e2, e3);
        state = state.apply(m);
        assertTrue(!state.allowsEnPassant());   

        
    }
    

}