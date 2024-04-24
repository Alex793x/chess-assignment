package org.kea.chess_test_engine.engine.move_validation.piece_validators;

import org.kea.chess_test_engine.board.Board;
import org.kea.chess_test_engine.board.enums.PieceColor;
import org.kea.chess_test_engine.board.enums.PieceType;
import org.kea.chess_test_engine.engine.pre_computations.PreComputationHandler;

public final class PawnValidator {
    public static final int[] WHITE_PAWN_ATTACK_OFFSETS = {7, 9};
    public static final int[] BLACK_PAWN_ATTACK_OFFSETS = {-9, -7};
    public static final int WHITE_PAWN_FORWARD_OFFSET = 8;
    public static final int BLACK_PAWN_FORWARD_OFFSET = -8;
    public static final int WHITE_PAWN_DOUBLE_FORWARD_OFFSET = 16;
    public static final int BLACK_PAWN_DOUBLE_FORWARD_OFFSET = -16;


    /**
     * Checks if the given move for a Pawn is valid.
     *
     * @param board       The current state of the chess board.
     * @param fromSquare  The source square of the move (in numerical format, 0-63).
     * @param toSquare    The destination square of the move (in numerical format, 0-63).
     * @param playerColor The color of the player making the move (WHITE or BLACK).
     * @return true if the move is valid, false otherwise.
     */
    public static boolean isValidPawnMove(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        if (!isPawnOnSquare(board, fromSquare, playerColor)) return false;

        int forwardOffset = playerColor == PieceColor.WHITE
                ? WHITE_PAWN_FORWARD_OFFSET
                : BLACK_PAWN_FORWARD_OFFSET;

        int doubleForwardOffset = playerColor == PieceColor.WHITE
                ? WHITE_PAWN_DOUBLE_FORWARD_OFFSET
                : BLACK_PAWN_DOUBLE_FORWARD_OFFSET;

        long pawnAttacks = playerColor == PieceColor.WHITE
                ? PreComputationHandler.WHITE_PAWN_ATTACKS[fromSquare]
                : PreComputationHandler.BLACK_PAWN_ATTACKS[fromSquare];

        // Check if the move is a valid forward move
        if (toSquare == fromSquare + forwardOffset && board.getPieceTypeAtSquare(toSquare) == null) {
            return true;
        }

        // Check if the move is a valid double forward move from the starting rank
        if (toSquare == fromSquare + doubleForwardOffset && board.getPieceTypeAtSquare(toSquare) == null &&
                ((playerColor == PieceColor.WHITE && fromSquare >= 8 && fromSquare <= 15) ||
                        (playerColor == PieceColor.BLACK && fromSquare >= 48 && fromSquare <= 55))) {
            return true;
        }

        // Check if the move is a valid capture
        return (pawnAttacks & (1L << toSquare)) != 0 && board.getPieceColorAtSquare(toSquare) != null &&
                board.getPieceColorAtSquare(toSquare) != playerColor;
    }

    /**
     * Checks if there is a pawn of the specified color on the given square.
     *
     * @param board       The current state of the chess board.
     * @param square      The square to check (in numerical format, 0-63).
     * @param playerColor The color of the pawn to check for (WHITE or BLACK).
     * @return true if a pawn of the specified color is on the square, false otherwise.
     */
    public static boolean isPawnOnSquare(Board board, int square, PieceColor playerColor) {
        PieceType pieceType = board.getPieceTypeAtSquare(square);
        PieceColor pieceColor = board.getPieceColorAtSquare(square);

        return pieceType == PieceType.PAWN && pieceColor == playerColor;
    }
}