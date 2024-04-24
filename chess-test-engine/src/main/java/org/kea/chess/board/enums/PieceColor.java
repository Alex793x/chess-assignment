package org.kea.chess.board.enums;

public enum PieceColor {
    WHITE, BLACK;
    public static PieceColor fromFENChar(char c) {
        return Character.isUpperCase(c) ? WHITE : BLACK;
    }

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}