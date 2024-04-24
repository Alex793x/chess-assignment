package org.kea.chess_test_engine.board;

import lombok.Data;
import org.kea.chess_test_engine.board.enums.PieceColor;
import org.kea.chess_test_engine.board.enums.PieceType;

import java.util.BitSet;
/**
 * The Bitboard class represents the state of a chessboard using bitboards.
 * Each piece type and color combination is represented by a separate bitboard.
 * The bitboards are used to efficiently track the positions of pieces on the chessboard.
 */
@Data
public class Bitboard {

    private BitSet whiteKing, whiteQueens, whiteRooks, whiteBishops, whiteKnights, whitePawns;
    private BitSet blackKing, blackQueens, blackRooks, blackBishops, blackKnights, blackPawns;

    /**
     * Constructs a new Bitboard object and initializes all the bitboards.
     * Each bitboard is initialized with a size of 64 bits, corresponding to the 64 squares on the chessboard.
     * Initially, all bits in the bitboards are set to 0, indicating an empty chessboard.
     */
    public Bitboard() {
        // Initialize the bitboards
        initializeBitsets();
    }


    private void initializeBitsets() {
        whiteKing = new BitSet(64);
        whiteQueens = new BitSet(64);
        whiteRooks = new BitSet(64);
        whiteBishops = new BitSet(64);
        whiteKnights = new BitSet(64);
        whitePawns = new BitSet(64);
        blackKing = new BitSet(64);
        blackQueens = new BitSet(64);
        blackRooks = new BitSet(64);
        blackBishops = new BitSet(64);
        blackKnights = new BitSet(64);
        blackPawns = new BitSet(64);
    }

    public BitSet[] loadFEN(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");

        BitSet[] bitboards = new BitSet[] {
                whiteKing, whiteQueens, whiteRooks, whiteBishops, whiteKnights, whitePawns,
                blackKing, blackQueens, blackRooks, blackBishops, blackKnights, blackPawns
        };

        // Names for the bitboards for logging purposes
        String[] bitboardNames = new String[] {
                "White King", "White Queens", "White Rooks", "White Bishops", "White Knights", "White Pawns",
                "Black King", "Black Queens", "Black Rooks", "Black Bishops", "Black Knights", "Black Pawns"
        };

        // Clear all bitboards
        for (BitSet bitboard : bitboards) {
            bitboard.clear();
        }
        System.out.println("All bitboards cleared.");

        // Process each row for piece placement
        for (int i = 0; i < rows.length; i++) {
            // Calculate the starting square for the current row
            int square = 64 - 8 * (i + 1);
            System.out.println("Processing row: " + (8 - i) + " with data: " + rows[i]);
            // Process each piece in the row
            for (char piece : rows[i].toCharArray()) {
                // Check if the character is a digit (empty squares)
                if (Character.isDigit(piece)) {
                    // Calculate the number of empty squares
                    int emptySpaces = Character.getNumericValue(piece);
                    System.out.println("Skipping " + emptySpaces + " empty spaces starting from square " + square);
                    square += emptySpaces; // Skip empty squares
                } else {
                    // Place the piece on the square
                    placePieceOnSquare(square, PieceType.fromFENChar(piece), PieceColor.fromFENChar(piece));
                    System.out.println("Placed " + piece + " at square " + square);
                    square++;
                }
            }
        }

        // Optional FEN parts with defaults if they are missing
        String activeColor = parts.length > 1 ? parts[1] : "w";
        String castlingAvailability = parts.length > 2 ? parts[2] : "-";
        String enPassantTarget = parts.length > 3 ? parts[3] : "-";
        int halfMoveClock = parts.length > 4 ? Integer.parseInt(parts[4]) : 0;
        int fullMoveNumber = parts.length > 5 ? Integer.parseInt(parts[5]) : 1;

        // Log additional FEN info
        System.out.println("Active color: " + activeColor);
        System.out.println("Castling rights: " + castlingAvailability);
        System.out.println("En passant target square: " + enPassantTarget);
        System.out.println("Halfmove clock: " + halfMoveClock);
        System.out.println("Fullmove number: " + fullMoveNumber);

        // Log the final state of each bitboard with their names
        for (int j = 0; j < bitboards.length; j++) {
            System.out.println(bitboardNames[j] + ": " + bitboards[j]);
        }

        // Return the array of bitboards
        return bitboards;
    }


    /**
     * Places a piece of the specified type and color on the given square of the chessboard.
     *
     * @param square     The index of the square where the piece is to be placed (0-63).
     * @param pieceType  The type of the piece to be placed (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to be placed (WHITE or BLACK).
     */
    public void placePieceOnSquare(int square, PieceType pieceType, PieceColor pieceColor) {
        BitSet bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard.set(square);
    }

    /**
     * Removes a piece of the specified type and color from the given square of the chessboard.
     *
     * @param square     The index of the square from where the piece is to be removed (0-63).
     * @param pieceType  The type of the piece to be removed (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to be removed (WHITE or BLACK).
     */
    public void removePieceFromSquare(int square, PieceType pieceType, PieceColor pieceColor) {
        BitSet bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard.clear(square);
    }

    /**
     * Checks if the specified square is occupied by a piece of the given type and color.
     *
     * @param square     The index of the square to check (0-63).
     * @param pieceType  The type of the piece to check for (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to check for (WHITE or BLACK).
     * @return true if the square is occupied by the specified piece type and color, false otherwise.
     */
    public boolean isSquareOccupiedByPiece(int square, PieceType pieceType, PieceColor pieceColor) {
        BitSet bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        return bitboard.get(square);
    }

    /**
     * Converts the current state of the bitboards to a string representation of the chessboard.
     * Each piece is represented by a single character:
     * - 'K' for white king
     * - 'Q' for white queen
     * - 'R' for white rook
     * - 'B' for white bishop
     * - 'N' for white knight
     * - 'P' for white pawn
     * - 'k' for black king
     * - 'q' for black queen
     * - 'r' for black rook
     * - 'b' for black bishop
     * - 'n' for black knight
     * - 'p' for black pawn
     * - ' ' for an empty square
     *
     * @return A string representation of the chessboard based on the current state of the bitboards.
     */
    public String convertBitboardToBinaryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; i--) {
            char piece = ' ';
            if (whiteKing.get(i)) piece = 'K';
            else if (whiteQueens.get(i)) piece = 'Q';
            else if (whiteRooks.get(i)) piece = 'R';
            else if (whiteBishops.get(i)) piece = 'B';
            else if (whiteKnights.get(i)) piece = 'N';
            else if (whitePawns.get(i)) piece = 'P';
            else if (blackKing.get(i)) piece = 'k';
            else if (blackQueens.get(i)) piece = 'q';
            else if (blackRooks.get(i)) piece = 'r';
            else if (blackBishops.get(i)) piece = 'b';
            else if (blackKnights.get(i)) piece = 'n';
            else if (blackPawns.get(i)) piece = 'p';
            sb.append(piece).append(" ");
            if (i % 8 == 0) sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Retrieves the appropriate bitboard for the given piece type and color.
     *
     * @param pieceType  The type of the piece (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece (WHITE or BLACK).
     * @return The bitboard corresponding to the specified piece type and color.
     * @throws IllegalArgumentException if the piece type is invalid.
     */
    private BitSet getBitboardForPieceTypeAndColor(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case KING -> pieceColor == PieceColor.WHITE ? whiteKing : blackKing;
            case QUEEN -> pieceColor == PieceColor.WHITE ? whiteQueens : blackQueens;
            case ROOK -> pieceColor == PieceColor.WHITE ? whiteRooks : blackRooks;
            case BISHOP -> pieceColor == PieceColor.WHITE ? whiteBishops : blackBishops;
            case KNIGHT -> pieceColor == PieceColor.WHITE ? whiteKnights : blackKnights;
            case PAWN -> pieceColor == PieceColor.WHITE ? whitePawns : blackPawns;
            default -> throw new IllegalArgumentException("Invalid piece type: " + pieceType);
        };
    }

}