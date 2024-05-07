package chess.engine.evaluation.piece_attack_evaluation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.Objects;

public class CaptureEvaluation {

    /**
     * Performs a Static Exchange Evaluation (SEE) to determine the material gain or loss
     * resulting from a series of captures on a single square.
     *
     * @param board the current chess board
     * @param square the square where the capture sequence starts
     * @param side the color of the side initiating the capture sequence
     * @return the net material gain or loss for the side initiating the capture sequence
     *
     * The Static Exchange Evaluation (SEE) is a method used to evaluate the outcome of a
     * series of captures on a single square. It recursively analyzes the consequences of
     * a capture by considering the opponent's best recapture options.
     *
     * The algorithm works as follows:
     * 1. Get the bitboard of all attackers on the specified square for the given side.
     * 2. If there are no attackers, return 0 (no capture sequence).
     * 3. Find the least valuable attacker using the {@link #getSmallestAttacker} method.
     * 4. Remove the attacking piece from the board.
     * 5. Calculate the value of the captured piece using its mid-game value.
     * 6. Recursively evaluate the opponent's best recapture sequence by calling
     *    {@link #staticExchangeEvaluation} with the opposite side.
     * 7. The value of the capture sequence is the maximum of 0 and the difference between
     *    the value of the captured piece and the opponent's best recapture sequence.
     * 8. Undo the capture by placing the attacking piece back on the board.
     * 9. Return the net material gain or loss for the side initiating the capture sequence.
     *
     * Note: This method modifies the board state during the evaluation process and restores
     * it before returning. It should not be used directly to make moves on the board.
     *
     * The SEE value can be used to make informed decisions about capture moves during move
     * generation and evaluation. A positive SEE value indicates a favorable capture sequence
     * for the initiating side, while a negative value indicates a loss of material.
     */
    public static int staticExchangeEvaluation(Board board, int square, PieceColor side) {
        int netValue = 0;
        PieceColor opponentColor = side.opposite();

        // If no opponent piece is present, return 0
        if (board.getPieceColorAtSquare(square) == side) {
            return 0;
        }

        long attackers = getAttackers(board, square, side);

        while (attackers != 0) {
            PieceType smallestAttacker = getSmallestAttacker(board, attackers, side);
            if (smallestAttacker == null) {
                break;  // No more attackers
            }

            // Calculate the value of the opponent's piece at the square
            int capturedValue = Objects.requireNonNull(board.getPieceTypeAtSquare(square)).getMidGameValue();

            // Simulate capture: remove the smallest attacker
            board.getBitboard().removePieceFromSquare(square, smallestAttacker, side);
            attackers = updateAttackers(board, square, side);

            // Recursively evaluate opponent's best recapture sequence
            int counterValue = staticExchangeEvaluation(board, square, opponentColor);

            // Undo the capture
            board.getBitboard().placePieceOnSquare(square, smallestAttacker, side);

            // Update net value considering this capture sequence
            netValue = capturedValue - counterValue;

            if (netValue <= 0) {
                break;  // Stop if the sequence is not favorable
            }
        }

        return netValue;
    }

    private static long updateAttackers(Board board, int square, PieceColor side) {
        // This method would recalculate all attackers after a change on the board.
        return getAttackers(board, square, side);
    }


    private static long getAttackers(Board board, int square, PieceColor side) {
        long occupancies = board.getBitboard().getOccupancies(side);
        long pawnAttackers = (side == PieceColor.WHITE ? PreComputationHandler.WHITE_PAWN_ATTACKS[square] : PreComputationHandler.BLACK_PAWN_ATTACKS[square]) & occupancies;
        long knightAttackers = PreComputationHandler.KNIGHT_ATTACKS[square] & occupancies;
        long bishopAttackers = PreComputationHandler.getBishopAttacks(square, occupancies);
        long rookAttackers = PreComputationHandler.getRookAttacks(square, occupancies);
        long queenAttackers = PreComputationHandler.getQueenAttacks(square, occupancies);
        long kingAttackers = PreComputationHandler.KING_ATTACKS[square] & occupancies;

        return pawnAttackers | knightAttackers | bishopAttackers | rookAttackers | queenAttackers | kingAttackers;
    }

    private static PieceType getSmallestAttacker(Board board, long attackers, PieceColor side) {
        if ((attackers & board.getPieceBitboard(PieceType.PAWN, side)) != 0) {
            return PieceType.PAWN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KNIGHT, side)) != 0) {
            return PieceType.KNIGHT;
        } else if ((attackers & board.getPieceBitboard(PieceType.BISHOP, side)) != 0) {
            return PieceType.BISHOP;
        } else if ((attackers & board.getPieceBitboard(PieceType.ROOK, side)) != 0) {
            return PieceType.ROOK;
        } else if ((attackers & board.getPieceBitboard(PieceType.QUEEN, side)) != 0) {
            return PieceType.QUEEN;
        } else if ((attackers & board.getPieceBitboard(PieceType.KING, side)) != 0) {
            return PieceType.KING;
        }
        return null;
    }
}