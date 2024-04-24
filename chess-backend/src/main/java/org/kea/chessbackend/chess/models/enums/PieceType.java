package org.kea.chessbackend.chess.models.enums;

public enum PieceType {
        PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;


    public static PieceType fromFENChar(char c) {
        switch (Character.toUpperCase(c)) {
            case 'K': return KING;
            case 'Q': return QUEEN;
            case 'R': return ROOK;
            case 'B': return BISHOP;
            case 'N': return KNIGHT;
            case 'P': return PAWN;
            default: throw new IllegalArgumentException("Invalid piece character: " + c);
        }
    }
    }

