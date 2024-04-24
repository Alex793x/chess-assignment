package org.kea.chessbackend.chess.engine.move_validation.piece_validators;

import org.kea.chessbackend.chess.board.Board;
import org.kea.chessbackend.chess.board.enums.PieceColor;
import org.kea.chessbackend.chess.board.enums.PieceType;
import org.kea.chessbackend.chess.engine.pre_computations.PreComputationHandler;

public final class RookValidator {

    public static final int[] ROOK_OFFSETS = {-1, 1, -8, 8};

    /**
     * Checks if the given move for a Rook is valid.
     *
     * @param board   The current state of the chess board.
     * @param fromSquare    The source square of the move (in numerical format, 0-63).
     * @param toSquare      The destination square of the move (in numerical format, 0-63).
     * @param playerColor The color of the player making the move (true for white, false for black).
     * @return true if the move is valid, false otherwise.
     */
    public static boolean isValidRookMove(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        if (!isRookOnSquare(board, fromSquare, playerColor)) return false;

        long rooksAttacks = PreComputationHandler.ROOK_ATTACKS[fromSquare];

        return (rooksAttacks & (1L << toSquare)) != 0 &&
                (board.getPieceColorAtSquare(toSquare) == null || board.getPieceColorAtSquare(toSquare) != playerColor);
    }

    /**
     * Checks if there is a queen of the specified color on the given square.
     *
     * @param board   The current state of the chess board.
     * @param fromSquare  The square to check (in numerical format, 0-63).
     * @param playerColor The color of the queen to check for (WHITE or BLACK).
     * @return true if a knight of the specified color is on the square, false otherwise.
     */
    private static boolean isRookOnSquare(Board board, int fromSquare, PieceColor playerColor) {
        PieceType pieceType = board.getPieceTypeAtSquare(fromSquare);
        PieceColor pieceColor = board.getPieceColorAtSquare(fromSquare);

        return pieceType == PieceType.ROOK && pieceColor == playerColor;
    }



}
