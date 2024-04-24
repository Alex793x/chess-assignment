package org.kea.chessbackend.chess_test_engine.engine.move_validation.piece_validators;


import org.kea.chessbackend.chess_test_engine.board.Board;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceColor;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceType;
import org.kea.chessbackend.chess_test_engine.engine.pre_computations.PreComputationHandler;

public final class BishopValidator {
    public static final int[] BISHOP_OFFSETS = {-9, -7, 7, 9};

    /**
     * Checks if the given move for a Bishop is valid.
     *
     * @param board   The current state of the chess board.
     * @param from    The source square of the move (in numerical format, 0-63).
     * @param to      The destination square of the move (in numerical format, 0-63).
     * @param playerColor The color of the player making the move (true for white, false for black).
     * @return true if the move is valid, false otherwise.
     */
    public static boolean isValidBishopMove(Board board, int from, int to, PieceColor playerColor) {
        if (!isBishopOnSquare(board, from, playerColor)) return false;
        long bishopAttacks = PreComputationHandler.BISHOP_ATTACKS[from];
        return (bishopAttacks & (1L << to)) != 0 &&
                (board.getPieceColorAtSquare(to) == null || board.getPieceColorAtSquare(to) != playerColor);
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