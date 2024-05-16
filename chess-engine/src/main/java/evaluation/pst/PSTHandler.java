package evaluation.pst;

import lombok.NoArgsConstructor;

import static evaluation.pst.tables.BishopPST.BLACK_BISHOP_PST;
import static evaluation.pst.tables.BishopPST.WHITE_BISHOP_PST;
import static evaluation.pst.tables.KingPST.BLACK_KING_PST;
import static evaluation.pst.tables.KingPST.WHITE_KING_PST;
import static evaluation.pst.tables.KnightPST.BLACK_KNIGHT_PST;
import static evaluation.pst.tables.KnightPST.WHITE_KNIGHT_PST;
import static evaluation.pst.tables.PawnPST.BLACK_PAWN_PST;
import static evaluation.pst.tables.PawnPST.WHITE_PAWN_PST;
import static evaluation.pst.tables.QueenPST.BLACK_QUEEN_PST;
import static evaluation.pst.tables.QueenPST.WHITE_QUEEN_PST;
import static evaluation.pst.tables.RookPST.BLACK_ROOK_PST;
import static evaluation.pst.tables.RookPST.WHITE_ROOK_PST;

@NoArgsConstructor
public final class PSTHandler {

    public static int getPiecePositionPSTValue(int rank, int file, char piece) {
        if (!Character.isUpperCase(piece)) {
            return getBlackPositionPSTTables(rank, file, piece);
        }
        return getWhitePositionPSTTables(rank, file, piece);
    }
    

    private static int getWhitePositionPSTTables(int rank, int file, char piece) {
        return switch (piece) {
            case 'P' -> WHITE_PAWN_PST[rank][file];
            case 'N' -> WHITE_KNIGHT_PST[rank][file];
            case 'B' -> WHITE_BISHOP_PST[rank][file];
            case 'R' -> WHITE_ROOK_PST[rank][file];
            case 'Q' -> WHITE_QUEEN_PST[rank][file];
            case 'K' -> WHITE_KING_PST[rank][file];
            default -> 0;
        };
    }

    private static int getBlackPositionPSTTables(int row, int file, char piece) {
        return switch (piece) {
            case 'p' -> BLACK_PAWN_PST[row][file];
            case 'n' -> BLACK_KNIGHT_PST[row][file];
            case 'b' -> BLACK_BISHOP_PST[row][file];
            case 'r' -> BLACK_ROOK_PST[row][file];
            case 'q' -> BLACK_QUEEN_PST[row][file];
            case 'k' -> BLACK_KING_PST[row][file];
            default -> 0;
        };
    }


}
