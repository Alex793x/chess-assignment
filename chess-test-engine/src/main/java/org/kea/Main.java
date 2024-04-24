package org.kea;


import org.kea.chess_test_engine.board.Bitboard;

import java.util.Arrays;
import java.util.BitSet;

public class Main {

    public static void main(String[] args) {

        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkb1r/ppppp1pp/5n2/5p1P/8/5N2/PPPPPPP1/RNBQKB1R b KQkq - 0 3"; // Custom FEN for this board setup
        BitSet[] updatedBitboards = bitboard.loadFEN(fen); // Assuming loadFEN now returns BitSet[]
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));
        System.out.println(bitboard.convertBitboardToBinaryString());
    }
}