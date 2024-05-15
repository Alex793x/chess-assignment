package model.enums;


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
    KING(0, 0);

    private final int midGameValue;
    private final int endGameValue;
}
