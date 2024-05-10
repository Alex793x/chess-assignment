package chess.material_rating;

import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.GameStateEvaluation;
import chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating_pst.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static chess.engine.evaluation.piece_board_evaluation.PieceSquareEvaluation.evaluatePiecePosition;

public class WhiteMaterialGameStateEvaluationTest {
    private Board newBoard;

    @BeforeEach
    void createCleanBoard() {
        newBoard = new Board();
        String fen = "rn2kb1r/pp2pp1p/3p2p1/2pP1b2/8/qP1P4/P1QBPPPP/R3KBNR w KQkq - 0 9"; // Custom FEN for this board setup
        newBoard.getBitboard().readFEN_String(fen, newBoard);

        System.out.println(newBoard.getBitboard().convertBitboardToBinaryString());

        System.out.println("BITBOARD CONVERTED TO FEN String: " + newBoard.getBitboard().convertBitboardToFEN());
    }

    @Test
    void WhiteMaterialPosition_ShouldEqual_() {
        int whiteMidGameTotal = evaluatePiecePositions(newBoard, PieceColor.WHITE, GameStage.MID_GAME);
        int blackMidGameTotal = evaluatePiecePositions(newBoard, PieceColor.BLACK, GameStage.MID_GAME);
        int whiteEndGameTotal = evaluatePiecePositions(newBoard, PieceColor.WHITE, GameStage.END_GAME);
        int blackEndGameTotal = evaluatePiecePositions(newBoard, PieceColor.BLACK, GameStage.END_GAME);

        System.out.println("White Mid-Game Total: " + whiteMidGameTotal);
        System.out.println("Black Mid-Game Total: " + blackMidGameTotal);
        System.out.println("White End-Game Total: " + whiteEndGameTotal);
        System.out.println("Black End-Game Total: " + blackEndGameTotal);

        int midGameDifference = whiteMidGameTotal - blackMidGameTotal;
        int endGameDifference = whiteEndGameTotal - blackEndGameTotal;

        System.out.println("Mid-Game Difference (White - Black): " + midGameDifference);
        System.out.println("End-Game Difference (White - Black): " + endGameDifference);

        if (midGameDifference > 0) {
            System.out.println("White is in the lead in the mid-game.");
        } else if (midGameDifference < 0) {
            System.out.println("Black is in the lead in the mid-game.");
        } else {
            System.out.println("The mid-game is equal.");
        }

        if (endGameDifference > 0) {
            System.out.println("White is in the lead in the end-game.");
        } else if (endGameDifference < 0) {
            System.out.println("Black is in the lead in the end-game.");
        } else {
            System.out.println("The end-game is equal.");
        }

        System.out.println("THIS IS THE RATING VALUE FOR RATING CALCULATION" + GameStateEvaluation.FullGameStateEvaluation(newBoard));
    }

    @Test
    void fixedRatingValueForFilesShouldMatchBoardFullGameStateEvaluation() {
        System.out.println(evaluatePiecePosition(newBoard, RookSquareBoardRating.WHITE_ROOK_MID_GAME_SQUARE_RATING, PieceType.ROOK, PieceColor.WHITE));
    }

    private int evaluatePiecePositions(Board board, PieceColor color, GameStage stage) {
        int total = 0;

        for (PieceType pieceType : PieceType.values()) {
            total += getSquareRatings(board, pieceType, color, stage);
        }

        return total;
    }

    private int getSquareRatings(Board board, PieceType pieceType, PieceColor color, GameStage stage) {
        return switch (pieceType) {
            case PAWN -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, PawnSquareBoardRating.WHITE_PAWN_MID_GAME_SQUARE_RATING, PieceType.PAWN, PieceColor.WHITE)
                    : evaluatePiecePosition(board, PawnSquareBoardRating.BLACK_PAWN_MID_GAME_SQUARE_RATING, PieceType.PAWN, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, PawnSquareBoardRating.WHITE_PAWN_END_GAME_SQUARE_RATING, PieceType.PAWN, PieceColor.WHITE)
                    : evaluatePiecePosition(board, PawnSquareBoardRating.BLACK_PAWN_END_GAME_SQUARE_RATING, PieceType.PAWN, PieceColor.BLACK));
            case KNIGHT -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, KnightSquareBoardRating.WHITE_KNIGHT_MID_GAME_SQUARE_RATING, PieceType.KNIGHT, PieceColor.WHITE)
                    : evaluatePiecePosition(board, KnightSquareBoardRating.BLACK_KNIGHT_MID_GAME_SQUARE_RATING, PieceType.KNIGHT, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, KnightSquareBoardRating.WHITE_KNIGHT_END_GAME_SQUARE_RATING, PieceType.KNIGHT, PieceColor.WHITE)
                    : evaluatePiecePosition(board, KnightSquareBoardRating.BLACK_KNIGHT_END_GAME_SQUARE_RATING, PieceType.KNIGHT, PieceColor.BLACK));
            case BISHOP -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, BishopSquareBoardRating.WHITE_BISHOP_MID_GAME_SQUARE_RATING, PieceType.BISHOP, PieceColor.WHITE)
                    : evaluatePiecePosition(board, BishopSquareBoardRating.BLACK_BISHOP_MID_GAME_SQUARE_RATING, PieceType.BISHOP, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, BishopSquareBoardRating.WHITE_BISHOP_END_GAME_SQUARE_RATING, PieceType.BISHOP, PieceColor.WHITE)
                    : evaluatePiecePosition(board, BishopSquareBoardRating.BLACK_BISHOP_END_GAME_SQUARE_RATING, PieceType.BISHOP, PieceColor.BLACK));
            case ROOK -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, RookSquareBoardRating.WHITE_ROOK_MID_GAME_SQUARE_RATING, PieceType.ROOK, PieceColor.WHITE)
                    : evaluatePiecePosition(board, RookSquareBoardRating.BLACK_ROOK_MID_GAME_SQUARE_RATING, PieceType.ROOK, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, RookSquareBoardRating.WHITE_ROOK_END_GAME_SQUARE_RATING, PieceType.ROOK, PieceColor.WHITE)
                    : evaluatePiecePosition(board, RookSquareBoardRating.BLACK_ROOK_END_GAME_SQUARE_RATING, PieceType.ROOK, PieceColor.BLACK));
            case QUEEN -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, QueenSquareBoardRating.WHITE_QUEEN_MID_GAME_SQUARE_RATING, PieceType.QUEEN, PieceColor.WHITE)
                    : evaluatePiecePosition(board, QueenSquareBoardRating.BLACK_QUEEN_MID_GAME_SQUARE_RATING, PieceType.QUEEN, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, QueenSquareBoardRating.WHITE_QUEEN_END_GAME_SQUARE_RATING, PieceType.QUEEN, PieceColor.WHITE)
                    : evaluatePiecePosition(board, QueenSquareBoardRating.BLACK_QUEEN_END_GAME_SQUARE_RATING, PieceType.QUEEN, PieceColor.BLACK));
            case KING -> stage == GameStage.MID_GAME
                    ? (color == PieceColor.WHITE ? evaluatePiecePosition(board, KingSquareBoardRating.WHITE_KING_MID_GAME_SQUARE_RATING, PieceType.KING, PieceColor.WHITE)
                    : evaluatePiecePosition(board, KingSquareBoardRating.BLACK_KING_MID_GAME_SQUARE_RATING, PieceType.KING, PieceColor.BLACK))
                    : (color == PieceColor.WHITE ? evaluatePiecePosition(board, KingSquareBoardRating.WHITE_KING_END_GAME_SQUARE_RATING, PieceType.KING, PieceColor.WHITE)
                    : evaluatePiecePosition(board, KingSquareBoardRating.BLACK_KING_END_GAME_SQUARE_RATING, PieceType.KING, PieceColor.BLACK));
        };
    }

    private enum GameStage {
        MID_GAME,
        END_GAME
    }
}

