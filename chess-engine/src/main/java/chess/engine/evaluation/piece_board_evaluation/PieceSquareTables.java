package chess.engine.evaluation.piece_board_evaluation;

import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.evaluation.piece_board_evaluation.piece_square_board_rating_pst.*;

public final class PieceSquareTables {

    public static int getMidGameValue(PieceType pieceType, PieceColor pieceColor, int square) {
        int[] pieceSquareTable = getMidGamePieceSquareTable(pieceType, pieceColor);
        return pieceSquareTable[square];
    }

    public static int getEndgameValue(PieceType pieceType, PieceColor pieceColor, int square) {
        int[] pieceSquareTable = getEndgamePieceSquareTable(pieceType, pieceColor);
        return pieceSquareTable[square];
    }

    public static int[] getMidGamePieceSquareTable(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
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

    public static int[] getEndgamePieceSquareTable(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
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

    public static int getGamePhaseInc(PieceType pieceType) {
        return switch (pieceType) {
            case KNIGHT, BISHOP -> 1;
            case ROOK -> 2;
            case QUEEN -> 4;
            default -> 0;
        };
    }
}
