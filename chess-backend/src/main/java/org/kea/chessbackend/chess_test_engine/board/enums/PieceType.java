package org.kea.chessbackend.chess_test_engine.board.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PieceType {
    PAWN(82, 94),
    KNIGHT(337, 281),
    BISHOP(365, 297),
    ROOK(477, 512),
    QUEEN(1025, 936),
    KING(12000, 12000);

    private final int midGameValue;
    private final int endGameValue;

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