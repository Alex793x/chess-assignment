package chess.engine.move_generation;

import chess.board.Board;
import chess.board.Bitboard;
import chess.board.enums.PieceType;
import chess.board.enums.PieceColor;
import chess.engine.pre_computations.PreComputationHandler;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    private final Board board;

    public MoveGenerator(Board board) {
        this.board = board;
    }

    public List<Integer> generateMovesForKing(int square, PieceColor color) {
        List<Integer> moves = new ArrayList<>();
        long possibleMoves = PreComputationHandler.KING_ATTACKS[square];
        long occupancies = board.getBitboard().getOccupancies(color);

        // Diagnostic output to check the bitboard values
        System.out.println("King Attacks Bitboard for " + square + ": " + Long.toBinaryString(possibleMoves));
        System.out.println("Occupancies for " + color + ": " + Long.toBinaryString(occupancies));

        for (int toSquare = 0; toSquare < 64; toSquare++) {
            if ((possibleMoves & (1L << toSquare)) != 0) { // Check if the move is within the precomputed moves
                if ((occupancies & (1L << toSquare)) == 0) { // Ensure the target square is not occupied by the same color piece
                    moves.add(toSquare);
                }
            }
        }

        System.out.println("Generated moves: " + moves);
        return moves;
    }

    public void moveKing(int fromSquare, int toSquare, PieceColor color) throws Exception {
        List<Integer> validMoves = generateMovesForKing(fromSquare, color);

        if (validMoves.contains(toSquare)) {
            // Assuming you have methods to directly manipulate the board
            board.getBitboard().removePieceFromSquare(fromSquare, PieceType.KING, color);
            board.getBitboard().placePieceOnSquare(toSquare, PieceType.KING, color);
            System.out.println("Move successful: King moved from " + fromSquare + " to " + toSquare);
        } else {
            throw new Exception("Invalid move: King cannot move from " + fromSquare + " to " + toSquare);
        }
    }

}
