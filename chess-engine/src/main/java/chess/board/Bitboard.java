package chess.board;

import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import lombok.Data;

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
        for (int i = 0; i < 64; ++i) {
            SQUARE_MASKS[i] = 1L << i;
        }
    }

    public long getFileMask(int file) {
        if (file < 0 || file > 7) {
            throw new IllegalArgumentException("Invalid file: " + file);
        }
        return 0x0101010101010101L << file;
    }

    /**
     * Constructs a new Bitboard object and initializes all the bitboards to 0.
     */
    public Bitboard() {
        // Initialize the bitboards to 0
        whiteKing = whiteQueens = whiteRooks = whiteBishops = whiteKnights = whitePawns = 0L;
        blackKing = blackQueens = blackRooks = blackBishops = blackKnights = blackPawns = 0L;
    }

    public Bitboard(Bitboard original) {
        this.whiteKing = original.whiteKing;
        this.whiteQueens = original.whiteQueens;
        this.whiteRooks = original.whiteRooks;
        this.whiteBishops = original.whiteBishops;
        this.whiteKnights = original.whiteKnights;
        this.whitePawns = original.whitePawns;
        this.blackKing = original.blackKing;
        this.blackQueens = original.blackQueens;
        this.blackRooks = original.blackRooks;
        this.blackBishops = original.blackBishops;
        this.blackKnights = original.blackKnights;
        this.blackPawns = original.blackPawns;
    }

    public void readFEN_String(String fen, Board board) {
        String[] parts = fen.split(" ");
        String[] ranks = parts[0].split("/");

        // Clear all bitboards
        whiteKing = whiteQueens = whiteRooks = whiteBishops = whiteKnights = whitePawns = 0L;
        blackKing = blackQueens = blackRooks = blackBishops = blackKnights = blackPawns = 0L;

        // Process each rank, starting from the 8th rank (top of the board) down to the 1st rank
        for (int rank = 0; rank < 8; rank++) {
            int file = 0; // Start from the leftmost file (a-file)
            for (char ch : ranks[7 - rank].toCharArray()) { // Start from the last element to represent the 8th rank at the top
                if (Character.isDigit(ch)) {
                    file += Character.getNumericValue(ch); // Increment file index for empty squares
                } else {
                    PieceType pieceType = PieceType.fromFENChar(ch);
                    PieceColor pieceColor = Character.isUpperCase(ch) ? PieceColor.WHITE : PieceColor.BLACK;
                    int square = rank * 8 + file; // Calculate square index based on rank and file
                    placePieceOnSquare(square, pieceType, pieceColor);
                    file++; // Move rightwards after placing each piece
                }
            }
        }

        // Handle the active color (turn)
        if (parts.length > 1) {
            String activeColor = parts[1];
            board.setCurrentPlayer(activeColor.equals("b")
                    ? PieceColor.BLACK
                    : PieceColor.WHITE);
        }
    }



    /**
     * Converts the current state of the bitboards into a Forsyth-Edwards Notation (FEN) string.
     * The FEN string represents the placement of pieces on the chessboard, as well as additional
     * game state information such as the active player, castling availability, en passant target,
     * halfmove clock, and fullmove number.
     *
     * @return A string in FEN format representing the current game state.
     */
    public String convertBitboardToFEN() {
        StringBuilder fen = new StringBuilder();
        // Process each rank starting from 8 down to 1
        for (int rank = 7; rank >= 0; rank--) {
            int emptyCount = 0;  // counts empty squares in the rank

            for (int file = 0; file < 8; file++) {
                int index = rank * 8 + file;  // Finds the index based on rank and file
                char piece = getPiece(index);  // Finds the piece for an index

                if (piece == ' ') {
                    emptyCount++;  // Increment count of empty squares
                } else {
                    if (emptyCount != 0) {
                        fen.append(emptyCount);  // Append number of empty squares before the piece
                        emptyCount = 0;  // Reset empty square count
                    }
                    fen.append(piece);  // Set the piece in the FEN
                }
            }

            if (emptyCount != 0) {
                fen.append(emptyCount);  // Append remaining empty squares at the end of the rank
            }
            if (rank > 0) {
                fen.append('/');  // Add slash to separate ranks except for the last one
            }
        }

        // TODO Here we need to add all the functionality for the rest of the FEN String.
        // THis means that when the move by the enige has been made, we need to update the
        // turn, move, half move, castling rights and en passant square. And then we send it
        // to the frontend for it to load.
        fen.append(" w KQkq - 0 1");

        return fen.toString();
    }


    /**
     * Places a piece of the specified type and color on the given square of the chessboard.
     *
     * @param square     The index of the square where the piece is to be placed (0-63).
     * @param pieceType  The type of the piece to be placed (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece to be placed (WHITE or BLACK).
     */
    public void placePieceOnSquare(int square, PieceType pieceType, PieceColor pieceColor) {
        long mask = SQUARE_MASKS[square];  // For square 0, mask will be 0b1
        long bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard |= mask;
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
        long mask = SQUARE_MASKS[square];
        long bitboard = getBitboardForPieceTypeAndColor(pieceType, pieceColor);
        bitboard &= ~mask;
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
        sb.append("     A    B    C    D    E    F    G    H\n");
        sb.append("  +----+----+----+----+----+----+----+----+\n");

        for (int rank = 7; rank >= 0; rank--) {
            sb.append(rank + 1).append(" |");
            for (int file = 0; file < 8; file++) {
                int i = rank * 8 + file;
                char piece = getPiece(i);
                sb.append("  ").append(piece).append(" |");
            }
            sb.append("  ").append(rank + 1).append("th rank\n");
            sb.append("  +----+----+----+----+----+----+----+----+\n");
        }

        sb.append("     A    B    C    D    E    F    G    H - file(s)\n");
        return sb.toString();
    }

    public static String convertBitboardToBinaryStringMaterial(int[] board) {
        StringBuilder sb = new StringBuilder();
        sb.append("     A    B    C    D    E    F    G    H\n");
        sb.append("  +----+----+----+----+----+----+----+----+\n");

        for (int rank = 7; rank >= 0; rank--) {
            sb.append(rank + 1).append(" |");
            for (int file = 0; file < 8; file++) {
                int i = rank * 8 + file;
                int piece = board[i];
                sb.append("  ").append(piece).append(" |");
            }
            sb.append("  ").append(rank + 1).append("th rank\n");
            sb.append("  +----+----+----+----+----+----+----+----+\n");
        }

        sb.append("     A    B    C    D    E    F    G    H - file(s)\n");
        return sb.toString();
    }

    private char getPiece(int i) {
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
        return piece;
    }



    /**
     * Retrieves the appropriate bitboard for the given piece type and color.
     *
     * @param pieceType  The type of the piece (e.g., KING, QUEEN, ROOK, etc.).
     * @param pieceColor The color of the piece (WHITE or BLACK).
     * @return The bitboard corresponding to the specified piece type and color.
     * @throws IllegalArgumentException if the piece type is invalid.
     */
    public long getBitboardForPieceTypeAndColor(PieceType pieceType, PieceColor pieceColor) {
        if (pieceType == null) return 0L;
        return switch (pieceType) {
            case KING -> pieceColor == PieceColor.WHITE ? whiteKing : blackKing;
            case QUEEN -> pieceColor == PieceColor.WHITE ? whiteQueens : blackQueens;
            case ROOK -> pieceColor == PieceColor.WHITE ? whiteRooks : blackRooks;
            case BISHOP -> pieceColor == PieceColor.WHITE ? whiteBishops : blackBishops;
            case KNIGHT -> pieceColor == PieceColor.WHITE ? whiteKnights : blackKnights;
            case PAWN -> pieceColor == PieceColor.WHITE ? whitePawns : blackPawns;
            default -> throw new IllegalArgumentException("Unknown piece type: " + pieceType);
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
        if (pieceType == null) return;
        switch (pieceType) {
            case KING:   if (pieceColor == PieceColor.WHITE) whiteKing = bitboard; else blackKing = bitboard; break;
            case QUEEN:  if (pieceColor == PieceColor.WHITE) whiteQueens = bitboard; else blackQueens = bitboard; break;
            case ROOK:   if (pieceColor == PieceColor.WHITE) whiteRooks = bitboard; else blackRooks = bitboard; break;
            case BISHOP: if (pieceColor == PieceColor.WHITE) whiteBishops = bitboard; else blackBishops = bitboard; break;
            case KNIGHT: if (pieceColor == PieceColor.WHITE) whiteKnights = bitboard; else blackKnights = bitboard; break;
            case PAWN:   if (pieceColor == PieceColor.WHITE) whitePawns = bitboard; else blackPawns = bitboard; break;
            default: throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
    }

    public long getOccupancies(PieceColor color) {
        return switch (color) {
            case WHITE -> whiteKing | whiteQueens | whiteRooks | whiteBishops | whiteKnights | whitePawns;
            case BLACK -> blackKing | blackQueens | blackRooks | blackBishops | blackKnights | blackPawns;
        };
    }


    public long getBitboardForColor(PieceColor currentPlayer) {
        return switch (currentPlayer) {
            case WHITE -> whiteKing | whiteQueens | whiteRooks | whiteBishops | whiteKnights | whitePawns;
            case BLACK -> blackKing | blackQueens | blackRooks | blackBishops | blackKnights | blackPawns;
        };
    }

    public PieceType getPieceTypeAtSquare(int square) {
        for (PieceColor color : PieceColor.values()) {
            for (PieceType type : PieceType.values()) {
                if (isSquareOccupiedByPiece(square, type, color)) {
                    return type;
                }
            }
        }
        return null;
    }

    public boolean isSquareEmpty(int square, long allOccupancies) {
        return (allOccupancies & (1L << square)) == 0;
    }

    public boolean isSquareOccupiedByEnemy(int square, long enemyOccupancies) {
        return (enemyOccupancies & (1L << square)) != 0;
    }

    public boolean isWithinBoardBounds(int square) {
        return square >= 0 && square < 64;
    }

    public PieceColor getPieceColorAtSquare(int square) {
        for (PieceColor color : PieceColor.values()) {
            for (PieceType type : PieceType.values()) {
                if (isSquareOccupiedByPiece(square, type, color)) {
                    return color;
                }
            }
        }
        return null;
    }

    public long getAllOccupancies() {
        return whiteKing | whiteQueens | whiteRooks | whiteBishops | whiteKnights | whitePawns |
               blackKing | blackQueens | blackRooks | blackBishops | blackKnights | blackPawns;
    }
}