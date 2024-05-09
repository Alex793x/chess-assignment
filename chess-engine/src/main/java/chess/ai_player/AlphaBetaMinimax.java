package chess.ai_player;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.ai_player.move_generation.*;
import chess.engine.evaluation.GameStateEvaluation;

import java.util.ArrayList;
import java.util.List;


public class AlphaBetaMinimax {



        private final Board board;
        private final int MAX_DEPTH;

        public AlphaBetaMinimax(Board board, int MAX_DEPTH) {
            this.board = board;
            this.MAX_DEPTH = MAX_DEPTH;
        }

        public Move findBestMove() {
            Move bestMove = null;
            int bestValue = Integer.MIN_VALUE;

            // Generate all possible moves
            List<Move> moves = getAllPossibleMoves(); // Assume this method exists and fetches all valid moves for the current player

            for (Move move : moves) {
                // Make the move
                board.makeMove(move);

                // Call minimax recursively
                int moveValue = alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, MAX_DEPTH, false);

                // Undo the move
                board.undoMove(move);

                if (moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                }
            }
            return bestMove;
        }

    private int alphaBeta(int alpha, int beta, int depth, boolean maximizingPlayer) {
        System.out.println("Entering alphaBeta - Depth: " + depth + " Maximizing: " + maximizingPlayer + " Alpha: " + alpha + " Beta: " + beta);
        if (depth == 0 || board.isGameOver()) {
            int eval = GameStateEvaluation.FullGameStateEvaluation(board);
            System.out.println("Evaluation at depth 0: " + eval);
            return eval;
        }

        int value = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<Move> moves = getAllPossibleMoves();
        for (Move move : moves) {
            board.makeMove(move);
            int newScore = alphaBeta(alpha, beta, depth - 1, !maximizingPlayer);
            board.undoMove(move);
            if (maximizingPlayer) {
                if (newScore > value) {
                    value = newScore;
                    alpha = Math.max(alpha, value);
                    System.out.println("Updated Alpha: " + alpha);
                }
            } else {
                if (newScore < value) {
                    value = newScore;
                    beta = Math.min(beta, value);
                    System.out.println("Updated Beta: " + beta);
                }
            }

            if (beta <= alpha) {
                System.out.println("Pruning at depth " + depth + " with alpha: " + alpha + " and beta: " + beta + " after move " + move);
                break;
            }
        }
        return value;
    }



    public List<Move> getAllPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        long allPieces = board.getBitboard().getOccupancies(board.getCurrentPlayer());

        while (allPieces != 0) {
            int fromSquare = Long.numberOfTrailingZeros(allPieces);
            PieceType pieceType = board.getPieceTypeAtSquare(fromSquare);
            if (pieceType != null) {
                moves.addAll(getMovesForPiece(fromSquare, pieceType, board.getCurrentPlayer()));
            }
            allPieces &= allPieces - 1; // Clear the least significant bit
        }

        //System.out.println("All possible moves: " + moves.stream().map(Move::toString).reduce("", (a, b) -> a + " " + b));
        return moves;
    }

    private List<Move> getMovesForPiece(int fromSquare, PieceType type, PieceColor color) {
        return switch (type) {
            case KING -> new KingMoveGenerator(board).generateMovesForKing(fromSquare, color);
            case KNIGHT -> new KnightMoveGenerator(board).generateMovesForKnight(fromSquare, color);
            case PAWN -> new PawnMoveGenerator(board).generateMovesForPawn(fromSquare, color);
            case ROOK, BISHOP, QUEEN ->
                    new SlidingPieceMoveGenerator(board).generateMovesForSlidingPiece(fromSquare, color, type);
        };
    }

    private int evaluate(Board board) {
        // Placeholder - Implement your own evaluation method
        return 0;
    }

    // Add helper methods to manage moves on the board directly if not already in Board

    public static void main(String[] args) {
        // Initialize the board
        Board board = new Board();
        board.getBitboard().readFEN_String("1nb2b2/3kp3/3q1Rp1/8/pQBpP2r/8/1P4PP/RNB1K3 b Q - 0 1");
        AlphaBetaMinimax alphaBetaMinimax = new AlphaBetaMinimax(board, 8);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        Move bestMove = alphaBetaMinimax.findBestMove();
        System.out.println("Best move: " + bestMove);

    }

}