package chess.board;

import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;
import lombok.Getter;

@Getter
public class Move {
    private final int fromSquare;
    private final int toSquare;
    private final PieceType pieceType;
    private final PieceType capturedPieceType;  // Null if no capture
    private final PieceColor pieceColor;

    // Constructor and getters
    public Move(int fromSquare, int toSquare, PieceType pieceType, PieceType capturedPieceType, PieceColor pieceColor) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.pieceType = pieceType;
        this.capturedPieceType = capturedPieceType;
        this.pieceColor = pieceColor;
    }
}
