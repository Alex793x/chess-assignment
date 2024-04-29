package chess.engine.move_generation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.exception.IllegalMoveException;
import chess.engine.move_validation.piece_validators.BishopValidator;
import chess.engine.move_validation.piece_validators.QueenValidator;
import chess.engine.move_validation.piece_validators.RookValidator;

import java.util.ArrayList;
import java.util.List;

public class SlidingPieceMoveGenerator {

    private final Board board;

    public SlidingPieceMoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Generates all valid moves for a sliding piece (rook, bishop, queen) from a specified square.
     *
     * @param square the starting square index of the piece, ranging from 0 (a1) to 63 (h8).
     * @param color the color of the piece (PieceColor.WHITE or PieceColor.BLACK).
     * @param pieceType the type of the sliding piece (PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN).
     * @return a list of integers, each representing a valid destination square index.
     */
    public List<Integer> generateMovesForSlidingPiece(int square, PieceColor color, PieceType pieceType) {
        List<Integer> moves = new ArrayList<>();
        long allOccupancies = board.getBitboard().getOccupancies(PieceColor.WHITE) | board.getBitboard().getOccupancies(PieceColor.BLACK);
        int[] directionOffsets = getDirectionOffsets(pieceType);

        for (int offset : directionOffsets) {
            int toSquare = square + offset;
            while (board.isWithinBoardBounds(toSquare)) {
                int currentRow = toSquare / 8;
                int currentCol = toSquare % 8;

                // Prevent wrap-around movement
                if (Math.abs(offset) == 1 && currentRow != square / 8) break; // Horizontal moves
                if (Math.abs(offset) == 8 && currentCol != square % 8) break; // Vertical moves
                if ((Math.abs(offset) == 7 || Math.abs(offset) == 9) && Math.abs(currentRow - square / 8) != Math.abs(currentCol - square % 8)) break; // Diagonal moves

                if ((allOccupancies & (1L << toSquare)) != 0) {
                    if ((board.getBitboard().getOccupancies(color.opposite()) & (1L << toSquare)) != 0) {
                        moves.add(toSquare); // Capture possible
                    }
                    break; // Blocked by any piece
                }
                moves.add(toSquare); // Add as valid move
                toSquare += offset; // Move to the next square in the direction
            }
        }
        return moves;
    }

    private int[] getDirectionOffsets(PieceType pieceType) {
        switch (pieceType) {
            case ROOK:
                return RookValidator.ROOK_OFFSETS; // Horizontal and vertical
            case BISHOP:
                return BishopValidator.BISHOP_OFFSETS; // Diagonal
            case QUEEN:
                return QueenValidator.QUEEN_OFFSET; // Horizontal, vertical, and diagonal
            default:
                throw new IllegalArgumentException("Unsupported piece type for sliding movement");
        }
    }

    /**
     * Moves the sliding piece from one square to another if the move is valid.
     *
     * @param fromSquare the current square index of the piece.
     * @param toSquare the target square index to move the piece to.
     * @param color the color of the piece (PieceColor.WHITE or PieceColor.BLACK).
     * @param pieceType the type of the sliding piece (PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN).
     * @throws IllegalMoveException if the move is not valid.
     */
    public void moveSlidingPiece(int fromSquare, int toSquare, PieceColor color, PieceType pieceType) throws IllegalMoveException {
        List<Integer> validMoves = generateMovesForSlidingPiece(fromSquare, color, pieceType);

        if (validMoves.contains(toSquare)) {
            board.getBitboard().removePieceFromSquare(fromSquare, pieceType, color);
            board.getBitboard().placePieceOnSquare(toSquare, pieceType, color);
            System.out.println("Move successful: " + pieceType + " moved from " + fromSquare + " to " + toSquare);
        } else {
            throw new IllegalMoveException("Invalid move: " + pieceType + " cannot move from " + fromSquare + " to " + toSquare);
        }
    }
}
