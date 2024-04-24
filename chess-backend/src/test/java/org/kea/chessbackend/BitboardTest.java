package org.kea.chessbackend;

import org.junit.jupiter.api.Test;
import org.kea.chessbackend.chess.models.Bitboard;
import org.kea.chessbackend.chess.models.enums.PieceColor;
import org.kea.chessbackend.chess.models.enums.PieceType;

import java.util.Arrays;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitboardTest {

    @Test
    void testLoadFEN() {
        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkbnr/pppp2p1/5p2/4N2p/7P/7R/PPPPPPP1/RNBQKB2 b Qkq - 1 4"; // Custom FEN for this board setup
        BitSet[] updatedBitboards = bitboard.loadFEN(fen); // Assuming loadFEN now returns BitSet[]
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));

        // Checking Black pieces from the FEN
        assertTrue(bitboard.isSquareOccupiedByPiece(56, PieceType.ROOK, PieceColor.BLACK)); // a8

    }
}
