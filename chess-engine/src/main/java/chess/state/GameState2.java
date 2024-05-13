package chess.state;

import chess.board.Bitboard;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import chess.engine.pre_computations.PreComputationHandler;

public class GameState2 {

    public static boolean isCheck(Board board, PieceColor color) {
        Bitboard bitboard = board.getBitboard();
        int kingPosition = board.getKingPosition(color);

        // Determine the enemy color
        PieceColor enemyColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        // Check for pawn attacks (opposite pawn attacks towards the king's position)
        long enemyPawnAttacks = (enemyColor == PieceColor.WHITE)
                ? PreComputationHandler.WHITE_PAWN_ATTACKS[kingPosition]
                : PreComputationHandler.BLACK_PAWN_ATTACKS[kingPosition];

        if ((enemyPawnAttacks & bitboard.getBitboardForPieceTypeAndColor(PieceType.PAWN, enemyColor)) != 0) {
            return true;
        }

        // Check for knight attacks
        if ((PreComputationHandler.KNIGHT_ATTACKS[kingPosition] & bitboard.getBitboardForPieceTypeAndColor(PieceType.KNIGHT, enemyColor)) != 0) {
            return true;
        }

        // Determine all occupied squares on the board for attack calculations
        long allOccupancies = bitboard.getAllOccupancies();

        // Check for bishop or queen attacks from diagonals
        long bishopAndQueenOccupancies = bitboard.getBitboardForPieceTypeAndColor(PieceType.BISHOP, enemyColor) |
                bitboard.getBitboardForPieceTypeAndColor(PieceType.QUEEN, enemyColor);

        if ((PreComputationHandler.getBishopAttacks(kingPosition, allOccupancies) & bishopAndQueenOccupancies) != 0) {
            return true;
        }

        // Check for rook or queen attacks from straight lines
        long rookAndQueenOccupancies = bitboard.getBitboardForPieceTypeAndColor(PieceType.ROOK, enemyColor) |
                bitboard.getBitboardForPieceTypeAndColor(PieceType.QUEEN, enemyColor);

        if ((PreComputationHandler.getRookAttacks(kingPosition, allOccupancies) & rookAndQueenOccupancies) != 0) {
            return true;
        }

        // Check for king attacks
        return (PreComputationHandler.KING_ATTACKS[kingPosition] & bitboard.getBitboardForPieceTypeAndColor(PieceType.KING, enemyColor)) != 0;
    }
}
