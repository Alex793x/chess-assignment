package chess.engine.evaluation.piece_move_evaluation;

import chess.board.Board;
import chess.board.MoveEvaluation;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_attack_evaluation.CaptureEvaluation;
import chess.engine.evaluation.piece_mobility_evaluation.PieceMobilityEvaluation;

import java.util.Comparator;
import java.util.List;

public class PieceMoveEvaluation {

    public static List<Integer> evaluateAndOrderMoves(Board board, List<Integer> moves, PieceColor color) {
        return moves.stream()
                .map(move -> {
                    int staticExchangeScore = CaptureEvaluation.staticExchangeEvaluation(board, move, color);
                    int mobilityScore = evaluateMobilityScore(board, move, color);
                    return new MoveEvaluation(move, staticExchangeScore, mobilityScore);
                })
                .sorted(Comparator.comparingInt(MoveEvaluation::getStaticExchangeScore).reversed()
                        .thenComparingInt(MoveEvaluation::getMobilityScore).reversed())
                .map(MoveEvaluation::getMove)
                .toList();
    }


    private static int evaluateMobilityScore(Board board, int square, PieceColor color) {
        int mobilityScore = 0;

        // Evaluate mobility based on the piece type at the given square
        PieceType pieceType = board.getPieceTypeAtSquare(square);
        if (pieceType != null) {
            mobilityScore = switch (pieceType) {
                case PAWN -> PieceMobilityEvaluation.evaluatePawnMobilityForSquare(board, square, color);
                case KNIGHT -> PieceMobilityEvaluation.evaluateKnightMobility(board, color);
                case BISHOP -> PieceMobilityEvaluation.evaluateBishopMobility(board, color);
                case ROOK -> PieceMobilityEvaluation.evaluateRookMobility(board, color);
                case QUEEN -> PieceMobilityEvaluation.evaluateQueenMobility(board, color);
                case KING -> PieceMobilityEvaluation.evaluateKingMobility(board, color);
            };
        }

        return mobilityScore;
    }
}
