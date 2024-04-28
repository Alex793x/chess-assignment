package chess.engine.pre_computations;


import chess.board.enums.PieceColor;
import chess.engine.move_validation.interfaces.PieceValidator;
import chess.engine.move_validation.piece_validators.*;

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
            int file = square % 8;  // File calculation (A=0, B=1, ..., H=7)

            for (int offset : attackOffsets) {
                int destSquare = square + offset;
                int destFile = (square + offset) % 8;

                // Check to ensure attack does not wrap around the board
                if (Math.abs(destFile - file) > 1) continue;

                // Validating square boundaries for both colors
                if (destSquare >= 0 && destSquare < 64) {
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
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;

            int row = square / 8, col = square % 8;
            for (int offset : KingValidator.KING_OFFSETS) {
                int toSquare = square + offset;
                int newRow = toSquare / 8, newCol = toSquare % 8;

                // Validate toSquare is within bounds and doesn't wrap around
                if (toSquare >= 0 && toSquare < 64 && Math.abs(newCol - col) <= 1 && Math.abs(newRow - row) <= 1) {
                    attacks |= 1L << toSquare;
                }
            }
            KING_ATTACKS[square] = attacks;
        }
    }

}
