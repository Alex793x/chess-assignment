package engine;

import engine.move_generation.MoveGenerator;
import engine.util.TranspositionTableEntry;
import engine.util.TranspositionTableEntryType;
import engine.util.ZobristHashing;
import evaluation.PieceEvaluator;
import game.GameEngine;
import lombok.Getter;
import model.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Engine {
    private static final int MAX_DEPTH = 100;

    private final int searchDepth;
    private final Map<Long, TranspositionTableEntry> transpositionTable = new ConcurrentHashMap<>();
    private int totalNodesEvaluated;
    private int totalNodesPossibilities;
    private Move bestMoveFromIterations;
    private Move bestMoveFromCurrentIteration;

    private final List<Move> bestMoves = new ArrayList<>();

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

            List<Move> allMoves = MoveGenerator.generateAllPossibleMoves(isWhiteTurn, board, false);
            if (allMoves.isEmpty()) {
                return null;
            }

            Move bestMove = null;
            int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (Move move : allMoves) {
                totalNodesPossibilities++;

                // Evaluate each promotion option if it's a pawn promotion move
                List<Move> promotionMoves = new ArrayList<>();
                if (move.isPromotion()) {
                    promotionMoves = MoveGenerator.generatePromotionMoves(move.getSourcePosition()[0], move.getSourcePosition()[1], isWhiteTurn, board);
                } else {
                    promotionMoves.add(move);
                }

                for (Move promotionMove : promotionMoves) {
                    int moveValue = evaluateMove(promotionMove, alpha, beta, board, depth, isWhiteTurn).value;
                    promotionMove.setValue(moveValue);

                    if (isWhiteTurn && moveValue > bestValue) {
                        bestValue = moveValue;
                        bestMove = promotionMove;
                        alpha = bestValue;
                    } else if (!isWhiteTurn && moveValue < bestValue) {
                        bestValue = moveValue;
                        bestMove = promotionMove;
                        beta = bestValue;
                    }

                    if (beta <= alpha) {
                        break; // α-β cutoff
                    }

                    if (System.currentTimeMillis() >= endTime) {
                        System.out.println("Time limit exceeded. Returning best move found so far.");
                        return bestMoveFromIterations != null ? bestMoveFromIterations : bestMoveFromCurrentIteration;
                    }
                }

                if (beta <= alpha) {
                    break; // α-β cutoff
                }
            }

            System.out.println("Depth: " + depth);
            System.out.println("TOTAL NODES EVALUATED: " + totalNodesEvaluated);
            System.out.println("TOTAL NODES POSSIBILITIES: " + totalNodesPossibilities);

            bestMoveFromCurrentIteration = bestMove;

            // Update the best move found across all iterations
            if (bestMoveFromIterations == null ||
                    (isWhiteTurn && Objects.requireNonNull(bestMoveFromCurrentIteration).getValue() > bestMoveFromIterations.getValue()) ||
                    (!isWhiteTurn && Objects.requireNonNull(bestMoveFromCurrentIteration).getValue() < bestMoveFromIterations.getValue())) {
                bestMoveFromIterations = bestMoveFromCurrentIteration;
            }

            bestMoves.add(bestMoveFromCurrentIteration); // Add the best move of the current depth to the list
        }

        return bestMoveFromIterations != null ? bestMoveFromIterations : bestMoveFromCurrentIteration;
    }


    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, char[][] board, int depth, boolean isWhiteTurn) {
        applyMove(move, board);

        int boardValue = alphaBeta(board, depth - 1, alpha, beta, !isWhiteTurn);
        move.setValue(boardValue);
        undoMove(move, board);

        return new MoveEvaluationResult(move, boardValue);
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
            int eval = new PieceEvaluator().evaluate(board);
            transpositionTable.put(boardHash, new TranspositionTableEntry(eval, depth, TranspositionTableEntryType.EXACT));
            return eval;
        }

        List<Move> allMoves = MoveGenerator.generateAllPossibleMoves(isWhiteTurn, board, false);
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
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.isPromotion() ? move.getPromotionPiece() : move.getPiece();

        move.setCapturedPiece(board[newSoutePosition[0]][newSoutePosition[1]]);

        board[newSoutePosition[0]][newSoutePosition[1]] = piece;
        board[destinationPosition[0]][destinationPosition[1]] = ' ';

    }


    private void undoMove(Move move, char[][] board) {
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.isPromotion() ? move.getPromotionPiece() : move.getPiece();

        board[newSoutePosition[0]][newSoutePosition[1]] = move.getCapturedPiece();
        board[destinationPosition[0]][destinationPosition[1]] = piece;

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

    private record MoveEvaluationResult(Move move, int value) {
    }
}
