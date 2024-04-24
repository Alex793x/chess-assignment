package org.kea.chessbackend;

import org.junit.jupiter.api.Test;
import org.kea.chessbackend.chess.models.Bitboard;
import org.kea.chessbackend.chess.models.enums.PieceColor;
import org.kea.chessbackend.chess.models.enums.PieceType;

import java.util.Arrays;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitboardTest {

    @Test
    void testLoadFEN() {
        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkb1r/ppppp1pp/5n2/5p1P/8/5N2/PPPPPPP1/RNBQKB1R b KQkq - 0 3"; // Custom FEN for this board setup
        BitSet[] updatedBitboards = bitboard.loadFEN(fen); // Assuming loadFEN now returns BitSet[]
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));
        System.out.println(bitboard.convertBitboardToBinaryString());

        // Checking Black pieces from the FEN
        assertTrue(bitboard.isSquareOccupiedByPiece(56, PieceType.ROOK, PieceColor.BLACK)); // a8

    }
}
