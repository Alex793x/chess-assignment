package engine;

import engine.evaluations.Evaluator;
import engine.move_generation.MoveGenerator;
import lombok.Getter;
import model.Board;
import model.Move;
import model.Piece;
import model.enums.PieceType;

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

//            System.out.println("Evaluating move: " + move);
//            System.out.println("Move value: " + moveValue);
//            System.out.println("Alpha: " + alpha + " Beta: " + beta);

            if (isWhiteTurn && moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
                alpha = bestValue;
                System.out.println("New best move found for White: " + bestMove + " with value: " + bestValue);
            } else if (!isWhiteTurn && moveValue < bestValue) {
                bestValue = moveValue;
                bestMove = move;
                beta = bestValue;
                System.out.println("New best move found for Black: " + bestMove + " with value: " + bestValue);
            }

            if (beta <= alpha) {
                break; // α-β cutoff
            }
        }

        System.out.println("TOTAL NODES EVALUATED: " + totalNodesEvaluated);
        System.out.println("TOTAL NODES POSSIBILITIES: " + totalNodesPossibilities);
        System.out.println("Best Move Selected: " + bestMove + " with value: " + bestValue);

        return bestMove;
    }

    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, Board board, int depth, boolean isWhiteTurn) {
        Board boardCopy = board.deepCopy();

        // Make the move on the copied board
        boardCopy.makeMove(move);

        // Check for immediate checkmate after the move
        if (boardCopy.isGameOver()) {
            // If this move results in checkmate, assign the highest possible value for the current player
            int checkmateValue = isWhiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            return new MoveEvaluationResult(move, checkmateValue);
        }

        // Evaluate the static board after making the move
        int staticBoardEvaluationAfter = Evaluator.evaluateStaticBoard(boardCopy);

        // Calculate other factors contributing to the move value
        Piece capturedPiece = move.getCapturedPiece();
        int moveAttackPenalty = move.getAttackPenalty();
        int attackBonus = move.getIsAttackingOtherPiece(); // Assuming you renamed it correctly
        int defendingBonus = move.getIsDefendingOtherPiece() * 2;
        int protectionBonus = move.isProtected() ? 500 : 0;
        int positionGain = move.getPositionGain();
        int attackingOtherPieceValue = isWhiteTurn ? attackBonus : -attackBonus;
        int defendingOtherPieceValue = isWhiteTurn ? defendingBonus : -defendingBonus;
        int penaltyByTurn = isWhiteTurn ? moveAttackPenalty + protectionBonus : -moveAttackPenalty - protectionBonus;

        // Special handling for game-over situations
        int gameOverBonus = 0;
        if (capturedPiece != null && capturedPiece.getPieceType().equals(PieceType.KING)) {
            gameOverBonus = isWhiteTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }

        int moveValue = positionGain + penaltyByTurn + attackingOtherPieceValue + defendingOtherPieceValue + gameOverBonus + staticBoardEvaluationAfter;

        // Evaluate the board using alpha-beta pruning
        int boardValue = alphaBeta(boardCopy, depth - 1, alpha, beta, !isWhiteTurn);
        boardCopy.undoMove(move);

        // Combine move value and board value
        int totalValue = moveValue + boardValue;

        // Logging for debugging
//        System.out.println("Evaluating move: " + move);
//        System.out.println("Move value: " + moveValue + ", Board value: " + boardValue);
//        System.out.println("Position gain: " + positionGain + ", Attack penalty: " + moveAttackPenalty + ", Protection bonus: " + protectionBonus);
//        System.out.println("Static Board Evaluation after move: " + staticBoardEvaluationAfter);
//        System.out.println("==========================================");

        return new MoveEvaluationResult(move, totalValue);
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

            int value = -alphaBeta(board, depth - 1, -beta, -alpha, !isWhiteTurn);
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

        int returnValue = isWhiteTurn ? alpha : beta;
        transpositionTable.put(boardHash, returnValue);
        transpositionTableDepth.put(boardHash, depth);

        return returnValue;
    }

    private record MoveEvaluationResult(Move move, int value) {

    }
}