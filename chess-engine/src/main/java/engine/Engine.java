package engine;

import engine.evaluations.Evaluator;
import engine.move_generation.MoveGenerator;
import lombok.Getter;
import model.Board;
import model.Move;

import java.util.PriorityQueue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
public class Engine {

    private final int searchDepth;
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final Map<Long, Integer> transpositionTable = new ConcurrentHashMap<>();
    private final Map<Long, Integer> transpositionTableDepth = new ConcurrentHashMap<>();

    private int totalNodesEvaluated;
    private int totalNodesPossibilities;

    public Engine(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public Move findBestMove(Board board, boolean isWhiteTurn) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        PriorityQueue<Move> allMovesQueue = moveGenerator.generateAllMoves(board);
        if (allMovesQueue == null || allMovesQueue.isEmpty()) {
            return null;
        }

        totalNodesEvaluated = 0;
        totalNodesPossibilities = 0;

        Move bestMove = null;
        int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        while (!allMovesQueue.isEmpty()) {
            Move move = allMovesQueue.poll();
            totalNodesPossibilities++;
            int moveValue = evaluateMove(move, alpha, beta, board, searchDepth, isWhiteTurn).value;

            if (isWhiteTurn && moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
                alpha = Math.max(alpha, bestValue);
            } else if (!isWhiteTurn && moveValue < bestValue) {
                bestValue = moveValue;
                bestMove = move;
                beta = Math.min(beta, bestValue);
            }

            if (beta <= alpha) {
                break; // α-β cutoff
            }
        }

        System.out.println("TOTAL NODES EVALUATED: " + totalNodesEvaluated);
        System.out.println("TOTAL NODES POSSIBILITIES: " + totalNodesPossibilities);
        return bestMove;
    }

    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, Board board, int depth, boolean isWhiteTurn) {
        Board boardCopy = board.deepCopy();
        boardCopy.makeMove(move);
        int moveAttackPenalty = move.getAttackPenalty();
        int protectionBonus = move.isProtected() ? 500 : 0;
        int penaltyByTurn = isWhiteTurn ? moveAttackPenalty + protectionBonus : -moveAttackPenalty - protectionBonus;
        int boardValue = penaltyByTurn+ alphaBeta(boardCopy, depth - 1, alpha, beta, !isWhiteTurn);
        boardCopy.undoMove(move);
        return new MoveEvaluationResult(move, boardValue);
    }

    private int alphaBeta(Board board, int depth, int alpha, int beta, boolean isWhiteTurn) {
        long boardHash = board.getHash();
        Integer storedDepth = transpositionTableDepth.get(boardHash);
        Integer storedValue = transpositionTable.get(boardHash);

        if (storedDepth != null && storedValue != null && storedDepth >= depth) {
            return storedValue;
        }

        if (depth == 0 || board.isGameOver()) {
            totalNodesEvaluated++;
            int eval = Evaluator.evaluateStaticBoard(board);
            transpositionTable.put(boardHash, eval);
            transpositionTableDepth.put(boardHash, depth);
            return eval;
        }

        PriorityQueue<Move> allMovesQueue = moveGenerator.generateAllMoves(board);
        while (!allMovesQueue.isEmpty()) {
            Move move = allMovesQueue.poll();
            totalNodesPossibilities++;
            board.makeMove(move);
            int moveAttackPenalty = move.getAttackPenalty();
            int protectionBonus = move.isProtected() ? 500 : 0;
            int penaltyByTurn = isWhiteTurn ? moveAttackPenalty + protectionBonus : -moveAttackPenalty - protectionBonus;
            int value = penaltyByTurn + alphaBeta(board, depth - 1, alpha, beta, !isWhiteTurn);
            board.undoMove(move);

            if (isWhiteTurn) {
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // α-β pruning
                }
            } else {
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break; // α-β pruning
                }
            }
        }

        transpositionTable.put(boardHash, isWhiteTurn ? alpha : beta);
        transpositionTableDepth.put(boardHash, depth);

        return isWhiteTurn ? alpha : beta;
    }

    private record MoveEvaluationResult(Move move, int value) {
    }
}
