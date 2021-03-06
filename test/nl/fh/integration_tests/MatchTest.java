/*
 * License: GPL v3
 * 
 */

package nl.fh.integration_tests;

import java.util.List;
import nl.fh.gamereport.GameFilter;
import nl.fh.gamereport.GameReport;
import nl.fh.gamereport.chess.PGNreportFormatter;
import nl.fh.gamereport.filter.TransparentFilter;
import nl.fh.gamestate.chess.ChessState;
import nl.fh.match.AlternatingMatch;
import nl.fh.match.Match;
import nl.fh.match.MatchReport;
import nl.fh.match.MatchReportFormatter;
import nl.fh.metric.chess.MaterialCountMetric;
import nl.fh.gamestate.chess.parser.PGN_Reader;
import nl.fh.gamestate.chess.parser.TolerantReader;
import nl.fh.player.Player;
import nl.fh.player.evalplayer.MetricPlayer;
import nl.fh.player.random.RandomPlayer;
import nl.fh.rule.chess.FIDEchess;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 
 * 
 */
public class MatchTest {
    @Test 
    /**
     * Tests the playing of a match, the conversion to pgn and the legibility
     * of the pgn
     */
    public void testMatch(){
        
        Player playerR = new RandomPlayer();
        Player playerM = MetricPlayer.getInstance(MaterialCountMetric.getWrappedInstance());
        
        int nRounds = 2;
        int nGames = 2;     
        GameFilter filter = new TransparentFilter();
        Match match = new AlternatingMatch(nGames, FIDEchess.getGameDriver());
        
        for(int iRound = 0; iRound < nRounds; iRound++){
            
//            System.out.println("Starting round " + iRound + "/" + nRounds);
//            System.out.flush();
            
            for(int iGame = 0; iGame<nGames; iGame++){
                
                MatchReport result = match.play(playerR, playerM, filter);
                MatchReportFormatter formatter = new PGNreportFormatter(FIDEchess.getGameDriver());
                String pgn = formatter.formatMatch(result);

                PGN_Reader reader = new TolerantReader();
                List<GameReport<ChessState>> result2 = reader.getGames(pgn, FIDEchess.getGameDriver());

                assertEquals(nGames, result2.size());
            }
        }
    }

}