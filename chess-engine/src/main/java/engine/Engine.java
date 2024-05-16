package engine;

import engine.move_generation.MoveGenerator;
import engine.move_generation.comparators.MoveValueComparator;
import engine.util.TranspositionTableEntry;
import engine.util.TranspositionTableEntryType;
import engine.util.ZobristHashing;
import evaluation.PieceEvaluator;
import lombok.Getter;
import model.Move;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Engine {
    private static final int MAX_DEPTH = 100;

    private final int searchDepth;
    private final Map<Long, TranspositionTableEntry> transpositionTable = new ConcurrentHashMap<>();
    private final Move[][] killerMoves = new Move[MAX_DEPTH][2]; // Store 2 killer moves per depth
    private final int[][] historyTable = new int[8][8];

    private int totalNodesEvaluated;
    private int totalNodesPossibilities;
    private Move bestMoveFromIterations;
    private Move bestMoveFromCurrentIteration;
    private final Comparator<Move> comparator = new MoveValueComparator();

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

            PriorityQueue<Move> allMovesQueue = MoveGenerator.generatePossibleMoves(isWhiteTurn, board);
            if (allMovesQueue.isEmpty()) {
                return null;
            }

            // Use the best move from the previous iteration to improve move ordering
            if (bestMoveFromCurrentIteration != null) {
                allMovesQueue = reorderMoves(allMovesQueue, bestMoveFromCurrentIteration);
            }

            Move bestMove = null;
            int bestValue = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            while (!allMovesQueue.isEmpty()) {
                Move move = allMovesQueue.poll();
                totalNodesPossibilities++;
                int moveValue = evaluateMove(move, alpha, beta, board, depth, isWhiteTurn).value;
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

        return findBestMoveFromEvaluations(isWhiteTurn, board);
    }

    private Move findBestMoveFromEvaluations(boolean isWhiteTurn, char[][] board) {
        Move bestMove = null;
        int bestScore = isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : bestMoves) {
            applyMove(move, board);
            int score = new PieceEvaluator().evaluateBestMovesWithBoardState(board);
            undoMove(move, board);

            if (isWhiteTurn && score > bestScore) {
                bestScore = score;
                bestMove = move;
            } else if (!isWhiteTurn && score < bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private PriorityQueue<Move> reorderMoves(PriorityQueue<Move> movesQueue, Move bestMove) {
        List<Move> moves = new ArrayList<>(movesQueue);
        moves.sort(Comparator.comparingInt(m -> m.equals(bestMove) ? -1 : 0));

        PriorityQueue<Move> sortedQueue = new PriorityQueue<>(comparator);
        sortedQueue.addAll(moves);
        return sortedQueue;
    }

    private MoveEvaluationResult evaluateMove(Move move, int alpha, int beta, char[][] board, int depth, boolean isWhiteTurn) {
        applyMove(move, board);

        int boardValue = alphaBeta(board, depth - 1, alpha, beta, !isWhiteTurn);
        move.setValue(boardValue);
        System.out.println(move);
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
            transpositionTable.put(boardHash, new TranspositionTableEntry(eval, depth, TranspositionTableEntryType.EXACT, bestMoveFromCurrentIteration, bestMoveFromIterations ));
            return eval;
        }

        PriorityQueue<Move> allMovesQueue = MoveGenerator.generatePossibleMoves(isWhiteTurn, board);
        allMovesQueue = prioritizeMoves(allMovesQueue, board, isWhiteTurn, depth);

        if (allMovesQueue.isEmpty()) {
            return isWhiteTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        int value;
        int alphaOriginal = alpha;
        int betaOriginal = beta;
        if (isWhiteTurn) {
            value = Integer.MIN_VALUE;
            while (!allMovesQueue.isEmpty()) {
                Move move = allMovesQueue.poll();
                totalNodesPossibilities++;
                applyMove(move, board);
                value = Math.max(value, alphaBeta(board, depth - 1, alpha, beta, false));
                move.setValue(value);
                undoMove(move, board);
                alpha = Math.max(alpha, value);
                if (alpha >= beta) {
                    addKillerMove(move, depth);
                    break; // α-β pruning
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            while (!allMovesQueue.isEmpty()) {
                Move move = allMovesQueue.poll();
                totalNodesPossibilities++;
                applyMove(move, board);
                value = Math.min(value, alphaBeta(board, depth - 1, alpha, beta, true));
                move.setValue(value);
                undoMove(move, board);
                beta = Math.min(beta, value);
                if (alpha >= beta) {
                    addKillerMove(move, depth);
                    break; // α-β pruning
                }
            }
        }

        TranspositionTableEntryType entryType;
        if (value <= alphaOriginal) {
            entryType = TranspositionTableEntryType.UPPERBOUND;
        } else if (value >= betaOriginal) {
            entryType = TranspositionTableEntryType.LOWERBOUND;
        } else {
            entryType = TranspositionTableEntryType.EXACT;
        }
        transpositionTable.put(boardHash, new TranspositionTableEntry(value, depth, entryType, bestMoveFromCurrentIteration, bestMoveFromIterations));
        return value;
    }

    private PriorityQueue<Move> prioritizeMoves(PriorityQueue<Move> allMovesQueue, char[][] board, boolean isWhiteTurn, int depth) {
        PriorityQueue<Move> prioritizedQueue = new PriorityQueue<>(Comparator.comparingInt(move -> {
            if (isCapture(move, board)) return -1;
            if (isCheck(move, board, isWhiteTurn)) return -2;
            if (isPromotion(move, board)) return -3;
            if (isKillerMove(move, depth)) return -4; // Prioritize killer moves
            return 0; // Don't reorder based on history value
        }));
        while (!allMovesQueue.isEmpty()) {
            Move move = allMovesQueue.poll();
            prioritizedQueue.add(move);
            updateHistoryTable(move, depth); // Update history table for each move
        }
        return prioritizedQueue;
    }

    private boolean isKillerMove(Move move, int depth) {
        return move.equals(killerMoves[depth][0]) || move.equals(killerMoves[depth][1]);
    }

    private void addKillerMove(Move move, int depth) {
        if (!move.equals(killerMoves[depth][0])) {
            killerMoves[depth][1] = killerMoves[depth][0];
            killerMoves[depth][0] = move;
        }
    }

    private void updateHistoryTable(Move move, int depth) {
        int[] source = move.getSourcePosition();
        int[] dest = move.getDestinationPosition();
        historyTable[source[0]][source[1]] += depth * depth; // Increment the history value of the source square
        historyTable[dest[0]][dest[1]] += depth * depth; // Increment the history value of the destination square
    }

    private boolean isCapture(Move move, char[][] board) {
        int[] dest = move.getDestinationPosition();
        return board[dest[0]][dest[1]] != ' ';
    }

    private boolean isCheck(Move move, char[][] board, boolean isWhiteTurn) {
        applyMove(move, board);
        boolean inCheck = isKingInCheck(board, !isWhiteTurn);
        undoMove(move, board);
        return inCheck;
    }

    private boolean isPromotion(Move move, char[][] board) {
        char piece = move.getPiece();
        int[] dest = move.getDestinationPosition();
        return (piece == 'P' && dest[0] == 0) || (piece == 'p' && dest[0] == 7);
    }

    private boolean isKingInCheck(char[][] board, boolean isWhiteTurn) {
        int[] kingPosition = findKingPosition(board, isWhiteTurn);
        List<Move> opponentMoves = MoveGenerator.generateAllPossibleMoves(!isWhiteTurn, board, false);
        for (Move move : opponentMoves) {
            int[] dest = move.getDestinationPosition();
            if (dest[0] == kingPosition[0] && dest[1] == kingPosition[1]) {
                return true;
            }
        }
        return false;
    }

    private int[] findKingPosition(char[][] board, boolean isWhiteTurn) {
        char king = isWhiteTurn ? 'K' : 'k';
        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) {
                if (board[rank][file] == king) {
                    return new int[]{rank, file};
                }
            }
        }
        throw new IllegalStateException("King not found on the board");
    }

    public void applyMove(Move move, char[][] board) {
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.getPiece();

        move.setCapturedPiece(board[newSoutePosition[0]][newSoutePosition[1]]);

        board[newSoutePosition[0]][newSoutePosition[1]] = piece;
        board[destinationPosition[0]][destinationPosition[1]] = ' ';
    }

    private void undoMove(Move move, char[][] board) {
        int[] destinationPosition = move.getDestinationPosition();
        int[] newSoutePosition = move.getSourcePosition();
        char piece = move.getPiece();

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

    private record MoveEvaluationResult(Move move, int value) {}
}
