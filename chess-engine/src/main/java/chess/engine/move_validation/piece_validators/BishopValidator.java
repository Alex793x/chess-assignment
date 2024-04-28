package chess.engine.move_validation.piece_validators;


import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

public final class BishopValidator {
    public static final int[] BISHOP_OFFSETS = {-9, -7, 7, 9};

    /**
     * Checks if the given move for a Bishop is valid.
     *
     * @param board   The current state of the chess board.
     * @param fromSquare    The source square of the move (in numerical format, 0-63).
     * @param toSquare      The destination square of the move (in numerical format, 0-63).
     * @param playerColor The color of the player making the move (true for white, false for black).
     * @return true if the move is valid, false otherwise.
     */
    public static boolean isValidBishopMove(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        if (!isBishopOnSquare(board, fromSquare, playerColor)) return false;
        long bishopAttacks = PreComputationHandler.BISHOP_ATTACKS[fromSquare];
        return (bishopAttacks & (1L << toSquare)) != 0 &&
                (board.getPieceColorAtSquare(toSquare) == null || board.getPieceColorAtSquare(toSquare) != playerColor);
    }

    /**
     * Checks if there is a bishop of the specified color on the given square.
     *
     * @param board   The current state of the chess board.
     * @param square  The square to check (in numerical format, 0-63).
     * @param playerColor The color of the bishop to check for (WHITE or BLACK).
     * @return true if a knight of the specified color is on the square, false otherwise.
     */
    private static boolean isBishopOnSquare(Board board, int square, PieceColor playerColor) {
        PieceType pieceType = board.getPieceTypeAtSquare(square);
        PieceColor pieceColor = board.getPieceColorAtSquare(square);

        return pieceType == PieceType.BISHOP && pieceColor == playerColor;
    }
}