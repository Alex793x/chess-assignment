package engine.evaluations.protection_board;

import model.Bitboards;
import model.Piece;
import model.enums.PieceColor;

public class ProtectionEvaluator {
    
    public static int calculateProtectionScore(Bitboards bitboards) {
        int[] protectionLevels = bitboards.calculateProtectionLevels();
        int whiteProtection = 0;
        int blackProtection = 0;

        // Aggregate the protection levels for each side
        for (int i = 0; i < 64; i++) {
            Piece piece = bitboards.getPieceBySquare(i);
            if (piece != null) {
                if (piece.getPieceColor() == PieceColor.WHITE) {
                    whiteProtection += protectionLevels[i];
                } else if (piece.getPieceColor() == PieceColor.BLACK) {
                    blackProtection += protectionLevels[i];
                }
            }
        }

        return whiteProtection - blackProtection; // Higher mobility for white is positive, for black is negative
    }
}
