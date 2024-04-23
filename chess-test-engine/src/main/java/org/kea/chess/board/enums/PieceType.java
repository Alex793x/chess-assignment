package org.kea.chess.board.enums;

import lombok.Getter;

@Getter
public enum PieceType {
    PAWN(82, 94),
    KNIGHT(337, 281),
    BISHOP(365, 297),
    ROOK(477, 512),
    QUEEN(1025, 936),
    KING(12000, 12000);

    private final int midGameValue;
    private final int endGameValue;

    PieceType(int midGameValue, int endGameValue) {
        this.midGameValue = midGameValue;
        this.endGameValue = endGameValue;
    }

}