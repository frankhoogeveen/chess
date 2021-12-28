/*
 * License: GPL v3
 * 
 */

package nl.fh.player;

import java.util.Set;
import nl.fh.gamestate.chess.ChessState;
import nl.fh.gamestate.Move;
import nl.fh.gamestate.chess.move.ChessResignation;

/**
 * 
 * Always resign
 */
public class ChessQuitter implements Player<ChessState>{

    @Override
    public Move getMove(ChessState currentState, Set<Move<ChessState>> legalMoves) {
        return ChessResignation.getInstance();
    }

    @Override
    public String getDescription() {
        return "Quitter";
    }

}