package chess.engine.evaluation;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_board_evaluation.MaterialBoardEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareTables;
import chess.engine.evaluation.piece_mobility_evaluation.PieceMobilityEvaluation;

public final class GameStateEvaluation {

    public static int FullGameStateEvaluation(Board board) {
        int materialAndBoardPositionScore = rateMaterialAndBoardPositionScore(board);
        int moveAbilityScore = rateMoveAbilityPosition(board);
        return materialAndBoardPositionScore + moveAbilityScore;
    }


    public static int rateMoveAbilityPosition(Board board) {
        int whiteMobilityScore = PieceMobilityEvaluation.evaluateMobility(board, PieceColor.WHITE);
        int blackMobilityScore = PieceMobilityEvaluation.evaluateMobility(board, PieceColor.BLACK);

        return whiteMobilityScore - blackMobilityScore;
    }

    private static int rateMaterialAndBoardPositionScore(Board board) {
        int materialPositionRating = MaterialBoardEvaluation.eval(board);
        int generalBoardPositionRating = whiteScoreBoardPosition(board) - blackScoreBoardPosition(board);
        int materialScore = getSidePiecesAccumulatedScore(board, PieceColor.WHITE) - getSidePiecesAccumulatedScore(board, PieceColor.BLACK);
        return materialPositionRating + generalBoardPositionRating + materialScore;
    }

    public static int whiteScoreBoardPosition(Board board) {
        int whiteScore = 0;
        for (PieceType pieceType : PieceType.values()) {
            if (MaterialBoardEvaluation.isMidgamePhase(board)) {
                whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getMidGamePieceSquareTable(pieceType, PieceColor.WHITE), pieceType, PieceColor.WHITE);
            } else {
                whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getEndgamePieceSquareTable(pieceType, PieceColor.WHITE), pieceType, PieceColor.WHITE);
            }
        }
        return whiteScore;
    }

    public static int blackScoreBoardPosition(Board board) {
        int blackScore = 0;
        for (PieceType pieceType : PieceType.values()) {
            if (MaterialBoardEvaluation.isMidgamePhase(board)) {
                blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getMidGamePieceSquareTable(pieceType, PieceColor.BLACK), pieceType, PieceColor.BLACK);
            } else {
                blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, PieceSquareTables.getEndgamePieceSquareTable(pieceType, PieceColor.BLACK), pieceType, PieceColor.BLACK);
            }
        }
        return blackScore;
    }


    public static int getSidePiecesAccumulatedScore(Board board, PieceColor side) {
        boolean isMidGamePhase = MaterialBoardEvaluation.isMidgamePhase(board);
        long allPiecesBitboardForOneSide = board.getBitboard().getOccupancies(side);
        int accumulatedScore = 0;

        while (allPiecesBitboardForOneSide != 0) {
            int square = Long.numberOfTrailingZeros(allPiecesBitboardForOneSide);
            PieceType pieceType = getPieceTypeOnSquare(board, square, side);
            accumulatedScore += isMidGamePhase ? pieceType.getMidGameValue() : pieceType.getEndGameValue();
            allPiecesBitboardForOneSide &= allPiecesBitboardForOneSide - 1;
        }

        //System.out.println("TOTAL SCORE FOR " + side + ": ---- " + accumulatedScore);
        return accumulatedScore;
    }

    private static PieceType getPieceTypeOnSquare(Board board, int square, PieceColor side) {
        for (PieceType pieceType : PieceType.values()) {
            if (board.getBitboard().isSquareOccupiedByPiece(square, pieceType, side)) {
                return pieceType;
            }
        }
        throw new IllegalStateException("No piece found on square " + square);
    }



}
