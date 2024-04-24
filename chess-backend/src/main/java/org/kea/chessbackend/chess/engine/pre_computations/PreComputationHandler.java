package org.kea.chessbackend.chess.engine.pre_computations;


import org.kea.chessbackend.chess.board.enums.PieceColor;
import org.kea.chessbackend.chess.engine.move_validation.interfaces.PieceValidator;
import org.kea.chessbackend.chess.engine.move_validation.piece_validators.*;

public final class PreComputationHandler {

    public static final long[] WHITE_PAWN_ATTACKS = new long[64];
    public static final long[] BLACK_PAWN_ATTACKS = new long[64];
    public static final long[] KNIGHT_ATTACKS = new long[64];
    public static final long[] BISHOP_ATTACKS = new long[64];
    public static final long[] ROOK_ATTACKS = new long[64];
    public static final long[] QUEEN_ATTACKS = new long[64];
    public static final long[] KING_ATTACKS = new long[64];

    static {
        calculatePawnAttacks(PieceColor.WHITE, PawnValidator.WHITE_PAWN_ATTACK_OFFSETS, WHITE_PAWN_ATTACKS);
        calculatePawnAttacks(PieceColor.BLACK, PawnValidator.BLACK_PAWN_ATTACK_OFFSETS, BLACK_PAWN_ATTACKS);
        calculateKnightAttacks();
        slidingPieceAttackComputation(BishopValidator.BISHOP_OFFSETS, BISHOP_ATTACKS);
        slidingPieceAttackComputation(RookValidator.ROOK_OFFSETS, ROOK_ATTACKS);
        slidingPieceAttackComputation(QueenValidator.QUEEN_OFFSET, QUEEN_ATTACKS);
        calculateKingAttacks();
    }

    /**
     * Precomputes the pawn attacks for each square and stores them in the respective attack arrays.
     *
     * @param playerColor     The color of the pawn (WHITE or BLACK).
     * @param attackOffsets   The attack offsets for the given pawn color.
     * @param attacksArray    The array to store the precomputed pawn attacks.
     */
    private static void calculatePawnAttacks(PieceColor playerColor, int[] attackOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : attackOffsets) {
                int destSquare = square + offset;
                if ((playerColor == PieceColor.WHITE && destSquare >= 0 && destSquare < 56) ||
                        (playerColor == PieceColor.BLACK && destSquare >= 8 && destSquare < 64)) {
                    attacks |= (1L << destSquare);
                }
            }
            attacksArray[square] = attacks;
        }
    }

    private static void calculateKnightAttacks() {
        // Pre-compute the attacks for each square
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : KnightValidator.KNIGHT_OFFSETS) {
                int destSquare = square + offset;

                // Check if the destination square is within bounds and the move does not wrap around the board edges
                int absoluteBoundary = Math.abs((destSquare % 8) - (square % 8));
                if (PieceValidator.isWithinBoardBounds(destSquare) &&
                        absoluteBoundary != 0 && // Ensure it does not wrap around
                        Math.abs((destSquare / 8) - (square / 8)) <= 2 && // Within 2 rows
                        absoluteBoundary <= 2) { // Within 2 columns
                    attacks |= (1L << destSquare);
                }
            }
            PreComputationHandler.KNIGHT_ATTACKS[square] = attacks;
        }
    }

    private static void slidingPieceAttackComputation(int[] pieceOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : pieceOffsets) {
                int destSquare = square;
                while (true) {
                    destSquare += offset;
                    if (!PieceValidator.isWithinBoardBounds(destSquare) || Math.abs(destSquare % 8 - square % 8) > 1) {
                        break;
                    }
                    attacks |= (1L << destSquare);
                }
            }
            attacksArray[square] = attacks;
        }
    }

    private static void calculateKingAttacks() {
        // Pre-compute the attacks for each square
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : KingValidator.KING_OFFSETS) {
                int destSquare = square + offset;

                // Stop iterating when one square away in any direction
                if (Math.abs(destSquare / 8 - square / 8) > 1 ||
                        Math.abs(destSquare % 8 - square % 8) > 1) {
                    break;
                }

                // Mark attackable square within bounds
                if (PieceValidator.isWithinBoardBounds(destSquare)) {
                    attacks |= (1L << destSquare);
                }
            }
            PreComputationHandler.KING_ATTACKS[square] = attacks;
        }
    }

}
