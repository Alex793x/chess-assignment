package evaluation;

import static evaluation.pst.PSTHandler.getPiecePositionPSTValue;

public class PieceEvaluator {
    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 300;
    private static final int BISHOP_VALUE = 300;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 12000;

    public int evaluate(char[][] board) {
        int whiteScore = 0;
        int blackScore = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                char piece = board[row][col];
                if (piece != ' ') {
                    int pieceValue = getPieceValue(piece);
                    int positionValue = getPiecePositionPSTValue(row, col, piece);

                    if (Character.isUpperCase(piece)) {
                        whiteScore += pieceValue + positionValue;
                    } else {
                        blackScore += pieceValue + positionValue;
                    }
                }
            }
        }
        return whiteScore - blackScore;
    }

    public static int getPieceValue(char piece) {
        return switch (Character.toLowerCase(piece)) {
            case 'p' -> PAWN_VALUE;
            case 'n' -> KNIGHT_VALUE;
            case 'b' -> BISHOP_VALUE;
            case 'r' -> ROOK_VALUE;
            case 'q' -> QUEEN_VALUE;
            case 'k' -> KING_VALUE;
            default -> 0;
        };
    }
}
