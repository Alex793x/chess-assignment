package chess.ai_player;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.ai_player.move_generation.*;
import chess.engine.evaluation.GameStateEvaluation;
import chess.engine.evaluation.piece_attack_evaluation.CaptureEvaluation;
import chess.engine.evaluation.piece_move_evaluation.PieceMoveEvaluation;

import java.util.ArrayList;
import java.util.List;

public class AlphaBetaMinimax {

    private final Board board;
    private final int MAX_DEPTH;
    private int totalNodes;
    private int totalPossibilities;

    public AlphaBetaMinimax(Board board, int MAX_DEPTH) {
        this.board = board;
        this.MAX_DEPTH = MAX_DEPTH;
        this.totalNodes = 0;
        this.totalPossibilities = 0;
    }

    public Move findBestMove() {
        totalNodes = 0;
        totalPossibilities = 0;
        int bestValue = Integer.MIN_VALUE;
        Move bestMove = null;
        List<Move> moves = getAllPossibleMoves();

        for (Move move : moves) {
            board.makeMove(move);
            int value = -alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH - 1, false);
            board.undoMove(move);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
                // Log the current best move and its evaluation
                System.out.println("Current best moves: " + move + " with evaluation: " + value);
            }
        }

        // Final decision logging
        System.out.println("Decided best move: " + bestMove + " with final evaluation: " + bestValue);
        System.out.println("Total nodes evaluated: " + totalNodes);
        System.out.println("Total possibilities (including pruned): " + totalPossibilities);

        return bestMove;
    }


    private int alphaBeta(int alpha, int beta, int depth, boolean maximizingPlayer) {
        //System.out.println("Entering alphaBeta: depth=" + depth + ", maximizingPlayer=" + maximizingPlayer);
        totalNodes++;
        if (depth == 0 || board.isGameOver()) {
            int eval = GameStateEvaluation.FullGameStateEvaluation(board);
            //System.out.println("Evaluation at leaf: " + eval);
            return eval;
        }

        List<Move> moves = getAllPossibleMoves();
        if (moves.isEmpty()) {
            int eval = GameStateEvaluation.FullGameStateEvaluation(board);
            //System.out.println("No moves available, evaluation: " + eval);
            return eval;
        }

        //System.out.println("Number of moves at depth " + depth + ": " + moves.size());
        int value = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : moves) {
            //System.out.println("Making move: " + move);
            board.makeMove(move);
            //System.out.println("Board after move:");
            //System.out.println(board.getBitboard().convertBitboardToBinaryString());

            int seeValue = CaptureEvaluation.staticExchangeEvaluation(board, move, board.getCurrentPlayer());
            //System.out.println("SEE Value: " + seeValue);

            if (seeValue < 0 && move.getCapturedPieceType() != null) {
                board.undoMove(move);
                continue; // Skip losing captures unless they're the only moves
            }

            int eval = -alphaBeta(-beta, -alpha, depth - 1, !maximizingPlayer);
            board.undoMove(move);
            //System.out.println("Undoing move: " + move);
            //System.out.println(board.getBitboard().convertBitboardToBinaryString());

            if (maximizingPlayer) {
                value = Math.max(value, eval);
                alpha = Math.max(alpha, eval);
                if (alpha >= beta) {
                    //System.out.println("Pruning at depth " + depth + " with alpha=" + alpha + ", beta=" + beta);
                    break; // Alpha-beta pruning
                }
            } else {
                value = Math.min(value, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    //System.out.println("Pruning at depth " + depth + " with alpha=" + alpha + ", beta=" + beta);
                    break; // Alpha-beta pruning
                }
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
                List<Move> pieceMoves = getMovesForPiece(fromSquare, pieceType, board.getCurrentPlayer());
                moves.addAll(pieceMoves);
                totalPossibilities += pieceMoves.size(); // Here we count all moves generated before any are evaluated
            }
            allPieces &= allPieces - 1; // Clear the least significant bit
        }

        moves = PieceMoveEvaluation.evaluateAndOrderMoves(board, moves, board.getCurrentPlayer());
        return moves;
    }

    private List<Move> getMovesForPiece(int fromSquare, PieceType type, PieceColor color) {
        return switch (type) {
            case KING -> new KingMoveGenerator(board.getBitboard()).generateMovesForKing(fromSquare, color);
            case KNIGHT -> new KnightMoveGenerator(board.getBitboard()).generateMovesForKnight(fromSquare, color);
            case PAWN -> new PawnMoveGenerator(board.getBitboard()).generateMovesForPawn(fromSquare, color);
            case ROOK, BISHOP, QUEEN ->
                    new SlidingPieceMoveGenerator(board.getBitboard()).generateMovesForSlidingPiece(fromSquare, color, type);
            default -> new ArrayList<Move>();
        };
    }

    // Add helper methods to manage moves on the board directly if not already in Board

    public static void main(String[] args) {
        // Initialize the board
        Board board = new Board();
        board.getBitboard().readFEN_String("rnb1kbnr/pppp1ppp/4p3/6q1/4P3/2N2N2/PPPP1PPP/R1BQKB1R b KQkq - 3 3", board);
        AlphaBetaMinimax alphaBetaMinimax = new AlphaBetaMinimax(board, 7);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        Move bestMove = alphaBetaMinimax.findBestMove();
        System.out.println("Best move: " + bestMove);
    }


}