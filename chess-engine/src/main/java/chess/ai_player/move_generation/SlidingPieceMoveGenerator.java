package chess.ai_player.move_generation;

import chess.board.Bitboard;
import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.exception.IllegalMoveException;
import chess.engine.move_validation.piece_validators.BishopValidator;
import chess.engine.move_validation.piece_validators.QueenValidator;
import chess.engine.move_validation.piece_validators.RookValidator;

import java.util.ArrayList;
import java.util.List;

public class SlidingPieceMoveGenerator {

    private final Bitboard bitBoard;

    public SlidingPieceMoveGenerator(Bitboard bitBoard) {
        this.bitBoard = bitBoard;
    }

    /**
     * Generates all valid moves for a sliding piece (rook, bishop, queen) from a specified square.
     *
     * @param square the starting square index of the piece, ranging from 0 (a1) to 63 (h8).
     * @param color the color of the piece (PieceColor.WHITE or PieceColor.BLACK).
     * @param pieceType the type of the sliding piece (PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN).
     * @return a list of integers, each representing a valid destination square index.
     */
    public List<Move> generateMovesForSlidingPiece(int square, PieceColor color, PieceType pieceType) {
        List<Move> moves = new ArrayList<>();
        long ownOccupancies = bitBoard.getOccupancies(color);
        long opponentOccupancies = bitBoard.getOccupancies(color.opposite());
        int[] directionOffsets = getDirectionOffsets(pieceType);

        int startRow = square / 8;
        int startCol = square % 8;

        for (int offset : directionOffsets) {
            int toSquare = square + offset;

            while (toSquare >= 0 && toSquare < 64) {
                int currentRow = toSquare / 8;
                int currentCol = toSquare % 8;

                // Prevent horizontal and vertical wrap-around for rooks and queens
                if ((Math.abs(offset) == 1 && currentRow != startRow) || (Math.abs(offset) == 8 && currentCol != startCol)) {
                    break;
                }

                // Prevent diagonal wrap-around for bishops and queens
                if (Math.abs(offset) == 7 || Math.abs(offset) == 9) {
                    if (Math.abs(currentRow - startRow) != Math.abs(currentCol - startCol)) {
                        break;
                    }
                }

                long toSquareBitboard = 1L << toSquare;

                if ((ownOccupancies & toSquareBitboard) != 0) {
                    break; // Blocked by own piece
                }

                PieceType capturedPieceType = bitBoard.getPieceTypeAtSquare(toSquare);
                moves.add(new Move(square, toSquare, pieceType, capturedPieceType, color));

                if ((opponentOccupancies & toSquareBitboard) != 0) {
                    break; // Capture possible, but cannot move further
                }

                toSquare += offset; // Move to the next square in the direction
            }
        }

        //System.out.println("Generated moves for " + pieceType + " at square " + square + ": " + moves);
        return moves;
    }






    private int[] getDirectionOffsets(PieceType pieceType) {
        return switch (pieceType) {
            case ROOK -> RookValidator.ROOK_OFFSETS; // Horizontal and vertical
            case BISHOP -> BishopValidator.BISHOP_OFFSETS; // Diagonal
            case QUEEN -> QueenValidator.QUEEN_OFFSET; // Horizontal, vertical, and diagonal
            default -> throw new IllegalArgumentException("Unsupported piece type for sliding movement");
        };
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
        List<Move> validMoves = generateMovesForSlidingPiece(fromSquare, color, pieceType);

        if (validMoves.contains(toSquare)) {
            bitBoard.removePieceFromSquare(fromSquare, pieceType, color);
            bitBoard.placePieceOnSquare(toSquare, pieceType, color);
            //System.out.println("Move successful: " + pieceType + " moved from " + fromSquare + " to " + toSquare);
        } else {
            throw new IllegalMoveException("Invalid move: " + pieceType + " cannot move from " + fromSquare + " to " + toSquare);
        }
    }


}
