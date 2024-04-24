package org.kea.chess.engine.move_validation.piece_validators;

import org.kea.chess.board.Board;
import org.kea.chess.board.enums.PieceColor;
import org.kea.chess.board.enums.PieceType;
import org.kea.chess.engine.pre_computations.PreComputationHandler;

public final class KnightValidator {
    public static final int[] KNIGHT_OFFSETS = {-17, -15, -10, -6, 6, 10, 15, 17};

    /**
     * Checks if the given move for a Knight is valid.
     *
     * @param board   The current state of the chess board.
     * @param from    The source square of the move (in numerical format, 0-63).
     * @param to      The destination square of the move (in numerical format, 0-63).
     * @param playerColor The color of the player making the move (true for white, false for black).
     * @return true if the move is valid, false otherwise.
     */
    public static boolean isValidKnightMove(Board board, int from, int to, PieceColor playerColor) {
        if (!isKnightOnSquare(board, from, playerColor)) return false;

        // Retrieve the pre-computed knight attack bitboard for the 'from' square
        long knightAttacks = PreComputationHandler.KNIGHT_ATTACKS[from];

        // Check if the destination square 'to' is set in the knight attack bitboard
        // This is done by creating a bitmask with a single bit set at the 'to' position (1L << to)
        // and then performing a bitwise AND (&) with the knight attack bitboard.
        // If the result is non-zero, it means the destination square is within the knight's valid moves.
        return (knightAttacks & (1L << to)) != 0 &&
                (board.getPieceColorAtSquare(to) == null || board.getPieceColorAtSquare(to) != playerColor);
    }

    /**
     * Checks if there is a knight of the specified color on the given square.
     *
     * @param board   The current state of the chess board.
     * @param square  The square to check (in numerical format, 0-63).
     * @param playerColor The color of the knight to check for (WHITE or BLACK).
     * @return true if a knight of the specified color is on the square, false otherwise.
     */
    private static boolean isKnightOnSquare(Board board, int square, PieceColor playerColor) {
        PieceType pieceType = board.getPieceTypeAtSquare(square);
        PieceColor pieceColor = board.getPieceColorAtSquare(square);
        return pieceType == PieceType.KNIGHT && pieceColor == playerColor;
    }

}