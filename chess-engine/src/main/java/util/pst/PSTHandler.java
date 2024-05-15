package util.pst;

import model.enums.PieceColor;
import model.enums.PieceType;
import util.pst.pst_tables.*;

public class PSTHandler {
    

    public static int getMidGameValue(PieceType pieceType, PieceColor pieceColor, int square) {
        int[] pieceSquareTable = getMidGamePieceSquareTable(pieceType, pieceColor);
            square = flipSquareIndexForWhite(square);
        return pieceSquareTable[square];
    }

    public static int getEndgameValue(PieceType pieceType, PieceColor pieceColor, int square) {
        int[] pieceSquareTable = getEndgamePieceSquareTable(pieceType, pieceColor);
        if (pieceColor == PieceColor.WHITE) {
            square = flipSquareIndexForWhite(square);
        }
        return pieceSquareTable[square];
    }
    
    public static int[] getMidGamePieceSquareTable(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case PAWN -> pieceColor == PieceColor.WHITE
                    ? PawnPST.WHITE_PAWN_MID_GAME_SQUARE_RATING
                    : PawnPST.BLACK_PAWN_MID_GAME_SQUARE_RATING;
            case KNIGHT -> pieceColor == PieceColor.WHITE
                    ? KinghtPST.WHITE_KNIGHT_MID_GAME_SQUARE_RATING
                    : KinghtPST.BLACK_KNIGHT_MID_GAME_SQUARE_RATING;
            case BISHOP -> pieceColor == PieceColor.WHITE
                    ? BishopPST.WHITE_BISHOP_MID_GAME_SQUARE_RATING
                    : BishopPST.BLACK_BISHOP_MID_GAME_SQUARE_RATING;
            case ROOK -> pieceColor == PieceColor.WHITE
                    ? RookPST.WHITE_ROOK_MID_GAME_SQUARE_RATING
                    : RookPST.BLACK_ROOK_MID_GAME_SQUARE_RATING;
            case QUEEN -> pieceColor == PieceColor.WHITE
                    ? QueenPST.WHITE_QUEEN_MID_GAME_SQUARE_RATING
                    : QueenPST.BLACK_QUEEN_MID_GAME_SQUARE_RATING;
            case KING -> pieceColor == PieceColor.WHITE
                    ? KingPST.WHITE_KING_MID_GAME_SQUARE_RATING
                    : KingPST.BLACK_KING_MID_GAME_SQUARE_RATING;
        };
    }

    public static int[] getEndgamePieceSquareTable(PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case PAWN -> pieceColor == PieceColor.WHITE
                    ? PawnPST.WHITE_PAWN_END_GAME_SQUARE_RATING
                    : PawnPST.BLACK_PAWN_END_GAME_SQUARE_RATING;
            case KNIGHT -> pieceColor == PieceColor.WHITE
                    ? KinghtPST.WHITE_KNIGHT_END_GAME_SQUARE_RATING
                    : KinghtPST.BLACK_KNIGHT_END_GAME_SQUARE_RATING;
            case BISHOP -> pieceColor == PieceColor.WHITE
                    ? BishopPST.WHITE_BISHOP_END_GAME_SQUARE_RATING
                    : BishopPST.BLACK_BISHOP_END_GAME_SQUARE_RATING;
            case ROOK -> pieceColor == PieceColor.WHITE
                    ? RookPST.WHITE_ROOK_END_GAME_SQUARE_RATING
                    : RookPST.BLACK_ROOK_END_GAME_SQUARE_RATING;
            case QUEEN -> pieceColor == PieceColor.WHITE
                    ? QueenPST.WHITE_QUEEN_END_GAME_SQUARE_RATING
                    : QueenPST.BLACK_QUEEN_END_GAME_SQUARE_RATING;
            case KING -> pieceColor == PieceColor.WHITE
                    ? KingPST.WHITE_KING_END_GAME_SQUARE_RATING
                    : KingPST.BLACK_KING_END_GAME_SQUARE_RATING;
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


    public static int flipSquareIndexForWhite(int square) {
        return square ^ 56;
    }
}
