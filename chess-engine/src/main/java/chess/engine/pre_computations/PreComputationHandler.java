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
        calculatePawnAttacks(PawnValidator.WHITE_PAWN_ATTACK_OFFSETS, WHITE_PAWN_ATTACKS);
        calculatePawnAttacks(PawnValidator.BLACK_PAWN_ATTACK_OFFSETS, BLACK_PAWN_ATTACKS);
        calculateKnightAttacks();
        slidingPieceAttackComputation(BishopValidator.BISHOP_OFFSETS, BISHOP_ATTACKS);
        slidingPieceAttackComputation(RookValidator.ROOK_OFFSETS, ROOK_ATTACKS);
        slidingPieceAttackComputation(QueenValidator.QUEEN_OFFSET, QUEEN_ATTACKS);
        calculateKingAttacks();
    }

    /**
     * Precomputes the pawn attacks for each square on a chess board based on the pawn's color
     * and stores the results in the provided array. This method calculates potential attack
     * positions for a pawn standing on any square from 0 (a1) to 63 (h8), considering the chess
     * board's boundaries and avoiding "wrapping" attacks from one side of the board to the other.
     *
     * @param attackOffsets The directional offsets defining the pawn's attack pattern. These
     *                      offsets should reflect the pawn's movement capabilities, which differ
     *                      based on color:
     *                      - For WHITE pawns, typical values might be {7, 9} representing attacks
     *                        to the northeast and northwest diagonally.
     *                      - For BLACK pawns, typical values might be {-7, -9} for southwest and
     *                        southeast diagonal attacks.
     * @param attacksArray An array to store the precomputed attack bitboards for each square.
     *                     Each bitboard is a 64-bit long, with a 1 in each position that a pawn
     *                     on the given square can attack. The index of the array corresponds to
     *                     the square number on a chessboard from 0 to 63.
     *
     * Example Usage:
     *     long[] whitePawnAttacks = new long[64];
     *     calculatePawnAttacks(PieceColor.WHITE, new int[] {7, 9}, whitePawnAttacks);
     *     // whitePawnAttacks now contains the attack bitboards for white pawns on all squares.
     */
    private static void calculatePawnAttacks(int[] attackOffsets, long[] attacksArray) {
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            int file = square % 8;

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
        int[] knightOffsets = KnightValidator.KNIGHT_OFFSETS;
        for (int square = 0; square < 64; square++) {
            long attacks = 0L;
            int rank = square / 8; // row
            int file = square % 8; // column
            for (int offset : knightOffsets) {
                int target = square + offset;
                if (target >= 0 && target < 64) { // Must be on the board
                    int targetRank = target / 8; // target row
                    int targetFile = target % 8; // target column
                    // Check that the target does not wrap around the chessboard
                    if (Math.abs(targetRank - rank) <= 2 && Math.abs(targetFile - file) <= 2 &&
                            (Math.abs(targetFile - file) == 1 || Math.abs(targetFile - file) == 2)) {
                        if (!(Math.abs(targetFile - file) == 2 && Math.abs(targetRank - rank) == 2)) {
                            attacks |= (1L << target);
                        }
                    }
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
