package org.kea.chess.board.enums;

public enum PieceColor {
    WHITE, BLACK;

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}