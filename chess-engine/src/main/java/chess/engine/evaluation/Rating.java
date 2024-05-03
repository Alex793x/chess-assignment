package chess.engine.evaluation;


import chess.board.Board;
import chess.board.Move;
import chess.board.enums.GamePhase;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_board_evaluation.MaterialBoardEvaluation;
import chess.engine.evaluation.piece_board_evaluation.PieceSquareEvaluation;
import chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating.*;

public final class Rating {
    private static int currentScore;

    public static int rating(Board board) {
        currentScore = initialScore(board);  // Compute the initial full score at the start
        return currentScore;
    }

    public static void updateScore(Board board, Move move) {
        // Deduct score of the piece moved from its original position
        currentScore -= getPieceSquareScore(board, move.getPieceType(), move.getFromSquare());

        // Add score of the piece to its new position
        currentScore += getPieceSquareScore(board, move.getPieceType(), move.getToSquare());

        // Additional logic for captures, promotions, etc.
        if (move.getCapturedPieceType() != null) {
            currentScore -= move.getCapturedPieceType().getMidGameValue();
        }
    }

    private static int getPieceSquareScore(Board board, PieceType piece, int square) {
        PieceColor pieceColor = board.getPieceColorAtSquare(square);
        int[] pieceSquareTable = getTableForPiece(board, piece, pieceColor);
        return pieceSquareTable[square];
    }

    private static int[] getTableForPiece(Board board, PieceType piece, PieceColor pieceColor) {
        // Return the appropriate piece-square table
        if (board.getGamePhase() == GamePhase.MID_GAME) {
            return switch (piece) {
                case PAWN -> pieceColor == PieceColor.WHITE
                        ? PawnSquareBoardRating.WHITE_PAWN_MID_GAME_SQUARE_RATING
                        : PawnSquareBoardRating.BLACK_PAWN_MID_GAME_SQUARE_RATING;
                case KNIGHT -> pieceColor == PieceColor.WHITE
                        ? KnightSquareBoardRating.WHITE_KNIGHT_MID_GAME_SQUARE_RATING
                        : KnightSquareBoardRating.BLACK_KNIGHT_MID_GAME_SQUARE_RATING;
                case BISHOP -> pieceColor == PieceColor.WHITE
                        ? BishopSquareBoardRating.WHITE_BISHOP_MID_GAME_SQUARE_RATING
                        : BishopSquareBoardRating.BLACK_BISHOP_MID_GAME_SQUARE_RATING;
                case ROOK -> pieceColor == PieceColor.WHITE
                        ? RookSquareBoardRating.WHITE_ROOK_MID_GAME_SQUARE_RATING
                        : RookSquareBoardRating.BLACK_ROOK_MID_GAME_SQUARE_RATING;
                case QUEEN -> pieceColor == PieceColor.WHITE
                        ? QueenSquareBoardRating.WHITE_QUEEN_MID_GAME_SQUARE_RATING
                        : QueenSquareBoardRating.BLACK_QUEEN_MID_GAME_SQUARE_RATING;
                case KING -> pieceColor == PieceColor.WHITE
                        ? KingSquareBoardRating.WHITE_KING_MID_GAME_SQUARE_RATING
                        : KingSquareBoardRating.BLACK_KING_MID_GAME_SQUARE_RATING;
            };
        }

        return switch (piece) {
            case PAWN -> pieceColor == PieceColor.WHITE
                    ? PawnSquareBoardRating.WHITE_PAWN_END_GAME_SQUARE_RATING
                    : PawnSquareBoardRating.BLACK_PAWN_END_GAME_SQUARE_RATING;
            case KNIGHT -> pieceColor == PieceColor.WHITE
                    ? KnightSquareBoardRating.WHITE_KNIGHT_END_GAME_SQUARE_RATING
                    : KnightSquareBoardRating.BLACK_KNIGHT_END_GAME_SQUARE_RATING;
            case BISHOP -> pieceColor == PieceColor.WHITE
                    ? BishopSquareBoardRating.WHITE_BISHOP_END_GAME_SQUARE_RATING
                    : BishopSquareBoardRating.BLACK_BISHOP_END_GAME_SQUARE_RATING;
            case ROOK -> pieceColor == PieceColor.WHITE
                    ? RookSquareBoardRating.WHITE_ROOK_END_GAME_SQUARE_RATING
                    : RookSquareBoardRating.BLACK_ROOK_END_GAME_SQUARE_RATING;
            case QUEEN -> pieceColor == PieceColor.WHITE
                    ? QueenSquareBoardRating.WHITE_QUEEN_END_GAME_SQUARE_RATING
                    : QueenSquareBoardRating.BLACK_QUEEN_END_GAME_SQUARE_RATING;
            case KING -> pieceColor == PieceColor.WHITE
                    ? KingSquareBoardRating.WHITE_KING_END_GAME_SQUARE_RATING
                    : KingSquareBoardRating.BLACK_KING_END_GAME_SQUARE_RATING;
        };
    }

    public static int rateAttackingPosition() {
        return 0;
    }

    public static int rateMaterialPosition(Board board) {
        return MaterialBoardEvaluation.eval(board);
    }

    public static int rateMoveAbilityPosition() {
        return 0;
    }

    public static int whiteScoreBoardPosition(Board board) {
        int whiteScore = 0;
        // White pieces score
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, PawnSquareBoardRating.WHITE_PAWN_MID_GAME_SQUARE_RATING, PieceType.PAWN);
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, KnightSquareBoardRating.WHITE_KNIGHT_MID_GAME_SQUARE_RATING, PieceType.KNIGHT);
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, BishopSquareBoardRating.WHITE_BISHOP_MID_GAME_SQUARE_RATING, PieceType.BISHOP);
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, RookSquareBoardRating.WHITE_ROOK_MID_GAME_SQUARE_RATING, PieceType.ROOK);
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, QueenSquareBoardRating.WHITE_QUEEN_MID_GAME_SQUARE_RATING, PieceType.QUEEN);
        whiteScore += PieceSquareEvaluation.evaluatePiecePosition(board, KingSquareBoardRating.WHITE_KING_MID_GAME_SQUARE_RATING, PieceType.KING);

        return whiteScore;
    }

    public static int blackScoreBoardPosition(Board board) {
        int blackScore = 0;
        // White pieces score
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, PawnSquareBoardRating.BLACK_PAWN_MID_GAME_SQUARE_RATING, PieceType.PAWN);
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, KnightSquareBoardRating.BLACK_KNIGHT_MID_GAME_SQUARE_RATING, PieceType.KNIGHT);
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, BishopSquareBoardRating.BLACK_BISHOP_MID_GAME_SQUARE_RATING, PieceType.BISHOP);
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, RookSquareBoardRating.BLACK_ROOK_MID_GAME_SQUARE_RATING, PieceType.ROOK);
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, QueenSquareBoardRating.BLACK_QUEEN_MID_GAME_SQUARE_RATING, PieceType.QUEEN);
        blackScore += PieceSquareEvaluation.evaluatePiecePosition(board, KingSquareBoardRating.BLACK_KING_MID_GAME_SQUARE_RATING, PieceType.KING);

        return blackScore;
    }

    public static int rateGeneralBoardPosition(Board board) {
        return whiteScoreBoardPosition(board) + blackScoreBoardPosition(board);
    }


    private static int initialScore(Board board) {
        // Full evaluation logic here
        return rateMaterialPosition(board) + rateGeneralBoardPosition(board);
    }
}
