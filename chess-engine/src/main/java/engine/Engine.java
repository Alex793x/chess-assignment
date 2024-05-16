package engine;

import engine.move_generation.MoveGenerator;
import engine.util.TranspositionTableEntry;
import engine.util.TranspositionTableEntryType;
import engine.util.ZobristHashing;
import evaluation.PieceEvaluator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.Move;
import model.MoveResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static engine.move_generation.MoveGenerator.generatePossibleMoves;

@Setter
@Getter
@AllArgsConstructor
public class Engine {

    private final Map<Long, TranspositionTableEntry> transpositionTable = new ConcurrentHashMap<>();

    public char[][] board;
    public int maxDepth;

    public MoveEvaluationResult minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
        long zobristHash = computeZobristHash();

        // Check the transposition table
        if (transpositionTable.containsKey(zobristHash)) {
            TranspositionTableEntry entry = transpositionTable.get(zobristHash);
            if (entry.getDepth() >= depth) {
                switch (entry.getType()) {
                    case EXACT:
                        return new MoveEvaluationResult(entry.getEvaluation(), null);
                    case LOWERBOUND:
                        alpha = Math.max(alpha, entry.getEvaluation());
                        break;
                    case UPPERBOUND:
                        beta = Math.min(beta, entry.getEvaluation());
                        break;
                }
                if (alpha >= beta) {
                    return new MoveEvaluationResult(entry.getEvaluation(), null);
                }
            }
        }

        if (depth == 0) {
            int evaluation = new PieceEvaluator().evaluate(board);
            transpositionTable.put(zobristHash, new TranspositionTableEntry(evaluation, depth, TranspositionTableEntryType.EXACT));
            return new MoveEvaluationResult(evaluation, null);
        }

        MoveResult moveResult = MoveGenerator.generatePossibleMoves(maximizingPlayer, board);
        Move bestMove = null;
        int alphaOrig = alpha;

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : getMovesFromQueues(moveResult)) {
                applyMove(move);
                int eval = minimax(depth - 1, alpha, beta, false).getEvaluation();
                undoMove(move);

                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            TranspositionTableEntryType type = (maxEval <= alphaOrig) ? TranspositionTableEntryType.UPPERBOUND : ((maxEval >= beta) ? TranspositionTableEntryType.LOWERBOUND : TranspositionTableEntryType.EXACT);
            transpositionTable.put(zobristHash, new TranspositionTableEntry(maxEval, depth, type));
            return new MoveEvaluationResult(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : getMovesFromQueues(moveResult)) {
                applyMove(move);
                int eval = minimax(depth - 1, alpha, beta, true).getEvaluation();
                undoMove(move);

                if (eval < minEval) {
                    minEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            TranspositionTableEntryType type = minEval <= alphaOrig ? TranspositionTableEntryType.UPPERBOUND : TranspositionTableEntryType.LOWERBOUND;
            transpositionTable.put(zobristHash, new TranspositionTableEntry(minEval, depth, type));
            return new MoveEvaluationResult(minEval, bestMove);
        }
    }

    private Iterable<Move> getMovesFromQueues(MoveResult moveResult) {
        ArrayList<Move> allMoves = new ArrayList<>();
        allMoves.addAll(moveResult.getPromotionMoves());
        allMoves.addAll(moveResult.getValidCaptures());
        allMoves.addAll(moveResult.getValidMoves());
        return allMoves;
    }

    public char[][] bestMove(boolean isWhiteTurn) {
        MoveEvaluationResult bestResult = minimax(maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isWhiteTurn);
        System.out.println(bestResult);
        applyMove(bestResult.getBestMove());

        return board;
    }

    private void applyMove(Move move) {
        int[] oldSpace = move.destinationPosition;
        int[] newSpace = move.sourcePosition;
        char piece = move.piece;

        // Store the captured piece in the move to restore it later
        move.capturedPiece = board[newSpace[0]][newSpace[1]];

        // Update the board with the move
        board[newSpace[0]][newSpace[1]] = piece;
        board[oldSpace[0]][oldSpace[1]] = ' ';

        if (move.isPromotion) {
            MoveGenerator.pawnPromotionFlag = false;
        }
    }

    private void undoMove(Move move) {
        int[] oldSpace = move.destinationPosition;
        int[] newSpace = move.sourcePosition;
        char piece = move.piece;

        // Restore the captured piece
        board[newSpace[0]][newSpace[1]] = move.capturedPiece;
        board[oldSpace[0]][oldSpace[1]] = piece;
    }


    private long computeZobristHash() {
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


}
