package util.precompuation;

import java.util.BitSet;

public class PreComputedData {

    public static final BitSet[] whitePawnMoves = new BitSet[64];
    public static final BitSet[] blackPawnMoves = new BitSet[64];
    public static final BitSet[] knightMoves = new BitSet[64];
    public static final BitSet[] bishopMoves = new BitSet[64];
    public static final BitSet[] rookMoves = new BitSet[64];
    public static final BitSet[] kingMoves = new BitSet[64];
    public static final BitSet[] queenMoves = new BitSet[64];


    // Masks for board edges
    public static final BitSet notAFile = new BitSet(64);
    public static final BitSet notHFile = new BitSet(64);
    public static final BitSet notGHFile = new BitSet(64);
    public static final BitSet notABFile = new BitSet(64);

    static {
        // Initialize edge masks
        for (int i = 0; i < 64; i++) {
            if (i % 8 != 0) notAFile.set(i);
            if (i % 8 != 7) notHFile.set(i);
            if (i % 8 != 6 && i % 8 != 7) notGHFile.set(i);
            if (i % 8 != 0 && i % 8 != 1) notABFile.set(i);
        }

        // Initialize moves for all pieces
        for (int i = 0; i < 64; i++) {
            whitePawnMoves[i] = generateWhitePawnMoves(i);
            blackPawnMoves[i] = generateBlackPawnMoves(i);
            knightMoves[i] = generateKnightMoves(i);
            bishopMoves[i] = generateBishopMask(i);
            rookMoves[i] = generateRookMask(i);
            kingMoves[i] = generateKingMask(i);
            queenMoves[i] = generateQueenMask(i);

        }
    }

    private static BitSet generateWhitePawnMoves(int square) {
        BitSet moves = new BitSet(64);
        int row = square / 8;
        int col = square % 8;

        if (row > 0) {
            int forward = square - 8;
            moves.set(forward);
        }

        if (row == 6) {
            int doubleForward = square - 16;
            moves.set(doubleForward);
        }

        if (col > 0 && row > 0) {
            int captureLeft = square - 9;
            moves.set(captureLeft);
        }
        if (col < 7 && row > 0) {
            int captureRight = square - 7;
            moves.set(captureRight);
        }

        return moves;
    }

    private static BitSet generateBlackPawnMoves(int square) {
        BitSet moves = new BitSet(64);
        int row = square / 8;
        int col = square % 8;

        if (row < 7) {
            int forward = square + 8;
            moves.set(forward);
        }

        if (row == 1) {
            int doubleForward = square + 16;
            moves.set(doubleForward);
        }

        if (col > 0 && row < 7) {
            int captureLeft = square + 7;
            moves.set(captureLeft);
        }
        if (col < 7 && row < 7) {
            int captureRight = square + 9;
            moves.set(captureRight);
        }

        return moves;
    }



    private static BitSet generateKnightMoves(int position) {
        BitSet moves = new BitSet(64);
        int row = position / 8;
        int col = position % 8;

        int[] rowOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] colOffsets = {1, 2, 2, 1, -1, -2, -2, -1};

        for (int k = 0; k < 8; k++) {
            int newRow = row + rowOffsets[k];
            int newCol = col + colOffsets[k];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                moves.set(newRow * 8 + newCol);
            }
        }
        return moves;
    }

    private static BitSet generateBishopMask(int square) {
        BitSet mask = new BitSet(64);
        int row = square / 8;
        int col = square % 8;

        // Move diagonally in all four directions
        for (int i = 1; i < 8; i++) {
            // Up-right diagonal
            if (row - i >= 0 && col + i < 8) {
                mask.set((row - i) * 8 + (col + i));
            }
            // Up-left diagonal
            if (row - i >= 0 && col - i >= 0) {
                mask.set((row - i) * 8 + (col - i));
            }
            // Down-right diagonal
            if (row + i < 8 && col + i < 8) {
                mask.set((row + i) * 8 + (col + i));
            }
            // Down-left diagonal
            if (row + i < 8 && col - i >= 0) {
                mask.set((row + i) * 8 + (col - i));
            }
        }

        return mask;
    }




    private static BitSet generateRookMask(int square) {
        BitSet mask = new BitSet(64);
        int row = square / 8;
        int col = square % 8;

        // Move horizontally
        for (int c = col + 1; c < 8; c++) {
            mask.set(row * 8 + c);
        }
        for (int c = col - 1; c >= 0; c--) {
            mask.set(row * 8 + c);
        }

        // Move vertically
        for (int r = row + 1; r < 8; r++) {
            mask.set(r * 8 + col);
        }
        for (int r = row - 1; r >= 0; r--) {
            mask.set(r * 8 + col);
        }

        return mask;
    }


    private static BitSet generateQueenMask(int square) {
        BitSet mask = new BitSet(64);
        // Combine moves from rook and bishop
        mask.or(generateRookMask(square));
        mask.or(generateBishopMask(square));
        return mask;
    }


    private static BitSet generateKingMask(int square) {
        BitSet mask = new BitSet(64);
        int[] offsets = {-9, -8, -7, -1, 1, 7, 8, 9};
        for (int offset : offsets) {
            int newRow = (square / 8) + (offset / 8);
            int newCol = (square % 8) + (offset % 8);
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                mask.set(newRow * 8 + newCol);
            }
        }
        return mask;
    }

    public static BitSet getBishopAttacks(BitSet occupied, int square) {
        BitSet attacks = new BitSet(64);

        // Diagonal directions: up-right, up-left, down-right, down-left
        int[] directions = {-9, -7, 7, 9};
        for (int direction : directions) {
            int pos = square;
            while (true) {
                int prevPos = pos;
                pos += direction;
                if (pos < 0 || pos >= 64 ||  boundaryReached(prevPos, direction)) break;
                attacks.set(pos);
                if (occupied.get(pos)) break;
            }
        }

        return attacks;
    }


    public static BitSet getRookAttacks(BitSet occupied, int square) {
        BitSet attacks = new BitSet(64);

        // Orthogonal directions: up, down, left, right
        int[] directions = {-8, 8, -1, 1};
        for (int direction : directions) {
            int pos = square;
            while (true) {
                int prevPos = pos; // Save the previous position
                pos += direction;
                if (pos < 0 || pos >= 64 || boundaryReached(prevPos, direction)) break;
                attacks.set(pos);
                if (occupied.get(pos)) break;
            }
        }

        return attacks;
    }

    public static BitSet getQueenAttacks(BitSet occupied, int square) {
        BitSet attacks = new BitSet(64);
        attacks.or(getBishopAttacks(occupied, square));
        attacks.or(getRookAttacks(occupied, square));
        return attacks;
    }

    private static boolean boundaryReached(int pos, int direction) {
        int row = pos / 8;
        int col = pos % 8;
        return (direction == -9 && (col == 0 || row == 0)) ||
                (direction == -7 && (col == 7 || row == 0)) ||
                (direction == 7 && (col == 0 || row == 7)) ||
                (direction == 9 && (col == 7 || row == 7)) ||
                (direction == -8 && row == 0) ||
                (direction == 8 && row == 7) ||
                (direction == -1 && col == 0) ||
                (direction == 1 && col == 7);
    }
}