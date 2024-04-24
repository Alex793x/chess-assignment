package org.kea.chessbackend.chess.models.enums;

// Enum representing the piece colors
    public enum PieceColor {
        WHITE, BLACK;


    public static PieceColor fromFENChar(char c) {
        return Character.isUpperCase(c) ? WHITE : BLACK;
    }
    }