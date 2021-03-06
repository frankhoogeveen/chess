/*
 * License: GPL v3
 * 
 */

package nl.fh.gamereport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.fh.gamestate.GameState;
import nl.fh.gamestate.Move;
import nl.fh.player.Player;

/**
 * 
 * This class maintains a record a Game. It contains:
 * 
 * - The moves
 * - The game states
 * - annotations
 * 
 * The moves and game states have to be added both. 
 * It is not the concern of this game report to ensure that the
 * moves and game states are legal and consistent. 
 * 
 */
public class GameReport<S extends GameState> {
    
    private final List<S> stateList;
    private final List<Move<S>> moveList;
    private GameResult gameResult;
    
    private final HashMap<String, String> tagValuePairs;
    private Player player1;
    private Player player2;

    public GameReport(){
        stateList = new ArrayList<S>();
        moveList = new ArrayList<Move<S>>();
        gameResult = GameResult.UNDECIDED;
        tagValuePairs = new HashMap<String, String>();
    }
    
    public List<S> getStateList() {
        return stateList;
    }
    
    /**
     * 
     * @return the final state of the reported game 
     * 
     * throws an exception when the stateList is empty
     */
    public S getFinalState(){
        if(stateList.isEmpty()){
            throw new IllegalStateException();
        }
        
        return this.stateList.get(this.stateList.size()-1);
    }

    public List<Move<S>> getMoveList() {
        return moveList;
    }
    
    /**
     * 
     * @return the final state of the reported game 
     * 
     * throws an exception when the stateList is empty
     */
    public Move<S> getFinalMove(){
        if(moveList.isEmpty()){
            return null;
        }
        
        return this.moveList.get(this.moveList.size()-1);
    }    

    /**
     * 
     * @return the result of the game. 
     */
    public GameResult getGameResult() {
        return gameResult;
    }

    /**
     * 
     * @return the tags of this game report 
     */
    public Set<String> getTags(){
        return tagValuePairs.keySet();
    }
    
        /**
     * 
     * @return the tags of this game report 
     */
    public Map<String, String> getTagValuePairs(){
        return tagValuePairs;
    }
    
    /**
     * 
     * @param tag
     * @return an empty string if the tag has not been defined, the value
     * corresponding to the tag otherwise. Unlike the get method of a map,
     * this will not return null;
     */
    public String getTag(String tag){
        if(tagValuePairs.containsKey(tag)){
            return tagValuePairs.get(tag);
        } else {
            return "";
        }
    }
    
    /**
     * Sets a tag, possible overwriting earlier key value pairs, without notice
     * 
     * @param key
     * @param value
     */
    public void addTag(String key, String value){
        tagValuePairs.put(key, value);
    }
    
    /**
     * add a move to this record
     * @param move
     */
    public void addMove(Move<S> move){
        moveList.add(move);
    }
    
    /**
     * add a game state to this record
     * @param state 
     */
    public void addGameState(S state){
        stateList.add(state);
    }
    
    
    /**
     * add a ply ( a move and the resulting state to the record)
     * @param move
     * @param state 
     */
    public void addPly(Move<S> move, S state){
        moveList.add(move);
        stateList.add(state);        
    }
    
    /**
     * set the result of this game. In case a tag "Result" is defined,
     * it is overwritten
     * @param result 
     */
    public void setResult(GameResult result){
       gameResult = result;
       if(this.tagValuePairs.keySet().contains("Result")){
            this.addTag("Result", result.toString());
       }
    }
        
    /**
     * 
     * @param firstPlayer
     * @param secondPlayer 
     * 
     * Adds tags White and Black for the two players. 
     */
    public void setPlayers(Player firstPlayer, Player secondPlayer) {
        this.player1 = firstPlayer;
        this.player2 = secondPlayer;
        this.tagValuePairs.put("Player1", firstPlayer.toString());
        this.tagValuePairs.put("Player2", secondPlayer.toString());
    }    

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}

