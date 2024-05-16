package engine.util;

import java.util.Random;

public class ZobristHashing {

    private static final int BOARD_SIZE = 8;
    private static final int NUM_PIECES = 12; // 6 white pieces and 6 black pieces
    private static final long[][][] zobristTable = new long[BOARD_SIZE][BOARD_SIZE][NUM_PIECES];
    private static final Random random = new Random();

    static {
        initializeZobristTable();
    }

    private static void initializeZobristTable() {
        for (int rank = 0; rank < BOARD_SIZE; rank++) {
            for (int file = 0; file < BOARD_SIZE; file++) {
                for (int piece = 0; piece < NUM_PIECES; piece++) {
                    zobristTable[rank][file][piece] = random.nextLong();
                }
            }
        }
    }

    public static long getPieceHash(int rank, int file, char piece) {
        int pieceIndex = getPieceIndex(piece);
        return pieceIndex == -1 ? 0 : zobristTable[rank][file][pieceIndex];
    }

    private static int getPieceIndex(char piece) {
        return switch (piece) {
            case 'P' -> 0;
            case 'N' -> 1;
            case 'B' -> 2;
            case 'R' -> 3;
            case 'Q' -> 4;
            case 'K' -> 5;
            case 'p' -> 6;
            case 'n' -> 7;
            case 'b' -> 8;
            case 'r' -> 9;
            case 'q' -> 10;
            case 'k' -> 11;
            default -> -1;
        };
    }
}
