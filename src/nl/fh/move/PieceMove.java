/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fh.move;

import java.util.Objects;
import nl.fh.chess.BoardSide;
import nl.fh.chess.Color;
import nl.fh.chess.Field;
import nl.fh.chess.PieceType;
import nl.fh.gamestate.GameState;

/**
 *
 * @author frank
 * 
 * This class represents a move of a single piece or pawn
 */
public class PieceMove implements Move {

    private Field from;
    private Field to;
    private boolean drawOffer;
    
    private PieceMove(){
        
    }
    
    /**
     * 
     * @param from
     * @param to
     * @return moves the contents of 'from' to 'to'
     */
    public static PieceMove getInstance( Field from, Field to) {
        PieceMove result = new PieceMove();
        result.from = from;
        result.to = to;
        result.drawOffer = false;
        
        return result;
        
    }

    @Override
    public String moveString(){
        return "moveString() not yet coded";
    }

    @Override
    public Field getFrom() {
        return from;
    }

    @Override
    public Field getTo() {
        return to;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.from.toString());
        sb.append("-");
        sb.append(this.to.toString());
        return sb.toString();
    }

    @Override
    public GameState applyTo(GameState state) {
        GameState result = state.copy();
        
        // increment the counters
        result.increment();
        if(result.getFieldContent(to) != PieceType.EMPTY 
                || result.getFieldContent(from) == PieceType.WHITE_PAWN 
                || result.getFieldContent(from) == PieceType.BLACK_PAWN){
            result.resetHalfMoveClock();
        }
        
        // update the castling flags
        Color mover = state.getToMove();
        boolean affectWhiteKingside = from.equals(Field.getInstance("e1")) 
                                   || from.equals(Field.getInstance("h1"));
        affectWhiteKingside = affectWhiteKingside && (mover == Color.WHITE);
        if(affectWhiteKingside){
            result.clearCastlingAllowedFlag(mover, BoardSide.KINGSIDE);
        }
        
        boolean affectBlackKingside = from.equals(Field.getInstance("e8")) 
                                   || from.equals(Field.getInstance("h8"));
        affectBlackKingside = affectBlackKingside && (mover == Color.BLACK);
        if(affectBlackKingside){
            result.clearCastlingAllowedFlag(mover, BoardSide.KINGSIDE);
        }   
        
        boolean affectWhiteQueenside = from.equals(Field.getInstance("e1")) 
                                   || from.equals(Field.getInstance("a1"));
        affectWhiteQueenside = affectWhiteQueenside && (mover == Color.WHITE);
        if(affectWhiteQueenside){
            result.clearCastlingAllowedFlag(mover, BoardSide.QUEENSIDE);
        }
        
        boolean affectBlackQueenside = from.equals(Field.getInstance("e8")) 
                                   || from.equals(Field.getInstance("a8"));
        affectBlackQueenside = affectBlackQueenside && (mover == Color.BLACK);
        if(affectBlackQueenside){
            result.clearCastlingAllowedFlag(mover, BoardSide.QUEENSIDE);
        }          
        
        //move the piece
        PieceType piece = result.getFieldContent(this.from);
        result.setFieldContent(this.from, PieceType.EMPTY);
        result.setFieldContent(this.to, piece);
        
        return result;
    }

    @Override
    public boolean offeredDraw() {
        return this.drawOffer;
    }

    @Override
    public void offerDraw() {
        this.drawOffer = true;
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.from);
        hash = 79 * hash + Objects.hashCode(this.to);
        hash = 79 * hash + (this.drawOffer ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PieceMove other = (PieceMove) obj;
        if (this.drawOffer != other.drawOffer) {
            return false;
        }
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        return true;
    }
    
    
    
}