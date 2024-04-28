package chess.engine.evaluation.piece_board_evaluation;


import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

public class PieceSquareEvaluation {

    public static int evaluatePiecePosition(Board board, int[] pieceSquareTable, PieceType pieceType) {
        int score = 0;
        for (int square = 0; square < 64; ++square) {
            if (board.getPieceTypeAtSquare(square) == pieceType) {
                PieceColor color = board.getPieceColorAtSquare(square);
                int index = (color == PieceColor.WHITE) ? square : 63 - square; // Flip index for black
                score += pieceSquareTable[index];
            }
        }
        return score;
    }
}
