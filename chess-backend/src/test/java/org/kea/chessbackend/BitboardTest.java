package org.kea.chessbackend;

import org.junit.jupiter.api.Test;
import org.kea.chessbackend.chess_test_engine.board.Bitboard;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceColor;
import org.kea.chessbackend.chess_test_engine.board.enums.PieceType;

import java.util.Arrays;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitboardTest {

    @Test
    void testLoadFEN() {
        Bitboard bitboard = new Bitboard();
        String fen = "rnbqkb1r/ppppppp1/5n2/8/5P1p/5N2/PPPPP1PP/RNBQKB1R b KQkq - 0 3"; // Custom FEN for this board setup
        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/"); // Split the piece placement part into ranks

        // Flip the entire board vertically and mirror horizontally, and swap colors
        String[] flippedRanks = new String[ranks.length];
        for (int i = 0; i < ranks.length; i++) {
            flippedRanks[i] = new StringBuilder(ranks[ranks.length - 1 - i]).reverse().toString(); // Flip and mirror
            flippedRanks[i] = flipColors(flippedRanks[i]); // Swap colors
        }

        // Reassemble the flipped ranks
        String flippedFEN = String.join("/", flippedRanks);
        parts[0] = flippedFEN; // Replace the board setup in the FEN
        String updatedFEN = String.join(" ", parts);

        // Assuming loadFEN is designed to handle an updated board correctly
        BitSet[] updatedBitboards = bitboard.loadFEN(updatedFEN);
        System.out.println("Updated Bitboards: " + Arrays.toString(updatedBitboards));
        System.out.println("Flipped and Mirrored FEN: " + updatedFEN);
        System.out.println(bitboard.convertBitboardToBinaryString());

        // Assertions can be adjusted based on expected outcomes
        assertTrue(bitboard.isSquareOccupiedByPiece(63, PieceType.KING, PieceColor.BLACK)); // Example check
    }

    private String flipColors(String rank) {
        StringBuilder flipped = new StringBuilder(rank.length());
        for (char c : rank.toCharArray()) {
            if (Character.isUpperCase(c)) {
                flipped.append(Character.toLowerCase(c)); // White to black
            } else if (Character.isLowerCase(c)) {
                flipped.append(Character.toUpperCase(c)); // Black to white
            } else {
                flipped.append(c); // Keep numbers and other characters unchanged
            }
        }
        return flipped.toString();
    }
}
