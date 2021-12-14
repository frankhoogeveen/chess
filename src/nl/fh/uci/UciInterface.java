/*
 * License: GPL v3
 * 
 */

package nl.fh.uci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.fh.gamestate.GameState;
import nl.fh.metric.ShannonMetric;
import nl.fh.move.Move;
import nl.fh.player.Player;
import nl.fh.player.evalplayer.MetricPlayer;
import nl.fh.player.random.RandomPlayer;
import nl.fh.rules.Rules;
import nl.fh.rules.SimpleRules;

/**
 * Wraps around a Player and communicates using the uci protocol
 * This results in a single thread interface, which is not
 * in line with the uci standard which states:
 * "the engine must always be able to process input from stdin, even while thinking.
 * 
 */
public class UciInterface implements Runnable {
   
    private BufferedReader reader;
    
    private final Player player;
    private final Rules rules;
    
    private GameState state;

    private UciInterface(Player player, Rules rules) {
        this.player = player;
        this.rules = rules;
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
        this.state = rules.getInitialState();
    }

    private String nextMove() {
        Move move = player.getMove(state);
        String result = move.getUCI(state);
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
        for(Move m : this.state.getLegalMoves()){
            if(m.getUCI(state).equals(string)){
                state = m.applyTo(state);
                count += 1;
            }
        }
        if(count > 1){
            throw new IllegalStateException("ambiguous move received from UCI");
        }
    }
    
    public static void main(String[] args){
        Player player = MetricPlayer.getInstance(new ShannonMetric());
        Rules rules = new SimpleRules();
        UciInterface uci = new UciInterface(player, rules);
        uci.run();
    }    
}