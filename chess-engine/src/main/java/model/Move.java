package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
public class Move {

    public int[] sourcePosition;
    public int[] destinationPosition;
    public char piece;
    public char capturedPiece = '\u0000';
    public char promotionPieceType;
    public boolean isPromotion = false;

    @Override
    public String toString() {
        return "Move{" +
                "sourcePosition=" + Arrays.toString(sourcePosition) +
                ", destinationPosition=" + Arrays.toString(destinationPosition) +
                ", piece=" + piece +
                ", capturedPiece=" + capturedPiece +
                ", promotionPieceType=" + promotionPieceType +
                ", isPromotion=" + isPromotion +
                '}';
    }
}
