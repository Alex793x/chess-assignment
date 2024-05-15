package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Move {
    private int fromSquare;
    private int toSquare;
    private int positionGain;
    private Piece piece;
    private Piece capturedPiece;
    private boolean isCapture; // This flag is set true if a piece is captured
    private boolean isPromotion; // This flag is set true if a pawn can be promoted in this move
    private int halfMoveClockBeforeMove; // To handle the fifty-move rule in chess
    private boolean isProtected;
    private boolean isAttacked;
    private int attackPenalty;

    @Override
    public String toString() {
        return "Move{" +
                "fromSquare=" + fromSquare +
                ", toSquare=" + toSquare +
                ", positionGain=" + positionGain +
                ", piece=" + (piece != null ? piece : "None") +
                ", capturedPiece=" + (capturedPiece != null ? capturedPiece : "None") +
                ", isCapture=" + isCapture +
                ", isPromotion=" + isPromotion +
                ", halfMoveClockBeforeMove=" + halfMoveClockBeforeMove +
                ", isProtected=" + isProtected +
                ", isAttacked=" + isAttacked +
                ", attackPenalty=" + attackPenalty +
                '}';
    }
}
