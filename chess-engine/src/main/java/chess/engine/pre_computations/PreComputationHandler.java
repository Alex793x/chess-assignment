package chess.engine.pre_computations;

import chess.engine.move_validation.interfaces.PieceValidator;
import chess.engine.move_validation.piece_validators.*;

/**
 * Handles precomputations of attack bitboards for different chess pieces. This class
 * is designed to optimize the performance of move generation during gameplay by
 * precalculating potential moves for each piece type on every square of the chessboard.
 */
public final class PreComputationHandler {

    // Bitboard arrays to store precomputed moves for each piece type
    public static final long[] WHITE_PAWN_ATTACKS = new long[64];
    public static final long[] BLACK_PAWN_ATTACKS = new long[64];
    public static final long[] KNIGHT_ATTACKS = new long[64];
    public static final long[] BISHOP_ATTACKS = new long[64];
    public static final long[] ROOK_ATTACKS = new long[64];
    public static final long[] QUEEN_ATTACKS = new long[64];
    public static final long[] KING_ATTACKS = new long[64];

    static {
        calculatePawnAttacks(PawnValidator.WHITE_PAWN_ATTACK_OFFSETS, WHITE_PAWN_ATTACKS);
        calculatePawnAttacks(PawnValidator.BLACK_PAWN_ATTACK_OFFSETS, BLACK_PAWN_ATTACKS);
        calculateKnightAttacks();
        slidingPieceAttackComputation(BishopValidator.BISHOP_OFFSETS, BISHOP_ATTACKS);
        slidingPieceAttackComputation(RookValidator.ROOK_OFFSETS, ROOK_ATTACKS);
        slidingPieceAttackComputation(QueenValidator.QUEEN_OFFSET, QUEEN_ATTACKS);
        calculateKingAttacks();
    }

    /**
     * Calculates and stores the possible attack squares for pawns of a given color.
     * This method ensures that pawn attacks don't wrap around the chessboard.
     *
     * @param attackOffsets Array of integers representing directional offsets for pawn attacks.
     * @param attacksArray Array of long integers to store the bitboards representing attack positions.
     */
    private static void calculatePawnAttacks(int[] attackOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            int file = square % 8;
            for (int offset : attackOffsets) {
                int destSquare = square + offset;
                int destFile = destSquare % 8;
                if (Math.abs(destFile - file) > 1) continue;
                if (destSquare >= 0 && destSquare < 64) {
                    attacks |= (1L << destSquare);
                }
            }
            attacksArray[square] = attacks;
        }
    }

    /**
     * Calculates and stores the possible move squares for knights on every square of the chessboard.
     * Ensures that moves do not go off the edges of the board.
     */
    private static void calculateKnightAttacks() {
        int[] knightOffsets = KnightValidator.KNIGHT_OFFSETS;
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : knightOffsets) {
                int target = square + offset;
                if (target >= 0 && target < 64 && Math.abs((target % 8) - (square % 8)) <= 2) {
                    attacks |= (1L << target);
                }
            }
            KNIGHT_ATTACKS[square] = attacks;
        }
    }

    /**
     * Generic method to compute sliding moves for rooks, bishops, and queens.
     * The method iterates through each direction the piece can move and calculates the valid squares.
     *
     * @param pieceOffsets Array of directional offsets corresponding to the movement of the piece.
     * @param attacksArray Array to store the computed attack bitboards.
     */
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

    /**
     * Calculates and stores the possible move squares for kings on every square of the chessboard.
     * Ensures that moves do not go off the edges of the board.
     */
    private static void calculateKingAttacks() {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            for (int offset : KingValidator.KING_OFFSETS) {
                int toSquare = square + offset;
                if (toSquare >= 0 && toSquare < 64 && Math.abs((toSquare % 8) - (square % 8)) <= 1) {
                    attacks |= (1L << toSquare);
                }
            }
            KING_ATTACKS[square] = attacks;
        }
    }
}
