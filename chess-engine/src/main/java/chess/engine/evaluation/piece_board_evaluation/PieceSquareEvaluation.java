package chess.engine.evaluation.piece_board_evaluation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

/**
 * The PieceSquareEvaluation class provides methods for evaluating the position of pieces on the chess board
 * based on piece-square tables.
 */
public final class PieceSquareEvaluation {

    /**
     * Evaluates the position of a specific piece type and color on the board using a piece-square table.
     *
     * @param board            the chess board object representing the current state of the board
     * @param pieceSquareTable an array of integers representing the piece-square table for the specific piece type;
     *                         the piece-square table assigns a score to each square on the board based on the piece type and its position
     * @param pieceType        an enum value representing the type of the piece to evaluate (e.g., PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING)
     * @param pieceColor       an enum value representing the color of the piece to evaluate (e.g., WHITE, BLACK)
     * @return the evaluation score for the positions of the specified piece type and color based on the piece-square table
     *
     * <p>
     * The method initializes a variable {@code score} to keep track of the evaluation score for the piece positions.
     * It retrieves the bitboard representation of the specified piece type and color using the {@code board.getPieceBitboard(pieceType, pieceColor)} method.
     * The bitboard is a 64-bit long value where each bit represents a square on the chess board. A set bit (1) indicates the presence of the piece on that square.
     * </p>
     *
     * <p>
     * The method enters a while loop that continues as long as there are set bits in the {@code pieceBitboard}.
     * Inside the loop, it finds the index of the least significant set bit (LSB) in the {@code pieceBitboard} using the {@code Long.numberOfTrailingZeros()} method.
     * This gives the square where the piece is located.
     * It adds the corresponding value from the {@code pieceSquareTable} to the {@code score} variable based on the square index.
     * The {@code pieceSquareTable} is an array that maps each square on the board to a specific evaluation score for the given piece type.
     * </p>
     *
     * <p>
     * The method clears the LSB in the {@code pieceBitboard} by performing the bitwise operation {@code pieceBitboard &= pieceBitboard - 1}.
     * This operation sets the LSB to 0, effectively removing the piece from the bitboard.
     * The loop continues until all set bits in the {@code pieceBitboard} have been processed, meaning all pieces of the specified type and color have been evaluated.
     * </p>
     *
     * <p>
     * Finally, the method returns the calculated {@code score}, which represents the evaluation score for the positions of the specified piece type and color based on the piece-square table.
     * </p>
     *
     * <p>
     * By using bitwise operations and the piece-square table, the {@code evaluatePiecePosition} method efficiently evaluates the positions of pieces on the chess board.
     * It iterates only over the squares occupied by the specified piece type and color, avoiding unnecessary iterations over empty squares.
     * </p>
     */
    public static int evaluatePiecePosition(Board board, int[] pieceSquareTable, PieceType pieceType, PieceColor pieceColor) {
        int score = 0;
        long pieceBitboard = board.getPieceBitboard(pieceType, pieceColor);

        while (pieceBitboard != 0) {
            int square = Long.numberOfTrailingZeros(pieceBitboard);
            score += pieceSquareTable[square];
            pieceBitboard &= pieceBitboard - 1;
        }

        return score;
    }


}
