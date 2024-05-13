package chess.ai_player;

import chess.board.Board;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.ai_player.move_generation.*;
import chess.engine.evaluation.GameStateEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareTables;
import chess.engine.evaluation.piece_move_evaluation.PieceMoveEvaluation;
import chess.state.GameState2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static chess.engine.evaluation.piece_board_evaluation.MaterialBoardEvaluation.isMidgamePhase;

public class AlphaBetaMinimax {

    private final Map<Long, Integer> transpositionTable = new ConcurrentHashMap<>(); // Add transposition table


    private final Board board;
    private final int MAX_DEPTH;
    private int totalNodes;
    private int totalPossibilities;

    private final int MAX_QUIESCENCE_DEPTH = 2;


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

        PieceColor currentPlayer = board.getCurrentPlayer();  // Get current player color

        for (Move move : moves) {
            board.makeMove(move);
            int value = -alphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH - 1, board.getCurrentPlayer().opposite()); // Correct maximizingPlayer initialization
            board.undoMove(move);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        System.out.println("Decided best move: " + bestMove + " with final evaluation: " + bestValue);
        System.out.println("Total nodes evaluated: " + totalNodes);
        System.out.println("Total possibilities (including pruned): " + totalPossibilities);

        return bestMove;
    }


    private int alphaBeta(int alpha, int beta, int depth, PieceColor maximizingPlayer) {
        System.out.println("Entering alphaBeta: Depth=" + depth + ", Player=" + maximizingPlayer + ", Alpha=" + alpha + ", Beta=" + beta);
        totalNodes++;

        // Transposition table lookup
        long boardHash = board.getHash();
        if (transpositionTable.containsKey(boardHash) && depth != MAX_DEPTH) {
            System.out.println("Transposition table hit at depth " + depth + " with value " + transpositionTable.get(boardHash));
            return transpositionTable.get(boardHash);
        }

        if (depth == 0 || board.isGameOver()) {
            int evaluation = GameStateEvaluation.FullGameStateEvaluation(board);
            System.out.println("Board evaluated at depth 0 or game over: " + evaluation);
            if (depth == 0 && !getCaptureMoves(board.getCurrentPlayer()).isEmpty()) {
                evaluation = quiescenceSearch(alpha, beta, maximizingPlayer, 0);
            }
            transpositionTable.put(boardHash, evaluation);
            return evaluation;
        }

        List<Move> moves = getAllPossibleMoves();
        if (moves.isEmpty()) {
            System.out.println("No moves available, evaluating game state.");
            return GameStateEvaluation.FullGameStateEvaluation(board);
        }

        if (maximizingPlayer == PieceColor.WHITE) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                System.out.println("White player considers move: " + move);
                board.makeMove(move);
                int newScore = -alphaBeta(-beta, -alpha, depth - 1, PieceColor.BLACK);
                board.undoMove(move);
                System.out.println("White player evaluates move " + move + " with score " + newScore);
                value = Math.max(value, newScore);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    System.out.println("Pruning branches for white with alpha=" + alpha + " and beta=" + beta);
                    break; // Alpha-beta pruning
                }
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                System.out.println("Black player considers move: " + move);
                board.makeMove(move);
                int newScore = -alphaBeta(-beta, -alpha, depth - 1, PieceColor.WHITE);
                board.undoMove(move);
                System.out.println("Black player evaluates move " + move + " with score " + newScore);
                value = Math.min(value, newScore);
                beta = Math.min(beta, value);
                if (beta <= alpha) {
                    System.out.println("Pruning branches for black with alpha=" + alpha + " and beta=" + beta);
                    break; // Alpha-beta pruning
                }
            }
            return value;
        }
    }

    private int quiescenceSearch(int alpha, int beta, PieceColor maximizingPlayer, int depth) {
        totalNodes++;
        System.out.println("Entering quiescenceSearch: Depth=" + depth + ", Player=" + maximizingPlayer + ", Alpha=" + alpha + ", Beta=" + beta);

        if (depth >= MAX_QUIESCENCE_DEPTH) {
            int quietEval = GameStateEvaluation.FullGameStateEvaluation(board);
            System.out.println("Maximum quiescence depth reached, evaluation: " + quietEval);
            return quietEval;
        }

        int standPat = GameStateEvaluation.FullGameStateEvaluation(board);
        System.out.println("Stand pat evaluation: " + standPat);
        if (maximizingPlayer == PieceColor.WHITE) {
            alpha = Math.max(alpha, standPat);
        } else {
            beta = Math.min(beta, standPat);
        }

        if (beta <= alpha) {
            System.out.println("Quiescence pruning at stand pat with alpha=" + alpha + ", beta=" + beta);
            return standPat;
        }

        List<Move> captureMoves = getCaptureMoves(maximizingPlayer);
        for (Move move : captureMoves) {
            System.out.println(maximizingPlayer + " considering capture move: " + move);
            board.makeMove(move);
            int score = -quiescenceSearch(-beta, -alpha, maximizingPlayer.opposite(), depth + 1);
            board.undoMove(move);
            System.out.println(maximizingPlayer + " evaluates capture move " + move + " with score " + score);

            if (maximizingPlayer == PieceColor.WHITE) {
                alpha = Math.max(alpha, score);
            } else {
                beta = Math.min(beta, score);
            }

            if (beta <= alpha) {
                System.out.println("Quiescence pruning after capture move with alpha=" + alpha + ", beta=" + beta);
                break;
            }
        }

        return maximizingPlayer == PieceColor.WHITE ? alpha : beta;
    }



    // Get only capturing moves
    private List<Move> getCaptureMoves(PieceColor side) {
        List<Move> captureMoves = new ArrayList<>();
        long allPieces = board.getBitboard().getOccupancies(side);

        while (allPieces != 0) {
            int fromSquare = Long.numberOfTrailingZeros(allPieces);
            PieceType pieceType = board.getPieceTypeAtSquare(fromSquare);
            if (pieceType != null) {
                List<Move> pieceMoves = getMovesForPiece(fromSquare, pieceType, side);
                for (Move move : pieceMoves) {
                    if (move.isCapture(board)) {
                        captureMoves.add(move);
                    }
                }
            }
            allPieces &= allPieces - 1; // Clear the lowest set bit
        }
        return captureMoves;
    }





    public List<Move> getAllPossibleMoves() {
        List<Move> validMoves = new ArrayList<>();
        long allPieces = board.getBitboard().getOccupancies(board.getCurrentPlayer());

        while (allPieces != 0) {
            int fromSquare = Long.numberOfTrailingZeros(allPieces);
            PieceType pieceType = board.getPieceTypeAtSquare(fromSquare);
            if (pieceType != null) {
                List<Move> pieceMoves = getMovesForPiece(fromSquare, pieceType, board.getCurrentPlayer());
                totalPossibilities += pieceMoves.size();
                for (Move move : pieceMoves) {
                    board.makeMove(move);
                    if (!GameState2.isCheck(board, board.getCurrentPlayer())) {
                        validMoves.add(move);
                    }
                    board.undoMove(move);
                }
            }
            allPieces &= allPieces - 1;
        }

        validMoves = PieceMoveEvaluation.evaluateAndOrderMoves(board, validMoves, board.getCurrentPlayer());

        return validMoves;
    }



    private List<Move> getMovesForPiece(int fromSquare, PieceType type, PieceColor color) {
        return switch (type) {
            case KING -> new KingMoveGenerator().generateMovesForKing(board.getBitboard(), fromSquare, color);
            case KNIGHT -> new KnightMoveGenerator(board.getBitboard()).generateMovesForKnight(fromSquare, color);
            case PAWN -> new PawnMoveGenerator(board.getBitboard()).generateMovesForPawn(fromSquare, color);
            case ROOK, BISHOP, QUEEN ->
                    new SlidingPieceMoveGenerator(board.getBitboard()).generateMovesForSlidingPiece(fromSquare, color, type);
        };
    }

    // Add helper methods to manage moves on the board directly if not already in Board

    public static void main(String[] args) {
        // Initialize the board
        Board board = new Board();
        boolean isMidgamePhase = isMidgamePhase(board);
        System.out.println("""
                * The initialization of the boards ensures each position of
                     *       A    B    C    D    E    F    G    H
                     *    +----+----+----+----+----+----+----+----+
                     *  8 | 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 |  8th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  7 | 48 | 49 | 50 | 51 | 52 | 53 | 54 | 55 |  7th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  6 | 40 | 41 | 42 | 43 | 44 | 45 | 46 | 47 |  6th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  5 | 32 | 33 | 34 | 35 | 36 | 37 | 38 | 39 |  5th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  4 | 24 | 25 | 26 | 27 | 28 | 29 | 30 | 31 |  4th rank
                     *    +----+----+----+----+----+----+----+----+
                     *  3 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 |  3rd rank
                     *    +----+----+----+----+----+----+----+----+
                     *  2 |  8 |  9 | 10 | 11 | 12 | 13 | 14 | 15 |  2nd rank
                     *    +----+----+----+----+----+----+----+----+
                     *  1 |  0 |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  1st rank
                     *    +----+----+----+----+----+----+----+----+
                     *       A    B    C    D    E    F    G    H - file(s)""");

        board.getBitboard().readFEN_String("r1bq1k1r/p1pp1ppp/1p3n2/2b1p3/P1BnP3/2N2P2/1PP1N1PP/R1BQK2R w KQ - 4 8", board);
        AlphaBetaMinimax alphaBetaMinimax = new AlphaBetaMinimax(board, 6);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());
        Move bestMove = alphaBetaMinimax.findBestMove();
        int pstValue = isMidgamePhase
                ? PieceSquareTables.getMidGameValue(bestMove.getPieceType(), bestMove.getPieceColor(), bestMove.getToSquare())
                : PieceSquareTables.getEndgameValue(bestMove.getPieceType(), bestMove.getPieceColor(), bestMove.getToSquare());
        System.out.println("THIS IS THE MOVE SQUARE VALUE = " + pstValue);
        System.out.println("Best move: " + bestMove);
        System.out.println(board.getBitboard().convertBitboardToBinaryString());

    }

}