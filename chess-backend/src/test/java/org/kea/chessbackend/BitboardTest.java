package org.kea.chessbackend;

import org.junit.jupiter.api.Test;
import org.kea.chessbackend.chess_test_engine.board.Bitboard;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceColor;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceType;

import java.util.Arrays;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

        testLoadFEN2();
    }

    @Test
    void testLoadFEN2() {
        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkb1r/ppppp1pp/5n2/5p1P/8/5N2/PPPPPPP1/RNBQKB1R b KQkq - 0 3"; // Custom FEN for this board setup
        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/"); // Split the piece placement part into ranks

        // Reverse the entire order of ranks
        String reversedFEN = "";
        for (int i = ranks.length - 1; i >= 0; i--) {
            reversedFEN += new StringBuilder(ranks[i]).toString();
            if (i > 0) {
                reversedFEN += "/";
            }
        }

        // Update the fen string with the reversed board setup
        parts[0] = reversedFEN;
        String updatedFEN = String.join(" ", parts);

        // Assuming loadFEN is designed to handle an updated board correctly
        BitSet[] updatedBitboards = bitboard.loadFEN(updatedFEN);
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));
        System.out.println("Reversed FEN: " + updatedFEN);
        System.out.println(bitboard.convertBitboardToBinaryString());

        // Checking Black pieces from the FEN on the original board
        assertTrue(bitboard.isSquareOccupiedByPiece(0, PieceType.ROOK, PieceColor.BLACK)); // h1 on the flipped board corresponds to a8 on the original
    }
}
