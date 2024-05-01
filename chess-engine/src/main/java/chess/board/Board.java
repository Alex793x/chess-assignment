package chess.board;


import chess.board.enums.GamePhase;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.move_validation.interfaces.PieceValidator;
import chess.engine.move_validation.service.MoveValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public final class Board {

    private Bitboard bitboard;
    private PieceColor currentPlayer;
    private GamePhase gamePhase;
    private boolean check;
    private boolean checkmate;

    public Board() {
        this.bitboard = new Bitboard();
        this.currentPlayer = PieceColor.WHITE;
        this.check = false;
        this.checkmate = false;
        initializeBoard();
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
        for (int i = 7; i <= 15; i++) {
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


    public void movePiece(int fromSquare, int toSquare) {
        if (!PieceValidator.isWithinBoardBounds(toSquare)) return;
        PieceType pieceType = getPieceTypeAtSquare(fromSquare);
        PieceColor pieceColor = getPieceColorAtSquare(fromSquare);

        // Validate Move Actions
        if (pieceType == PieceType.PAWN && !MoveValidator.validatePawnMoves(this, fromSquare, toSquare, pieceColor))
            return;
        if (pieceType == PieceType.KNIGHT && !MoveValidator.validateKnightMoves(this, fromSquare, toSquare, pieceColor))
            return;
        if (pieceType == PieceType.BISHOP && !MoveValidator.validateBishopMoves(this, fromSquare, toSquare, pieceColor))
            return;
        if (pieceType == PieceType.ROOK && !MoveValidator.validateRookMoves(this, fromSquare, toSquare, pieceColor))
            return;
        if (pieceType == PieceType.QUEEN && !MoveValidator.validateQueenMoves(this, fromSquare, toSquare, pieceColor))
            return;
        if (pieceType == PieceType.KING && !MoveValidator.validateKingMoves(this, fromSquare, toSquare, pieceColor))
            return;

        bitboard.removePieceFromSquare(fromSquare, pieceType, pieceColor);
        bitboard.placePieceOnSquare(toSquare, pieceType, pieceColor);
    }

    public PieceType getPieceTypeAtSquare(int square) {
        for (PieceType pieceType : PieceType.values()) {
            if (bitboard.isSquareOccupiedByPiece(square, pieceType, PieceColor.WHITE) ||
                    bitboard.isSquareOccupiedByPiece(square, pieceType, PieceColor.BLACK)) {
                return pieceType;
            }
        }
        return null;
    }

    public PieceColor getPieceColorAtSquare(int square) {
        for (PieceColor pieceColor : PieceColor.values()) {
            for (PieceType pieceType : PieceType.values()) {
                if (bitboard.isSquareOccupiedByPiece(square, pieceType, pieceColor)) {
                    return pieceColor;
                }
            }
        }
        return null;
    }

    public void updateGameState(PieceColor nextPlayer, boolean isCheck, boolean isCheckmate) {
        this.currentPlayer = nextPlayer;
        this.check = isCheck;
        this.checkmate = isCheckmate;
    }

    public boolean isWhite() {
        return currentPlayer == PieceColor.WHITE;
    }

    public boolean isSquareEmpty(int square, long allOccupancies) {
        return (allOccupancies & (1L << square)) == 0;
    }

    public boolean isSquareOccupiedByEnemy(int square, long enemyOccupancies) {
        return (enemyOccupancies & (1L << square)) != 0;
    }

    public boolean isWithinBoardBounds(int square) {
        return square >= 0 && square < 64;
    }

    public int getKingPositiion(PieceColor color) {
        long kingBitboard = color == PieceColor.WHITE ? bitboard.getWhiteKing() : bitboard.getBlackKing();
        return bitboardIndex(kingBitboard);
    }

    private int bitboardIndex(long bitboard) {
        if (bitboard == 0) {
            return -1; // No pieces of this type on the board
        }
        int index = 0;
        while ((bitboard & 1) == 0) {
            bitboard >>>= 1;
            index++;
        }
        return index;
    }
}