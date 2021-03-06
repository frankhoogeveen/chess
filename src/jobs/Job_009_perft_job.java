/*
 * License: GPL v3
 * 
 */

package jobs;

import nl.fh.gamestate.chess.ChessState;
import nl.fh.output_file.OutputToFile;
import nl.fh.rule.chess.perft.Perft;
import nl.fh.rule.chess.perft.PerftCase;

/**
 * 
 */
public class Job_009_perft_job {
    
        
    // list moves at depth two
    public static void main(String[] args){
        
        int caseId = 9;
        
        PerftCase perftCase = Perft.getCases().get(caseId);
        int depth = perftCase.getDepth();
        long expValue = perftCase.getPerftValue();
        ChessState state = ChessState.fromFEN(perftCase.getFen());
        
        String result = Perft.details(state, depth);
        
        OutputToFile.write("perftcase_"+caseId+ "_" + depth, result);
        System.out.println("expected: " + expValue);
        System.out.println(result);    

        
//        Move m1 = PieceMove.getInstance(Field.getInstance("c2"), Field.getInstance("c4"));
//        GameState state = m1.applyTo(state);
//        
//        result = Perft.details(state, 4);
//        System.out.println(result);  

        

    }
}