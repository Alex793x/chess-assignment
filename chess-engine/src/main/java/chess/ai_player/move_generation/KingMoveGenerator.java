package chess.ai_player.move_generation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.exception.IllegalMoveException;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.ArrayList;
import java.util.List;

public class KingMoveGenerator {


    private final Board board;

    public KingMoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Generates all valid moves for a king from a specified square.
     * This method utilizes precomputed attack paths to efficiently determine
     * valid moves considering both the layout of the board and the rules governing
     * king movement.
     *
     * @param square the starting square index of the king, ranging from 0 (a1) to 63 (h8).
     * @param color the color of the king (PieceColor.WHITE or PieceColor.BLACK).
     * @return a list of integers, each representing a valid destination square index.
     */
    public List<Integer> generateMovesForKing(int square, PieceColor color) {
        List<Integer> moves = new ArrayList<>();
        long possibleMoves = PreComputationHandler.KING_ATTACKS[square];
        long occupancies = board.getBitboard().getOccupancies(color);

        System.out.println("King Attacks Bitboard for " + square + ": " + Long.toBinaryString(possibleMoves));
        System.out.println("Occupancies for " + color + ": " + Long.toBinaryString(occupancies));

        // Use bitwise AND to get the available moves considering the same color occupancies
        long availableMoves = possibleMoves & ~occupancies;

        // Iterate through the available moves using bitwise operations
        while (availableMoves != 0) {
            int toSquare = Long.numberOfTrailingZeros(availableMoves);
            moves.add(toSquare);
            availableMoves &= availableMoves - 1; // Clear the least significant set bit
        }

        // A printout of all the available moves
        System.out.println("Generated moves: " + moves);
        return moves;
    }

    /**
     * Moves the king from one square to another if the move is valid.
     * This method first checks if the intended move is within the list of valid moves
     * generated by `generateMovesForKing`. If the move is valid, it updates the board
     * state by moving the king to the new square. If the move is not valid, it throws
     * an exception.
     *
     * @param fromSquare the current square index of the king.
     * @param toSquare the target square index to move the king to.
     * @param color the color of the king (PieceColor.WHITE or PieceColor.BLACK).
     * @throws IllegalMoveException if the move is not valid, with a message explaining why.
     */
    public void moveKing(int fromSquare, int toSquare, PieceColor color) {
        List<Integer> validMoves = generateMovesForKing(fromSquare, color);

        if (validMoves.contains(toSquare)) {
            // Perform the move on the board
            board.getBitboard().removePieceFromSquare(fromSquare, PieceType.KING, color);
            board.getBitboard().placePieceOnSquare(toSquare, PieceType.KING, color);
            System.out.println("Move successful: King moved from " + fromSquare + " to " + toSquare);
        } else {
            // If the move is not valid, throw an exception detailing the issue
            throw new IllegalMoveException("Invalid move: King cannot move from " + fromSquare + " to " + toSquare);
        }
    }

}
