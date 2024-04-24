package org.kea.chessbackend;

import org.junit.jupiter.api.Test;
import org.kea.chessbackend.chess_test_engine.board.Bitboard;

import java.util.Arrays;
import java.util.BitSet;

public class BitboardTest {

    @Test
    void testLoadFEN() {
        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1"; // Custom FEN for this board setup
        BitSet[] updatedBitboards = bitboard.loadFEN(fen); // Assuming loadFEN now returns BitSet[]
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));
        System.out.println(bitboard.convertBitboardToBinaryString());

    }


}
