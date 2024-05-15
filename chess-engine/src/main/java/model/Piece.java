package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import model.enums.PieceColor;
import model.enums.PieceType;

@Getter
@Setter
@AllArgsConstructor
public class Piece {

    private PieceType pieceType;
    private PieceColor pieceColor;
    private int square;

    public int getPieceValue(boolean isMidgame) {
        return isMidgame 
                ? pieceType.getMidGameValue()
                : pieceType.getEndGameValue();
    }

    @Override
    public String toString() {
        return "Piece{" +
                "pieceType=" + pieceType +
                ", pieceColor=" + pieceColor +
                ", square=" + square +
                '}';
    }
}
