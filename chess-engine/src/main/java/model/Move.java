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
    public boolean isPromotion = false;
    public int value;
    private char promotionPiece;

    public Move(int fromNum, int fromChar, int toNum, int toChar, char piece) {
        this.sourcePosition = new int[]{fromNum, fromChar};
        this.destinationPosition = new int[]{toNum, toChar};
        this.piece = piece;
    }




    @Override
    public String toString() {
        return "Move{" +
                "sourcePosition=" + Arrays.toString(sourcePosition) +
                ", destinationPosition=" + Arrays.toString(destinationPosition) +
                ", piece=" + piece +
                ", capturedPiece=" + capturedPiece +
                ", isPromotion=" + isPromotion +
                ", value=" + value +
                ", promotionPiece=" + promotionPiece +
                '}';
    }
}
