package org.kea.chessbackend.chess.board;

import lombok.Data;
import org.kea.chessbackend.chess.board.enums.PieceColor;
import org.kea.chessbackend.chess.board.enums.PieceType;

/**
 * The Bitboard class represents the state of a chessboard using bitboards.
 * Each piece type and color combination is represented by a separate bitboard.
 * The bitboards are used to efficiently track the positions of pieces on the chessboard.
 */
@Data
public class Bitboard {

    // the long primitive datatype consist of 64bits, which is ideal for representing a bitboard
    private long whiteKing, whiteQueens, whiteRooks, whiteBishops, whiteKnights, whitePawns;
    private long blackKing, blackQueens, blackRooks, blackBishops, blackKnights, blackPawns;

    /**
     * The SQUARE_MASKS array is a precomputed array of long values that represents the bit masks for each square
     * on the chessboard. It is used to efficiently set, clear, or check the presence of a piece on a specific
     * square using bitwise operations.
     */
    private static final long[] SQUARE_MASKS = new long[64];


    /**
     * Inside this static initializer block,
     * a loop iterates from 0 to 63 (inclusive) to precompute the bit masks for each square.
     */
    static {
        for (int i = 0; i < 64; i++) {
            SQUARE_MASKS[i] = 1L << i;
        }
    }

    /**
     * Constructs a new Bitboard object and initializes all the bitboards to 0.
     */
    public Bitboard() {
        // Initialize the bitboards to 0
        whiteKing = whiteQueens = whiteRooks = whiteBishops = whiteKnights = whitePawns = 0L;
        blackKing = blackQueens = blackRooks = blackBishops = blackKnights = blackPawns = 0L;
    }

    public void readFEN_String(String fen) {
        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/");

        // Clear all bitboards
        whiteKing = whiteQueens = whiteRooks = whiteBishops = whiteKnights = whitePawns = 0L;
        blackKing = blackQueens = blackRooks = blackBishops = blackKnights = blackPawns = 0L;

        // Process each rank, starting from the 8th rank (top of the board)
        for (int rank = 0; rank < 8; rank++) {
            int file = 7; // Start from the rightmost file (h-file) for horizontal mirroring
            for (char ch : ranks[rank].toCharArray()) {
                if (Character.isDigit(ch)) {
                    file -= Character.getNumericValue(ch); // Decrement file index for empty squares
                } else {
                    PieceType pieceType = PieceType.fromFENChar(ch);
                    PieceColor pieceColor = Character.isUpperCase(ch) ? PieceColor.WHITE : PieceColor.BLACK;
                    int square = (7 - rank) * 8 + file; // Use adjusted rank and file for mirroring
                    placePieceOnSquare(square, pieceType, pieceColor);
                    file--; // Move leftwards after placing each piece
                }
            }
        }
    }







    /**
     * Places a piece of the specified type and color on the given square of the chessboard.
     *
     * @param square     The index of the square where the piece is to be placed (0-63).
     * @param pieceType  The type of the piece to be placed (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to be placed (WHITE or BLACK).
     */
    public void placePieceOnSquare(int square, PieceType pieceType, PieceColor pieceColor) {
        long bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard |= SQUARE_MASKS[square];
        setBitboardForPieceTypeAndColor(pieceType, pieceColor, bitboard);
    }

    /**
     * Removes a piece of the specified type and color from the given square of the chessboard.
     *
     * @param square     The index of the square from where the piece is to be removed (0-63).
     * @param pieceType  The type of the piece to be removed (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to be removed (WHITE or BLACK).
     */
    public void removePieceFromSquare(int square, PieceType pieceType, PieceColor pieceColor) {
        long bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard &= ~SQUARE_MASKS[square];
        setBitboardForPieceTypeAndColor(pieceType, pieceColor, bitboard);
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
        long bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        return (bitboard & SQUARE_MASKS[square]) != 0;
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
            if ((whiteKing & SQUARE_MASKS[i]) != 0) piece = 'K';
            else if ((whiteQueens & SQUARE_MASKS[i]) != 0) piece = 'Q';
            else if ((whiteRooks & SQUARE_MASKS[i]) != 0) piece = 'R';
            else if ((whiteBishops & SQUARE_MASKS[i]) != 0) piece = 'B';
            else if ((whiteKnights & SQUARE_MASKS[i]) != 0) piece = 'N';
            else if ((whitePawns & SQUARE_MASKS[i]) != 0) piece = 'P';
            else if ((blackKing & SQUARE_MASKS[i]) != 0) piece = 'k';
            else if ((blackQueens & SQUARE_MASKS[i]) != 0) piece = 'q';
            else if ((blackRooks & SQUARE_MASKS[i]) != 0) piece = 'r';
            else if ((blackBishops & SQUARE_MASKS[i]) != 0) piece = 'b';
            else if ((blackKnights & SQUARE_MASKS[i]) != 0) piece = 'n';
            else if ((blackPawns & SQUARE_MASKS[i]) != 0) piece = 'p';
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
    private long getBitboardForPieceTypeAndColor(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case KING -> pieceColor == PieceColor.WHITE ? whiteKing : blackKing;
            case QUEEN -> pieceColor == PieceColor.WHITE ? whiteQueens : blackQueens;
            case ROOK -> pieceColor == PieceColor.WHITE ? whiteRooks : blackRooks;
            case BISHOP -> pieceColor == PieceColor.WHITE ? whiteBishops : blackBishops;
            case KNIGHT -> pieceColor == PieceColor.WHITE ? whiteKnights : blackKnights;
            case PAWN -> pieceColor == PieceColor.WHITE ? whitePawns : blackPawns;
        };
    }

    /**
     * Sets the appropriate bitboard for the given piece type and color.
     *
     * @param pieceType  The type of the piece (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece (WHITE or BLACK).
     * @param bitboard   The bitboard value to set.
     * @throws IllegalArgumentException if the piece type is invalid.
     */
    private void setBitboardForPieceTypeAndColor(PieceType pieceType, PieceColor pieceColor, long bitboard) {
        switch (pieceType) {
            case KING -> {
                if (pieceColor == PieceColor.WHITE) whiteKing = bitboard;
                else blackKing = bitboard;
            }
            case QUEEN -> {
                if (pieceColor == PieceColor.WHITE) whiteQueens = bitboard;
                else blackQueens = bitboard;
            }
            case ROOK -> {
                if (pieceColor == PieceColor.WHITE) whiteRooks = bitboard;
                else blackRooks = bitboard;
            }
            case BISHOP -> {
                if (pieceColor == PieceColor.WHITE) whiteBishops = bitboard;
                else blackBishops = bitboard;
            }
            case KNIGHT -> {
                if (pieceColor == PieceColor.WHITE) whiteKnights = bitboard;
                else blackKnights = bitboard;
            }
            case PAWN -> {
                if (pieceColor == PieceColor.WHITE) whitePawns = bitboard;
                else blackPawns = bitboard;
            }
            default -> throw new IllegalArgumentException("Invalid piece type: " + pieceType);
        }
    }
}