package chess.ai_player;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.ai_player.move_generation.*;
import chess.engine.evaluation.GameStateEvaluation;
import chess.engine.evaluation.piece_move_evaluation.PieceMoveEvaluation;

import java.util.ArrayList;
import java.util.List;

import static chess.engine.evaluation.piece_board_evaluation.MaterialBoardEvaluation.isMidgamePhase;


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
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        // Generate all possible moves
        List<Move> moves = getAllPossibleMoves();

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

        System.out.println("Total nodes evaluated: " + totalNodes);
        System.out.println("Total possibilities: " + totalPossibilities);
        return bestMove;
    }

    private int alphaBeta(int alpha, int beta, int depth, boolean maximizingPlayer) {
        totalNodes++;
        System.out.println("Entering alphaBeta - Depth: " + depth + " Maximizing: " + maximizingPlayer + " Alpha: " + alpha + " Beta: " + beta);
        if (depth == 0 || board.isGameOver()) {
            int eval = GameStateEvaluation.FullGameStateEvaluation(board);
            return eval;
        }

        List<Move> moves = getAllPossibleMoves();
        if (maximizingPlayer) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                PieceType capturedPieceType = board.getPieceTypeAtSquare(move.getToSquare());
                int captureValue = (capturedPieceType != null) ? isMidgamePhase(board) ? capturedPieceType.getMidGameValue() : capturedPieceType.getEndGameValue() : 0;

                board.makeMove(move);
                value = Math.max(value, alphaBeta(alpha, beta, depth - 1, false) + captureValue);
                board.undoMove(move);

                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                PieceType capturedPieceType = board.getPieceTypeAtSquare(move.getToSquare());
                int captureValue = (capturedPieceType != null) ? isMidgamePhase(board) ? capturedPieceType.getMidGameValue() : capturedPieceType.getEndGameValue() : 0;
                board.makeMove(move);
                value = Math.min(value, alphaBeta(alpha, beta, depth - 1, true) - captureValue);
                board.undoMove(move);

                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        }
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
                totalPossibilities += pieceMoves.size();
            }
            allPieces &= allPieces - 1; // Clear the least significant bit
        }

        // Evaluate and order the moves using PieceMoveEvaluation
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
        board.setCurrentPlayer(PieceColor.BLACK);
        board.getBitboard().readFEN_String("rnbqkbnr/pppppppp/5P2/8/8/8/PPPPP1PP/RNBQKBNR");
        AlphaBetaMinimax alphaBetaMinimax = new AlphaBetaMinimax(board, 6);
        System.out.println(alphaBetaMinimax.getAllPossibleMoves());
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        //Move bestMove = alphaBetaMinimax.findBestMove();
        //System.out.println("Best move: " + bestMove);

    }

}