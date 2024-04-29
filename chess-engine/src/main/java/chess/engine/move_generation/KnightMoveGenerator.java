
package chess.engine.move_generation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.exception.IllegalMoveException;
import chess.engine.move_validation.piece_validators.KnightValidator;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.ArrayList;
import java.util.List;

public class KnightMoveGenerator {

    private final Board board;

    public KnightMoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Generates all valid moves for a knight from a specified square.
     * Knights move in an L-shape: two squares in one direction and one square perpendicular.
     *
     * @param square the starting square index of the knight, ranging from 0 (a1) to 63 (h8).
     * @param color  the color of the knight (PieceColor.WHITE or PieceColor.BLACK).
     * @return a list of integers, each representing a valid destination square index.
     */
    public List<Integer> generateMovesForKnight(int square, PieceColor color) {
        List<Integer> moves = new ArrayList<>();
        long possibleMoves = PreComputationHandler.KNIGHT_ATTACKS[square];
        long ownPieces = board.getBitboard().getOccupancies(color);
        for (int i = 0; i < 64; i++) {
            if ((possibleMoves & (1L << i)) != 0 && (ownPieces & (1L << i)) == 0) {
                moves.add(i);
            }
        }
        return moves;
    }


    /**
     * Moves the knight from one square to another if the move is valid.
     * This method first checks if the intended move is within the list of valid moves
     * generated by `generateMovesForKnight`. If the move is valid, it updates the board
     * state by moving the knight to the new square. If the move is not valid, it throws
     * an exception.
     *
     * @param fromSquare the current square index of the knight.
     * @param toSquare   the target square index to move the knight to.
     * @param color      the color of the knight (PieceColor.WHITE or PieceColor.BLACK).
     * @throws IllegalMoveException if the move is not valid, with a message explaining why.
     */
    public void moveKnight(int fromSquare, int toSquare, PieceColor color) throws IllegalMoveException {
        List<Integer> validMoves = generateMovesForKnight(fromSquare, color);
        if (validMoves.contains(toSquare)) {
            board.getBitboard().removePieceFromSquare(fromSquare, PieceType.KNIGHT, color);
            board.getBitboard().placePieceOnSquare(toSquare, PieceType.KNIGHT, color);
            System.out.println("Move successful: Knight moved from " + fromSquare + " to " + toSquare);
        } else {
            throw new IllegalMoveException("Invalid move: Knight cannot move from " + fromSquare + " to " + toSquare);
        }
    }

}


