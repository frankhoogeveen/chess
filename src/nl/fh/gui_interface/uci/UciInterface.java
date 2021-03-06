/*
 * License: GPL v3
 * 
 */

package nl.fh.gui_interface.uci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.fh.gamestate.chess.ChessState;
import nl.fh.metric.chess.MaterialCountMetric;
import nl.fh.gamestate.chess.move.ChessMove;
import nl.fh.gamestate.Move;
import nl.fh.player.Player;
import nl.fh.player.evalplayer.MetricPlayer;
import nl.fh.rule.chess.FIDEchess;
import nl.fh.rule.GameDriver;

/**
 * Wraps around a Player and communicates using the uci protocol
 * This results in a single thread interface, which is not
 * in line with the uci standard which states:
 * "the engine must always be able to process input from stdin, even while thinking.
 * 
 */
public class UciInterface implements Runnable {
   
    private BufferedReader reader;
    
    private  Player<ChessState> player;
    private  GameDriver<ChessState> driver;
    
    private ChessState state;

    private UciInterface(Player player, GameDriver gameDriver) {
        this.player = player;
        this.driver = gameDriver;
        reader = new BufferedReader(new InputStreamReader(System.in));          
    }

    @Override
    public void run() {
        boolean done = false;
        while(!done){
            String input = getFromServer();
            process(input);
        }
    }

    private String getFromServer() {
        
        String result = null;
        try {
            result = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(UciInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
     
        return result;
    }
    
    private void tellServer(String output) {
       System.out.println(output);
    }    

    private void process(String input) {
        String[] split = input.split(" ");
        if(split.length == 0){
            System.exit(-1);
        }
        
        if(split[0].equals("uci")){
            tellServer("id system BOTbreuk");
            tellServer("id author Frank Hoogeveen");
            announceOptions();
            tellServer("uciok");
        }
        
        if(split[0].equals("quit")){
            return;
        }
        
        if(split[0].equals("setoption")){
            setOptions(split);
        }
        
        if(split[0].equals("isready")){
            tellServer("readyok");
        }
        
        if(split[0].equals("ucinewgame")){
            initializeGame();
        }
        
        if(split[0].equals("position")){
            setPosition(split);
        }
        
        if(split[0].equals("go")){
            tellServer("bestmove " + nextMove());
        }

    }

    /**
     * method to send options to the server
     */
    private void announceOptions() {
//        tellServer("option name.....");
        return;
    }

    /**
     * Method to set the options on the engine(this) side
     * @param args 
     */
    private void setOptions(String[] args) {
    }

    private void initializeGame() {
        this.state = driver.getInitialState();
    }

    private String nextMove() {
        Set<Move<ChessState>> moves = this.driver.getMoveGenerator().calculateAllLegalMoves(state);
        Move<ChessState> move = player.getMove(state, moves);
        String result = ((ChessMove)move).formatUCI(state);
        state = move.applyTo(state);
        return result;
    }

    private void setPosition(String[] split) {
        // for the moment assume that the position is allways in sync
        for(int i = 0; i < split.length-1; i++){
            if(split[i].equals("moves")){
                guiMove(split[split.length-1]);
            }
        }
    }

    private void guiMove(String string) {
        int count = 0;
        for(Move<ChessState> m : driver.getMoveGenerator().calculateAllLegalMoves(state)){
            if(((ChessMove)m).formatUCI(state).equals(string)){
                state = m.applyTo(state);
                count += 1;
            }
        }
        if(count > 1){
            throw new IllegalStateException("ambiguous move received from UCI");
        }
    }
    
    public static void main(String[] args){
        Player player = MetricPlayer.getInstance(MaterialCountMetric.getWrappedInstance());
        GameDriver driver = FIDEchess.getGameDriver();
        UciInterface uci = new UciInterface(player, driver);
        uci.run();
    }    
}