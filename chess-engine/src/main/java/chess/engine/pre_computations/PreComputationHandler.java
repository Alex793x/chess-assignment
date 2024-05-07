package chess.engine.pre_computations;

import chess.engine.move_validation.interfaces.PieceValidator;
import chess.engine.move_validation.piece_validators.*;

import static chess.engine.move_validation.piece_validators.BishopValidator.BISHOP_OFFSETS;

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
    public static final long[][] BISHOP_ATTACK_MASKS = new long[64][];
    public static final long[][] ROOK_ATTACK_MASKS = new long[64][];
    public static final long[] KING_ATTACKS = new long[64];
    public static final long[] BORDER_MASK = {
            0xfefefefefefefefeL, // NW and SE mask (file != 0)
            0x7f7f7f7f7f7f7f7fL, // NE and SW mask (file != 7)
            0x7f7f7f7f7f7f7f7fL, // SE mask
            0xfefefefefefefefeL  // SW mask
    };

    static {
        calculatePawnAttacks(PawnValidator.WHITE_PAWN_ATTACK_OFFSETS, WHITE_PAWN_ATTACKS);
        calculatePawnAttacks(PawnValidator.BLACK_PAWN_ATTACK_OFFSETS, BLACK_PAWN_ATTACKS);
        calculateKnightAttacks();
        calculateBishopAttackMasks();
        calculateRookAttackMasks();
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
     * Calculates and stores the attack masks for bishops on every square of the chessboard.
     */
    private static void calculateBishopAttackMasks() {
        for (int square = 0; square < 64; square++) {
            BISHOP_ATTACK_MASKS[square] = calculateBishopAttackMasksForSquare(square);
        }
    }

    /**
     * Calculates the attack masks for a bishop on a specific square.
     *
     * @param square The square index of the bishop.
     * @return An array of long values representing the attack masks for each diagonal direction.
     */
    private static long[] calculateBishopAttackMasksForSquare(int square) {
        // Initialize an array to store potential moves in four diagonal directions
        long[] diagonalMasks = new long[4];

        // Determine the bishop's current position on the board
        int startingRank = square / 8;  // Rank (row) is obtained by dividing by 8
        int startingFile = square % 8;  // File (column) is obtained by modulo 8

        // Define the directions in which the bishop can move
        final int NORTHEAST = 0;
        final int NORTHWEST = 1;
        final int SOUTHEAST = 2;

        // Loop through each diagonal direction
        for (int direction = 0; direction < 4; direction++) {
            // Set the initial position for the current direction
            int currentRank = startingRank;
            int currentFile = startingFile;

            // Continue moving in the direction until reaching the edge of the board
            while (true) {
                // Increment the rank and file according to the current diagonal direction
                if (direction == NORTHEAST) {
                    currentRank++;  // Moving northeast increases the rank
                    currentFile++;  // and increases the file
                } else if (direction == NORTHWEST) {
                    currentRank++;  // Moving northwest increases the rank
                    currentFile--;  // but decreases the file
                } else if (direction == SOUTHEAST) {
                    currentRank--;  // Moving southeast decreases the rank
                    currentFile++;  // but increases the file
                } else {
                    currentRank--;  // Moving southwest decreases both the rank
                    currentFile--;  // and the file
                }

                // Check if the new position is still within the board limits
                if (currentRank < 0 || currentRank > 7 || currentFile < 0 || currentFile > 7) {
                    break;  // If out of bounds, break the loop and stop adding positions
                }

                // Calculate the square index from the current rank and file
                int targetSquare = currentRank * 8 + currentFile;
                // Set the bit corresponding to the target square in the mask for this direction
                diagonalMasks[direction] |= 1L << targetSquare;
            }
        }

        // Return the array containing bitboards for each of the four diagonal directions
        return diagonalMasks;
    }

    /**
     * Calculates and stores the attack masks for rooks on every square of the chessboard.
     */
    private static void calculateRookAttackMasks() {
        for (int square = 0; square < 64; square++) {
            ROOK_ATTACK_MASKS[square] = calculateRookAttackMasksForSquare(square);
        }
    }

    /**
     * Calculates the attack masks for a rook on a specific square.
     *
     * @param square The square index of the rook.
     * @return An array of long values representing the attack masks for each direction.
     */
    private static long[] calculateRookAttackMasksForSquare(int square) {
        // Initialize an array to store potential moves in four directions
        long[] directionalMasks = new long[4];

        // Determine the rook's current position on the board
        int startingRank = square / 8;  // Rank (row) is obtained by dividing by 8
        int startingFile = square % 8;  // File (column) is obtained by modulo 8

        // Define the directions in which the rook can move
        final int NORTH = 0;
        final int SOUTH = 1;
        final int EAST = 2;

        // Movement loop for each direction
        for (int direction = 0; direction < 4; direction++) {
            // Set the initial position for the current direction
            int currentRank = startingRank;
            int currentFile = startingFile;

            // Continue moving in the direction until reaching the edge of the board
            while (true) {
                // Move one step in the current direction
                if (direction == NORTH) {
                    currentRank++;  // Moving north increases the rank
                } else if (direction == SOUTH) {
                    currentRank--;  // Moving south decreases the rank
                } else if (direction == EAST) {
                    currentFile++;  // Moving east increases the file
                } else {
                    currentFile--;  // Moving west decreases the file
                }

                // Check if the new position is still within the board limits
                if (currentRank < 0 || currentRank > 7 || currentFile < 0 || currentFile > 7) {
                    break;  // If out of bounds, break the loop and stop adding positions
                }

                // Calculate the square index from the rank and file
                int targetSquare = currentRank * 8 + currentFile;
                // Set the bit corresponding to the target square in the mask for this direction
                directionalMasks[direction] |= 1L << targetSquare;
            }
        }

        // Return the array containing bitboards for each of the four directions
        return directionalMasks;
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



    /**
     * Calculates the attack squares for a bishop located at a specific square on the board.
     * @param square The index of the square where the bishop is placed, ranging from 0 to 63.
     * @param occupancy A bitmask representing the occupied squares of the board; 1s indicate occupied squares.
     * @return A bitmask representing all the squares attacked by the bishop from the given position, considering blockers.
     */
    public static long getBishopAttacks(int square, long occupancy) {
        long attacks = 0L; // This will hold the final set of attack squares as a bitmask.

        // Loop through each of the four possible diagonal directions a bishop can move.
        for (int i = 0; i < 4; i++) {
            int directionOffset = BISHOP_OFFSETS[i]; // The directional increment for the current diagonal
            long edgeMask = BORDER_MASK[i]; // The edge mask prevents the bishop attack from wrapping around the board

            // Calculate attacks in one direction until the edge of the board or a blocker is encountered.
            for (int nextSquare = square + directionOffset;
                 (nextSquare >= 0) && (nextSquare < 64) && ((1L << nextSquare) & edgeMask) != 0;
                 nextSquare += directionOffset) {

                attacks |= (1L << nextSquare); // Add the square to the attack set.

                // Check if the next square is occupied by another piece which blocks further movement.
                if ((occupancy & (1L << nextSquare)) != 0) {
                    // If a piece is on the next square, the bishop's path is blocked and cannot extend further.
                    break;
                }
            }
        }

        return attacks; // Return the calculated attack squares as a bitmask.
    }


    /**
     * Calculates the rook attacks from a given square considering the occupancy of the board.
     * Source: <a href=https://chat.openai.com >OpenAI</a>
     * @param square The square index of the rook.
     * @param occupancy The bitboard representing the occupancy of the board.
     * @return The bitboard representing the rook attacks.
     */
    public static long getRookAttacks(int square, long occupancy) {
        // Initialize variable to hold the final attack squares
        long attacks;

        // Calculate the rank (row) and file (column) based on the square index
        int rank = square / 8;
        int file = square % 8;

        // Create masks for the rook's current rank and file
        long rankMask = 0xFFL << (rank * 8); // Covers all squares in the rook's rank
        long fileMask = 0x0101010101010101L << file; // Covers all squares in the rook's file

        // Apply the masks to the occupancy to isolate piece positions on the rook's rank and file
        long rankOccupancy = occupancy & rankMask;
        long fileOccupancy = occupancy & fileMask;

        // Calculate attacks along the rank
        // Uses a combination of bit manipulation and bit reversal to determine clear paths considering blockers
        long rankAttacks = ((rankOccupancy - (2L << square)) ^
                Long.reverse(Long.reverse(rankOccupancy) - (2L << (63 - square))))
                & rankMask;

        // Calculate attacks along the file
        // Similar method as rank calculation, adjusted for vertical movement
        long fileAttacks = ((fileOccupancy - (2L << square)) ^
                Long.reverse(Long.reverse(fileOccupancy) - (2L << (63 - square))))
                & fileMask;

        // Combine the attacks from both rank and file into a single bitboard
        attacks = (rankAttacks | fileAttacks);

        // Return the combined attacks bitboard
        return attacks;
    }


    /**
     * Calculates the queen attacks from a given square considering the occupancy of the board.
     *
     * @param square The square index of the queen.
     * @param occupancy The bitboard representing the occupancy of the board.
     * @return The combined bitboard of bishops and rooks attacks for each square - Since queen can move what equals to bishop and rook combined
     */
    public static long getQueenAttacks(int square, long occupancy) {
        return getBishopAttacks(square, occupancy) | getRookAttacks(square, occupancy);
    }

}