package engine;

import engine.evaluations.Evaluator;
import engine.move_generation.MoveGenerator;
import lombok.Getter;
import model.Board;
import model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.*;

import static engine.move_generation.comparators.MoveValueEndGameComparator.calculateMoveValue;

@Getter
public class Engine {

    private final int searchDepth;
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final Map<Long, Integer> transpositionTable = new ConcurrentHashMap<>();
    private final Map<Long, Integer> transpositionTableDepth = new ConcurrentHashMap<>();

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

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

        List<Callable<MoveEvaluationResult>> tasks = new ArrayList<>();
        while (!allMovesQueue.isEmpty()) {
            Move move = allMovesQueue.poll();
            totalNodesPossibilities++;
            tasks.add(() -> evaluateMove(move, alpha, beta, board, searchDepth, isWhiteTurn));
        }

        try {
            List<Future<MoveEvaluationResult>> futures = forkJoinPool.invokeAll(tasks);
            return processResults(futures, isWhiteTurn, alpha, beta);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, Board board, int depth, boolean isWhiteTurn) {
        Board boardCopy = board.deepCopy();
        boardCopy.makeMove(move);
        int movePoint = calculateMoveValue(move);
        int moveValue = isWhiteTurn ? movePoint : -movePoint;
        int boardValue = moveValue + alphaBeta(boardCopy, depth - 1, alpha, beta, !isWhiteTurn);
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
            int movePoint = calculateMoveValue(move);
            int moveValue = isWhiteTurn ? movePoint : -movePoint;
            int value = moveValue + alphaBeta(board, depth - 1, alpha, beta, !isWhiteTurn);
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

    private Move processResults(List<Future<MoveEvaluationResult>> futures, boolean isWhiteTurn, int alpha, int beta) {
        Move bestMove = null;
        int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Future<MoveEvaluationResult> future : futures) {
            try {
                MoveEvaluationResult result = future.get();
                if (isWhiteTurn && result.value > bestValue) {
                    bestValue = result.value;
                    bestMove = result.move;
                    alpha = Math.max(alpha, bestValue);
                } else if (!isWhiteTurn && result.value < bestValue) {
                    bestValue = result.value;
                    bestMove = result.move;
                    beta = Math.min(beta, bestValue);
                }

                if (beta <= alpha) {
                    break; // α-β cutoff
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("TOTAL NODES EVALUATED: " + totalNodesEvaluated);
        System.out.println("TOTAL NODES POSSIBILITIES: " + totalNodesPossibilities);
        return bestMove;
    }

    private static class MoveEvaluationResult {
        private final Move move;
        private final int value;

        public MoveEvaluationResult(Move move, int value) {
            this.move = move;
            this.value = value;
        }
    }
}