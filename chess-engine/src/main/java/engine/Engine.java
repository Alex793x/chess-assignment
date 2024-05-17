package engine;

import engine.move_generation.MoveGenerator;
import engine.util.TranspositionTableEntry;
import engine.util.TranspositionTableEntryType;
import engine.util.ZobristHashing;
import evaluation.PieceEvaluator;
import lombok.Getter;
import model.Move;
import model.MoveResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static game.GameEngine.transpositionTable;

@Getter
public class Engine {
    private static final int MAX_DEPTH = 100;

    private final int searchDepth;
    private int totalNodesEvaluated;
    private int totalNodesPossibilities;
    private Move bestMoveFromIterations;
    private Move bestMoveFromCurrentIteration;

    private final List<Move> bestMoves = new ArrayList<>();
    private PieceEvaluator pieceEvaluator = new PieceEvaluator();

    public Engine(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public Move findBestMove(char[][] board, boolean isWhiteTurn, long timeLimitMillis) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimitMillis;

        for (int depth = 1; depth <= searchDepth; depth++) {
            totalNodesEvaluated = 0;
            totalNodesPossibilities = 0;
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;

            // Use the new function to prevent checkmate
            MoveResult moveResult = MoveGenerator.generateAllPossibleMovesWithCheckPrevention(isWhiteTurn, board);
            List<Move> allMoves = new ArrayList<>(moveResult.getValidMoves());
            allMoves.addAll(moveResult.getValidCaptures());
            allMoves.addAll(moveResult.getPromotionMoves());

            if (allMoves.isEmpty()) {
                return null; // No valid moves
            }

            Move bestMove = null;
            int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            // Evaluate all moves, don't skip based on isSacrificeBeneficial
            for (Move move : allMoves) {
                totalNodesPossibilities++;

                // Check if the move is tactically sound and add a bonus if it is
                int moveValue = evaluateMove(move, alpha, beta, board, depth, isWhiteTurn).value;
                if (pieceEvaluator.isTacticallySound(board, move)) {
                    moveValue += 500;
                }

                move.setValue(moveValue);

                if (isWhiteTurn && moveValue > bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                    alpha = bestValue;
                } else if (!isWhiteTurn && moveValue < bestValue) {
                    bestValue = moveValue;
                    bestMove = move;
                    beta = bestValue;
                }

                if (beta <= alpha) {
                    break; // α-β cutoff
                }

                // Check if time limit exceeded after evaluating each move
                if (System.currentTimeMillis() >= endTime) {
                    System.out.println("Time limit exceeded. Returning best move found so far.");
                    return bestMoveFromIterations != null ? bestMoveFromIterations : bestMoveFromCurrentIteration;
                }
            }

            // Handle the case where no suitable moves are found
            if (bestMove == null) {
                System.out.println("No suitable moves found at depth: " + depth);
                return null;
            }

            System.out.println("Depth: " + depth);
            System.out.println("TOTAL NODES EVALUATED: " + totalNodesEvaluated);
            System.out.println("TOTAL NODES POSSIBILITIES: " + totalNodesPossibilities);

            bestMoveFromCurrentIteration = bestMove;

            // Update the best move found across all iterations
            if (bestMoveFromIterations == null ||
                    (isWhiteTurn && bestMoveFromCurrentIteration.getValue() > bestMoveFromIterations.getValue()) ||
                    (!isWhiteTurn && bestMoveFromCurrentIteration.getValue() < bestMoveFromIterations.getValue())) {
                bestMoveFromIterations = bestMoveFromCurrentIteration;
            }

            bestMoves.add(bestMoveFromCurrentIteration); // Add the best move of the current depth to the list
        }

        return bestMoveFromIterations != null ? bestMoveFromIterations : bestMoveFromCurrentIteration;
    }

    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, char[][] board, int depth, boolean isWhiteTurn) {
        applyMove(move, board);

        int moveValue = alphaBeta(board, depth - 1, alpha, beta, !isWhiteTurn);
        move.setValue(moveValue);
        System.out.println(move);
        undoMove(move, board);

        return new MoveEvaluationResult(move, move.getValue());
    }

    private int alphaBeta(char[][] board, int depth, int alpha, int beta, boolean isWhiteTurn) {
        long boardHash = computeZobristHash(board);
        TranspositionTableEntry entry = transpositionTable.get(boardHash);

        if (entry != null && entry.getDepth() >= depth) {
            if (entry.getType() == TranspositionTableEntryType.EXACT) {
                return entry.getEvaluation();
            } else if (entry.getType() == TranspositionTableEntryType.LOWERBOUND) {
                alpha = Math.max(alpha, entry.getEvaluation());
            } else if (entry.getType() == TranspositionTableEntryType.UPPERBOUND) {
                beta = Math.min(beta, entry.getEvaluation());
            }
            if (alpha >= beta) {
                return entry.getEvaluation();
            }
        }

        if (depth == 0 || isGameOver(board)) {
            totalNodesEvaluated++;
            int eval = new PieceEvaluator().evaluate(board, isWhiteTurn);
            transpositionTable.put(boardHash, new TranspositionTableEntry(eval, depth, TranspositionTableEntryType.EXACT));
            return eval;
        }

        // Use the new function to prevent checkmate
        MoveResult moveResult = MoveGenerator.generateAllPossibleMovesWithCheckPrevention(isWhiteTurn, board);
        List<Move> allMoves = new ArrayList<>(moveResult.getValidMoves());
        allMoves.addAll(moveResult.getValidCaptures());
        allMoves.addAll(moveResult.getPromotionMoves());

        if (allMoves.isEmpty()) {
            return isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        int value;
        if (isWhiteTurn) {
            value = Integer.MIN_VALUE;
            for (Move move : allMoves) {
                totalNodesPossibilities++;
                applyMove(move, board);
                value = Math.max(value, alphaBeta(board, depth - 1, alpha, beta, false));
                move.setValue(value);
                undoMove(move, board);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    break; // α-β pruning
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            for (Move move : allMoves) {
                totalNodesPossibilities++;
                applyMove(move, board);
                value = Math.min(value, alphaBeta(board, depth - 1, alpha, beta, true));
                move.setValue(value);
                undoMove(move, board);
                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    break; // α-β pruning
                }
            }
        }

        TranspositionTableEntryType entryType;
        if (value <= alpha) {
            entryType = TranspositionTableEntryType.UPPERBOUND;
        } else if (value >= beta) {
            entryType = TranspositionTableEntryType.LOWERBOUND;
        } else {
            entryType = TranspositionTableEntryType.EXACT;
        }
        transpositionTable.put(boardHash, new TranspositionTableEntry(value, depth, entryType));
        return value;
    }

    public void applyMove(Move move, char[][] board) {
        MoveGenerator.applyMove(move, board);

    }


    private void undoMove(Move move, char[][] board) {
        MoveGenerator.undoMove(move, board);

    }

    private long computeZobristHash(char[][] board) {
        long hash = 0L;
        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) {
                char piece = board[rank][file];
                if (piece != ' ') {
                    hash ^= ZobristHashing.getPieceHash(rank, file, piece);
                }
            }
        }
        return hash;
    }

    private boolean isGameOver(char[][] board) {
        // Implement game over check logic
        return false;
    }

    private record MoveEvaluationResult(Move move, int value) {}
}