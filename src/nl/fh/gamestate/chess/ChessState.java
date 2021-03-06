/*
 * License: GPL v3
 * 
 */

package nl.fh.gamestate.chess;

import nl.fh.gamestate.Move;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import nl.fh.gamestate.GameState;
import nl.fh.gamestate.Mover;
import nl.fh.gamestate.chess.format.FENformatter;

/**
 * @author frank
 * 
 * Keeps track of the state of a chess game.
 * 
 */
public class ChessState implements GameState   {
    
    

////////////////////////////////////////////////////////////////////////////////
// static data
////////////////////////////////////////////////////////////////////////////////    
    
    private static char[] file = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private static char[] rank = {'1', '2', '3', '4', '5', '6', '7', '8'};  
    private static FENformatter fenFormatter = new FENformatter();
    
////////////////////////////////////////////////////////////////////////////////
// the data independently describing the board state
////////////////////////////////////////////////////////////////////////////////     
    
    private PieceType[][] board;  // first coordinate is file, second is rank:
                                  // board[0][0] is a1
                                  // board[7][0] is h1 
                                  // board[0][7] is a8
    
    private Color activeColor;    // the player that moves next
    
    private boolean whiteCanCastleKingside;
    private boolean whiteCanCastleQueenside;
    private boolean blackCanCastleKingside;
    private boolean blackCanCastleQueenside;
 
    private Field enPassantField;  // field where an en passant capture is possible, null otherwise
    
    private int halfMoveClock;
//    private int fullMoveNumber;    Keeping track of the move number is the concern of the context of GameState
    
    private boolean drawOffered;
    
    /**
     * set up the board for a new game
     */
    public ChessState(){
        board = new PieceType[8][8];
        clear();
    }
    
    /**
     * reset the board to the state for a new game, without any pieces
     */
    private void clear(){
        
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = PieceType.EMPTY;
            }
        }
        
        activeColor = Color.WHITE;
        
        whiteCanCastleKingside = true;
        whiteCanCastleQueenside = true;
        blackCanCastleKingside = true;
        blackCanCastleQueenside = true;
        
        enPassantField = null;
        
        halfMoveClock = 0;
        
        drawOffered = false;
    }    
    

    
    /**
     * 
     * @param fen a string that contains a game state in FEN notation
     * @return a game state corresponding to the FEN string
     * 
     * https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
     * https://www.chessclub.com/help/PGN-spec
     * 
     * The FEN parser is tolerant of small deviations in the syntax
     */
    public static ChessState fromFEN(String fen){
        
        ChessState result = new ChessState();
        result.clear();
        
        // remove leading and trailing blanks
        String str = fen.replaceAll("^\\s+|\\s+$}","");
        
        //replace multiple blanks with a single blank
        str = str.replaceAll("\\s+"," ");
        
        //split the FEN string on the blank spaces
        String[] piece = str.split("\\s");
        if(piece.length != 6){
            throw new IllegalArgumentException("FEN string does not contain 6 parts: " + fen);
        }
        
        //get the location of the pieces
        //start by replacing all numbers by repetitions of '.'
        if(piece[0].contains(".")){
            throw new IllegalArgumentException("FEN string contains illegal character \".\":  "+ fen);
        }
        piece[0] = piece[0].replaceAll("8","........");
        piece[0] = piece[0].replaceAll("7",".......");
        piece[0] = piece[0].replaceAll("6","......");
        piece[0] = piece[0].replaceAll("5",".....");
        piece[0] = piece[0].replaceAll("4","....");
        piece[0] = piece[0].replaceAll("3","...");
        piece[0] = piece[0].replaceAll("2","..");
        piece[0] = piece[0].replaceAll("1",".");        
        
        //split the location information into rows
        String[] row = piece[0].split("/");
        if(row.length != 8){
            throw new IllegalArgumentException("FEN with incorrect number of rows: " + fen);
        }

        //parse row by row, keeping sight of the fact that a FEN string starts with the 8th row
        for(int nRow = 0; nRow < 8; nRow++){
            String currentRow = row[7 - nRow];
            if(currentRow.length() != 8){
                throw new IllegalArgumentException("FEN string with incorrect row length: " + fen);
            }
            for(int nCol = 0; nCol < 8; nCol ++){
                char c = currentRow.charAt(nCol);
                switch(c){
                    case 'R':
                        result.board[nCol][nRow] = PieceType.WHITE_ROOK;
                        break;
                    case 'N':
                        result.board[nCol][nRow] = PieceType.WHITE_KNIGHT;
                        break;
                    case 'B':
                        result.board[nCol][nRow] = PieceType.WHITE_BISHOP;
                        break;
                    case 'Q':
                        result.board[nCol][nRow] = PieceType.WHITE_QUEEN;
                        break;
                    case 'K':
                        result.board[nCol][nRow] = PieceType.WHITE_KING;
                        break;  
                    case 'P':
                        result.board[nCol][nRow] = PieceType.WHITE_PAWN;
                        break;                         
                    case 'r':
                        result.board[nCol][nRow] = PieceType.BLACK_ROOK;
                        break;
                    case 'n':
                        result.board[nCol][nRow] = PieceType.BLACK_KNIGHT;
                        break;
                    case 'b':
                        result.board[nCol][nRow] = PieceType.BLACK_BISHOP;
                        break;
                    case 'q':
                        result.board[nCol][nRow] = PieceType.BLACK_QUEEN;
                        break;
                    case 'k':
                        result.board[nCol][nRow] = PieceType.BLACK_KING;
                        break;   
                    case 'p':
                        result.board[nCol][nRow] = PieceType.BLACK_PAWN;
                        break;   
                    case '.':
                        result.board[nCol][nRow] = PieceType.EMPTY;
                        break;
                    default:
                        throw new IllegalArgumentException("FEN contains illegal character for piece: " + fen);
                }
            }
        }
        
        // get the player that is to move next
        // note that both upper and lower case are accepted
        switch(piece[1]){
            case "w":
            case "W":
                result.activeColor = Color.WHITE;
                break;
            case "b":
            case "B":
                result.activeColor = Color.BLACK;
                break;
            default:
                throw new IllegalArgumentException("FEN player to move is illegal: " + fen);
        }
        
        // the castling opportunities
        // note that this parser does not demand the KQkq order
        // multiple instances of the same character are allowed to appear
        
        result.whiteCanCastleKingside = false;
        result.whiteCanCastleQueenside = false;
        result.blackCanCastleKingside = false;
        result.blackCanCastleQueenside = false;        
        
        if(piece[2].length() > 4){
            throw new IllegalArgumentException("FEN contains too long castling string: " + fen);
        }
        if(!piece[2].equals("-")){
            for(int ix = 0; ix < piece[2].length(); ix++){
                char c = piece[2].charAt(ix);
                switch(c){
                    case 'K':
                        result.whiteCanCastleKingside = true;
                        break;
                    case 'Q':
                        result.whiteCanCastleQueenside = true;
                        break;
                    case 'k':
                        result.blackCanCastleKingside = true;
                        break;
                    case 'q':
                        result.blackCanCastleQueenside = true;
                        break;
                    default:
                        throw new IllegalArgumentException("FEN castling string contains illegal characters: " + fen);
                }
            }
        }
        
        // the enpassant information
        if(piece[3].equals("-")){
            result.enPassantField = null;
        } else if(piece[3].length() != 2){
            throw new IllegalArgumentException("FEN enpassant information incorrect length: " + fen);
        } else {
       
            int nFile = -1;
            int nRank = -1;
            for(int ix = 0; ix < 8; ix++){
                if(piece[3].charAt(1) == rank[ix]){
                    nRank= ix;
                }
                if(piece[3].charAt(0) == file[ix]){
                    nFile = ix;
                }
            }
            if((nFile == -1) || (nRank == -1)){
                throw new IllegalArgumentException("FEN enpassant information incorrect square: " + fen);
            }

            result.enPassantField = Field.getInstance(nFile, nRank);
 

            if(((result.activeColor == Color.BLACK) && (result.enPassantField.getY()) != 2)
                    || ((result.activeColor == Color.WHITE) && (result.enPassantField.getY()) != 5))     
              {
                throw new IllegalArgumentException("FEN enpassant information inconsistent with who is to move: " + fen);
            }
           }  
    
        // the half move clock
        result.halfMoveClock = Integer.parseInt(piece[4]);
        if(result.halfMoveClock < 0){
            throw new IllegalArgumentException("FEN negative number of half moves: " + fen);
        }
        
        // the full move number
//        result.fullMoveNumber = Integer.parseInt(piece[5]);
//        if(result.fullMoveNumber < 0){
//            throw new IllegalArgumentException("FEN negative number of full moves: " + fen);
//        }
        
        return result;
    } 
    
    /**
     * 
     * @return the color of the player that is to move next 
     */
    public Color getToMove() {
        return activeColor;
    }
    
    /**
     * 
     * @return the color of the player that is to move next 
     */
    @Override
    public Mover getMover(){
        if(this.activeColor == Color.WHITE){
            return Mover.FIRST_MOVER;
        } else {
            return Mover.SECOND_MOVER;
        }
    }
    
    /**
     * 
     * @return true if draw was offered on the last move 
     */
    public boolean isDrawOffered(){
        return this.drawOffered;
    }

    /**
     * 
     * @param ptype
     * @return the set of fields that contain piece of the given type 
     */
    public Set<Field> getPieceLocations(PieceType ptype) {
        Set<Field> result = new HashSet<Field>();
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                if(this.board[x][y] == ptype){
                    result.add(Field.getInstance(x,y));
                }
            }
        }
        return result;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return the piece at location x,y 
     */
    public PieceType getFieldContent(int x, int y){
        return this.board[x][y];
    }
    /**
     * 
     * @param field
     * @return the contents of the field in this game state 
     */
    public PieceType getFieldContent(Field field){
        return getFieldContent(field.getX(), field.getY());
    }
    
    /**
     * Puts a piece of a given type on the given field
     * @param field
     * @param type 
     */
    public void setFieldContent(Field field, PieceType type){
        this.board[field.getX()][field.getY()] = type;
    }
    /**
     * 
     * @param move
     * @return a new game state by applying the move to this state
     * 
     * This may or may not be a legal move according to a rule set. All it does
     * it picking up a piece, moving it and possibly replacing it when promoting.
     * 
     */
    public ChessState apply(Move<ChessState> move) {
        return move.applyTo(this);
    } 
    
    /**
     * increments the counters and changes who is to move
     */
    public void increment(){
        this.halfMoveClock += 1;
//        if(this.activeColor == Color.BLACK){
//            this.fullMoveNumber += 1;
//        }
        this.activeColor = this.activeColor.flip();
    }
    
    /**
     * 
     * @return a copy of this game state 
     */
    public ChessState copy(){
        ChessState result = new ChessState();
        
        result.board = new PieceType[8][8];
        for(int x = 0; x < 8; x++){
            for(int y = 0; y < 8; y++){
                result.board[x][y] = this.board[x][y];
            }
        }
        result.activeColor = this.activeColor;   

        result.whiteCanCastleKingside = this.whiteCanCastleKingside;
        result.whiteCanCastleQueenside = this.whiteCanCastleQueenside;
        result.blackCanCastleKingside = this.blackCanCastleKingside;
        result.blackCanCastleQueenside = this.blackCanCastleQueenside;

        result.enPassantField = this.enPassantField;

        result.halfMoveClock =this.halfMoveClock;
//        result.fullMoveNumber =this.fullMoveNumber;

        result.drawOffered = this.drawOffered; 
        
        return result;
    }
    
    /**
     * 
     * @param color
     * @param side
     * @return true if the color can castle on the given side
     * 
     * This only checks on whether the king and/or rook have 
     * already moved in the past. It does not check for the
     * other conditions on castling. This is the concern of the rules
     */
    public boolean getCastlingAllowedFlag(Color color, BoardSide side){
        if((color == Color.WHITE) && (side == BoardSide.KINGSIDE)){
            return this.whiteCanCastleKingside;
        }
        if((color == Color.WHITE) && (side == BoardSide.QUEENSIDE)){
            return this.whiteCanCastleQueenside;
        }
        if((color == Color.BLACK) && (side == BoardSide.KINGSIDE)){
            return this.blackCanCastleKingside;
        }
        if((color == Color.BLACK) && (side == BoardSide.QUEENSIDE)){
            return this.blackCanCastleQueenside;
        }
        throw new IllegalStateException("This should not happen");
    }
    
    /**
     * 
     * @param color
     * @param boardSide 
     * 
     * Clears the specified castling flag. I.e., after calling this method,
     * castling is not allowed anymore for the given color on the specified 
     * board side.
     */
    public void clearCastlingAllowedFlag(Color color, BoardSide boardSide) {
        if((color == Color.WHITE) && (boardSide == BoardSide.KINGSIDE)){
            this.whiteCanCastleKingside = false;
        } else if((color == Color.WHITE) && (boardSide == BoardSide.QUEENSIDE)){
            this.whiteCanCastleQueenside = false;
        } else if((color == Color.BLACK) && (boardSide == BoardSide.KINGSIDE)){
            this.blackCanCastleKingside = false;
        } else if((color == Color.BLACK) && (boardSide == BoardSide.QUEENSIDE)){
            this.blackCanCastleQueenside = false;
        } 
    }    
    
    /**
     * 
     * @return true if en passant moves are allowed 
     */
    public boolean allowsEnPassant() {
        return (this.enPassantField != null);
    }

    public Field getEnPassantField(){
        return this.enPassantField;
    }
    
    /**
     * 
     * @param field the field where en passant capture can take place
     * 
     * E.g. after the move e2-e4, this method should return e3
     */
    public void setEnPassantField(Field field){
        this.enPassantField = field;
    }
    
    /**
     * calling this method resets the en passant information of this GameState
     */
    public void clearEnPassant(){
        this.enPassantField = null;
    }
    
    /**
     * resets the half move clock. This is needed to keep track of e.g.
     * the fifty move rule.
     */
    public void resetHalfMoveClock() {
        this.halfMoveClock = 0;
    }
    
    /**
     * 
     * @return the half move clock 
     */
    public int getHalfMoveClock(){
        return this.halfMoveClock;
    }
    
    /**
     * 
     * @param color
     * @return the field where the king resides or null if there is no king on the board
     */
    public Field findKing(Color color){
        PieceType target; 
        switch(color){
            case WHITE:
                target = PieceType.WHITE_KING;
                break;
            case BLACK:
                target = PieceType.BLACK_KING;
                break;
            default:
                throw new IllegalStateException("Incorrect color");
        }
        
        for(Field f : Field.getAll()){
            if(this.getFieldContent(f) ==  target){
                return f;
            }
        }
        
        return null;
    }

    public PieceType[][] getBoard() {
        return board;
    }

    public Color getColor() {
        return activeColor;
    }
    
    
    
    public boolean isOfferedDraw(){
        return this.drawOffered;
    }
    
    public void offerDraw(){
        this.drawOffered = true;
    }
    
    public void agreeDraw(){
        // consider maintaining a this.drawAgreed field. For the moment there is no need
        if(!drawOffered){
           throw new IllegalStateException("Draw agreed, but not offered");   
        }
    } 

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Arrays.deepHashCode(this.board);
        hash = 79 * hash + Objects.hashCode(this.activeColor);
        hash = 79 * hash + (this.whiteCanCastleKingside ? 1 : 0);
        hash = 79 * hash + (this.whiteCanCastleQueenside ? 1 : 0);
        hash = 79 * hash + (this.blackCanCastleKingside ? 1 : 0);
        hash = 79 * hash + (this.blackCanCastleQueenside ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.enPassantField);
        hash = 79 * hash + this.halfMoveClock;
//        hash = 79 * hash + this.fullMoveNumber;
        hash = 79 * hash + (this.drawOffered ? 1 : 0);
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
        final ChessState other = (ChessState) obj;
        if (this.whiteCanCastleKingside != other.whiteCanCastleKingside) {
            return false;
        }
        if (this.whiteCanCastleQueenside != other.whiteCanCastleQueenside) {
            return false;
        }
        if (this.blackCanCastleKingside != other.blackCanCastleKingside) {
            return false;
        }
        if (this.blackCanCastleQueenside != other.blackCanCastleQueenside) {
            return false;
        }
        if (this.halfMoveClock != other.halfMoveClock) {
            return false;
        }
//        if (this.fullMoveNumber != other.fullMoveNumber) {
//            return false;
//        }
        if (this.drawOffered != other.drawOffered) {
            return false;
        }
        if (!Arrays.deepEquals(this.board, other.board)) {
            return false;
        }
        if (this.activeColor != other.activeColor) {
            return false;
        }
        if (!Objects.equals(this.enPassantField, other.enPassantField)) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @return a game state in which the player to move has been switched and
     * the en passant flag is not raised. The history of the game is discarded.
     */
    public ChessState changeColor() {
        ChessState result = this.copy();
        result.activeColor = this.activeColor.flip();
        result.enPassantField = null;
        return result;
    }
    
    @Override
    public String toString(){
        return ChessState.fenFormatter.format(this);
    }
}
