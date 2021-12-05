/*
 * License: GPL v3
 * 
 */

package nl.fh.player.random;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import nl.fh.gamestate.GameState;
import nl.fh.move.Move;
import nl.fh.player.Player;

/**
 * Chooses moves randomly from the set of legal moves.
 * 
 */
public class RandomPlayer implements Player {

    @Override
    public Move getMove(GameState currentState) {
        
        List<Move> moves = new ArrayList<Move>(currentState.getLegalMoves());        
        int randomNum = ThreadLocalRandom.current().nextInt(0, moves.size());  
        return moves.get(randomNum);
    }
}