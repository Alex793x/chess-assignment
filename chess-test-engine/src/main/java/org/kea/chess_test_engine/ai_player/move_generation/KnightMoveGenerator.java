package org.kea.chess_test_engine.ai_player.move_generation;

import org.kea.chess_test_engine.board.Board;
import org.kea.chess_test_engine.board.enums.PieceColor;
import org.kea.chess_test_engine.engine.pre_computations.PreComputationHandler;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveGenerator {

    /**
     * Generates all the valid moves for a knight on the specified square.
     *
     * @param board   The current state of the chess board.
     * @param square  The square where the knight is located (in numerical format, 0-63).
     * @param playerColor The color of the knight (WHITE or BLACK).
     * @return an array of valid destination squares (in numerical format, 0-63).
     */
    public static int[] generateKnightMoves(Board board, int square, PieceColor playerColor) {
        List<Integer> validMoves = new ArrayList<>();
        long knightAttacks = PreComputationHandler.KNIGHT_ATTACKS[square];

        // Iterate through the set bits in the knight attack bitboard
        for (int i = 0; i < 64; i++) {
            if ((knightAttacks & (1L << i)) != 0) {
                if (board.getPieceColorAtSquare(i) == null || board.getPieceColorAtSquare(i) != playerColor) {
                    validMoves.add(i);
                }
            }
        }

        return validMoves.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}