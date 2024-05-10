package chess.board;


import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.move_validation.interfaces.PieceValidator;
import chess.engine.move_validation.service.MoveValidator;
import chess.state.GameState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public final class Board {

    private Bitboard bitboard;
    private PieceColor currentPlayer;
    private boolean check;
    private boolean checkmate;
    private boolean isStalemate;
    private GameState gameState;

    public Board() {
        this.bitboard = new Bitboard();
        this.currentPlayer = PieceColor.WHITE;
        initializeBoard();
        this.gameState = new GameState(getGameStateData()); // Use the new constructor
        this.check = gameState.isKingInCheck();
        this.checkmate = gameState.isKingInCheckmate();
    }

    public Board(Board original) {
        this.bitboard = new Bitboard(original.getBitboard());
        this.currentPlayer = original.currentPlayer;
        this.gameState = new GameState(getGameStateData()); // Use the new constructor
        this.check = gameState.isKingInCheck();
        this.checkmate = gameState.isKingInCheckmate();
        this.isStalemate = original.isStalemate;
    }


    /**
     * The initialization of the boards ensures each position of
     *       A    B    C    D    E    F    G    H
     *    +----+----+----+----+----+----+----+----+
     *  8 | 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 |  8th rank
     *    +----+----+----+----+----+----+----+----+
     *  7 | 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 |  7th rank
     *    +----+----+----+----+----+----+----+----+
     *  6 | 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 |  6th rank
     *    +----+----+----+----+----+----+----+----+
     *  5 | 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 |  5th rank
     *    +----+----+----+----+----+----+----+----+
     *  4 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 |  4th rank
     *    +----+----+----+----+----+----+----+----+
     *  3 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 |  3rd rank
     *    +----+----+----+----+----+----+----+----+
     *  2 |  8 |  9 | 10 | 11 | 12 | 13 | 14 | 15 |  2nd rank
     *    +----+----+----+----+----+----+----+----+
     *  1 |  0 |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  1st rank
     *    +----+----+----+----+----+----+----+----+
     *       A    B    C    D    E    F    G    H - file(s)
     * */

    /**
     *  The board initializing piece position would be
     *       A    B    C    D    E    F    G    H
     *    +----+----+----+----+----+----+----+----+
     *  8 |  r |  n |  b |  q |  k |  b |  n |  r |  8th rank
     *    +----+----+----+----+----+----+----+----+
     *  7 |  p |  p |  p |  p |  p |  p |  p |  p |  7th rank
     *    +----+----+----+----+----+----+----+----+
     *  6 |    |    |    |    |    |    |    |    |  6th rank
     *    +----+----+----+----+----+----+----+----+
     *  5 |    |    |    |    |    |    |    |    |  5th rank
     *    +----+----+----+----+----+----+----+----+
     *  4 |    |    |    |    |    |    |    |    |  4th rank
     *    +----+----+----+----+----+----+----+----+
     *  3 |    |    |    |    |    |    |    |    |  3rd rank
     *    +----+----+----+----+----+----+----+----+
     *  2 |  P |  P |  P |  P |  P |  P |  P |  P |  2nd rank
     *    +----+----+----+----+----+----+----+----+
     *  1 |  R |  N |  B |  Q |  K |  B |  N |  R |  1st rank
     *    +----+----+----+----+----+----+----+----+
     *       A    B    C    D    E    F    G    H - file(s)
     * */


    /**
     * Initialize the board
     */
    private void initializeBoard() {
        // White pieces
        bitboard.placePieceOnSquare(0, PieceType.ROOK, PieceColor.WHITE);    // A1
        bitboard.placePieceOnSquare(1, PieceType.KNIGHT, PieceColor.WHITE);  // B1
        bitboard.placePieceOnSquare(2, PieceType.BISHOP, PieceColor.WHITE);  // C1
        bitboard.placePieceOnSquare(3, PieceType.QUEEN, PieceColor.WHITE);   // D1
        bitboard.placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);    // E1
        bitboard.placePieceOnSquare(5, PieceType.BISHOP, PieceColor.WHITE);  // F1
        bitboard.placePieceOnSquare(6, PieceType.KNIGHT, PieceColor.WHITE);  // G1
        bitboard.placePieceOnSquare(7, PieceType.ROOK, PieceColor.WHITE);    // H1
        for (int i = 8; i <= 15; i++) {
            bitboard.placePieceOnSquare(i, PieceType.PAWN, PieceColor.WHITE); // A2 to H2
        }

        // Black pieces
        bitboard.placePieceOnSquare(56, PieceType.ROOK, PieceColor.BLACK);   // A8
        bitboard.placePieceOnSquare(57, PieceType.KNIGHT, PieceColor.BLACK); // B8
        bitboard.placePieceOnSquare(58, PieceType.BISHOP, PieceColor.BLACK); // C8
        bitboard.placePieceOnSquare(59, PieceType.QUEEN, PieceColor.BLACK);  // D8
        bitboard.placePieceOnSquare(60, PieceType.KING, PieceColor.BLACK);   // E8
        bitboard.placePieceOnSquare(61, PieceType.BISHOP, PieceColor.BLACK); // F8
        bitboard.placePieceOnSquare(62, PieceType.KNIGHT, PieceColor.BLACK); // G8
        bitboard.placePieceOnSquare(63, PieceType.ROOK, PieceColor.BLACK);   // H8
        for (int i = 48; i <= 55; i++) {
            bitboard.placePieceOnSquare(i, PieceType.PAWN, PieceColor.BLACK); // A7 to H7
        }

    }

    public PieceType getPieceTypeAtSquare(int square) {
        for (PieceColor color : PieceColor.values()) {
            for (PieceType type : PieceType.values()) {
                if (bitboard.isSquareOccupiedByPiece(square, type, color)) {
                    return type;
                }
            }
        }
        return null;
    }

    public PieceColor getPieceColorAtSquare(int square) {
        for (PieceColor color : PieceColor.values()) {
            for (PieceType type : PieceType.values()) {
                if (bitboard.isSquareOccupiedByPiece(square, type, color)) {
                    return color;
                }
            }
        }
        return null;
    }


    public long getPieceBitboard(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case PAWN -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhitePawns() : bitboard.getBlackPawns();
            case KNIGHT -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhiteKnights() : bitboard.getBlackKnights();
            case BISHOP -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhiteBishops() : bitboard.getBlackBishops();
            case ROOK -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhiteRooks() : bitboard.getBlackRooks();
            case QUEEN -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhiteQueens() : bitboard.getBlackQueens();
            case KING -> (pieceColor == PieceColor.WHITE) ? bitboard.getWhiteKing() : bitboard.getBlackKing();
        };
    }

    public void makeMove(Move move) {
        // Remove the piece from its current location
        bitboard.removePieceFromSquare(move.getFromSquare(), move.getPieceType(), move.getPieceColor());

        // If a capture occurs, remove the captured piece from the target square
        if (move.getCapturedPieceType() != null) {
            bitboard.removePieceFromSquare(move.getToSquare(), move.getCapturedPieceType(), currentPlayer.opposite());
        }

        // Place the piece on the new square
        bitboard.placePieceOnSquare(move.getToSquare(), move.getPieceType(), move.getPieceColor());

        updateGameState(currentPlayer, gameState.isKingInCheck(), gameState.isKingInCheckmate());
    }

    public void undoMove(Move move) {
        // Move the piece back to its original position
        bitboard.removePieceFromSquare(move.getToSquare(), move.getPieceType(), move.getPieceColor());
        bitboard.placePieceOnSquare(move.getFromSquare(), move.getPieceType(), move.getPieceColor());

        // If a capture occurred, restore the captured piece to the board
        if (move.getCapturedPieceType() != null) {
            bitboard.placePieceOnSquare(move.getToSquare(), move.getCapturedPieceType(), currentPlayer.opposite());
        }

        // Revert the player turn
        updateGameState(currentPlayer, gameState.isKingInCheck(), gameState.isKingInCheckmate());
    }


    public Board copy() {
        Board boardCopy = new Board();

        // Copy the current player
        boardCopy.setCurrentPlayer(this.getCurrentPlayer());

        // Copy the bitboards
        boardCopy.getBitboard().setWhitePawns(this.getBitboard().getWhitePawns());
        boardCopy.getBitboard().setWhiteKnights(this.getBitboard().getWhiteKnights());
        boardCopy.getBitboard().setWhiteBishops(this.getBitboard().getWhiteBishops());
        boardCopy.getBitboard().setWhiteRooks(this.getBitboard().getWhiteRooks());
        boardCopy.getBitboard().setWhiteQueens(this.getBitboard().getWhiteQueens());
        boardCopy.getBitboard().setWhiteKing(this.getBitboard().getWhiteKing());

        boardCopy.getBitboard().setBlackPawns(this.getBitboard().getBlackPawns());
        boardCopy.getBitboard().setBlackKnights(this.getBitboard().getBlackKnights());
        boardCopy.getBitboard().setBlackBishops(this.getBitboard().getBlackBishops());
        boardCopy.getBitboard().setBlackRooks(this.getBitboard().getBlackRooks());
        boardCopy.getBitboard().setBlackQueens(this.getBitboard().getBlackQueens());
        boardCopy.getBitboard().setBlackKing(this.getBitboard().getBlackKing());
        boardCopy.setCheck(this.check);
        boardCopy.setCheckmate(this.checkmate);
        boardCopy.setStalemate(this.isStalemate);
        boardCopy.setGameState(this.gameState);

        return boardCopy;
    }



    public void updateGameState(PieceColor nextPlayer, boolean isCheck, boolean isCheckmate) {
        this.currentPlayer = nextPlayer;
        this.check = isCheck;
        this.checkmate = isCheckmate;
    }

    public boolean isWhite() {
        return currentPlayer == PieceColor.WHITE;
    }


    public int getKingPosition(PieceColor color) {
        long kingBitboard = (color == PieceColor.WHITE) ? getPieceBitboard(PieceType.KING, PieceColor.WHITE) : getPieceBitboard(PieceType.KING, PieceColor.BLACK);
        if (kingBitboard == 0) {
            System.err.println("No king found for " + color + ". Bitboard is zero.");
            return -1;
        }
        System.out.println("King for " + color + " found at position " + Long.numberOfTrailingZeros(kingBitboard));
        return Long.numberOfTrailingZeros(kingBitboard);
    }


    public GameStateData getGameStateData() {
        return new GameStateData(currentPlayer, bitboard, getKingPosition(currentPlayer));
    }

    public boolean isGameOver() {
        return checkmate || isStalemate;
    }

    public long getHash() {
        long hash = 0L;
        // XOR all bitboards to combine their hashes
        hash ^= bitboard.getWhitePawns();
        hash ^= bitboard.getWhiteKnights();
        hash ^= bitboard.getWhiteBishops();
        hash ^= bitboard.getWhiteRooks();
        hash ^= bitboard.getWhiteQueens();
        hash ^= bitboard.getWhiteKing();
        hash ^= bitboard.getBlackPawns();
        hash ^= bitboard.getBlackKnights();
        hash ^= bitboard.getBlackBishops();
        hash ^= bitboard.getBlackRooks();
        hash ^= bitboard.getBlackQueens();
        hash ^= bitboard.getBlackKing();

        // Optionally, include the current player to move in the hash
        hash ^= currentPlayer == PieceColor.WHITE ? 0 : 1;

        return hash;
    }


}