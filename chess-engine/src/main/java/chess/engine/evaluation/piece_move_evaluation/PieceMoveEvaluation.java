package chess.engine.evaluation.piece_move_evaluation;

import chess.board.Board;
import chess.board.Move;
import chess.board.MoveEvaluation;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_attack_evaluation.CaptureEvaluation;
import chess.engine.evaluation.piece_mobility_evaluation.PieceMobilityEvaluation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PieceMoveEvaluation {

    public static List<Move> evaluateAndOrderMoves(Board board, List<Move> moves, PieceColor color) {
        return moves.stream()
                .map(move -> {
                    int staticExchangeScore = CaptureEvaluation.staticExchangeEvaluation(board, move, color);
                    int mobilityScore = evaluateMobilityScore(board, move, color);
                    int moveTypeScore = 0;

                    if (move.getCapturedPieceType() != null) {
                        // Increase score for captures based on the piece value
                        moveTypeScore += 30 + move.getCapturedPieceType().getMidGameValue();
                    } else {
                        // Standard move score
                        moveTypeScore += 20;
                    }

                    int combinedScore = staticExchangeScore + mobilityScore + moveTypeScore;
                    return new MoveEvaluation(move, staticExchangeScore, mobilityScore, combinedScore);
                })
                .sorted(Comparator.comparingInt(MoveEvaluation::getCombinedScore).reversed())
                .map(MoveEvaluation::getMove)
                .collect(Collectors.toList());
    }



    private static int evaluateMobilityScore(Board board, Move move, PieceColor color) {
        int mobilityScore = 0;
        PieceType pieceType = move.getPieceType();
        int toSquare = move.getToSquare();

        if (pieceType != null) {
            mobilityScore = switch (pieceType) {
                case PAWN -> PieceMobilityEvaluation.evaluatePawnMobilityForSquare(board, toSquare, color);
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

