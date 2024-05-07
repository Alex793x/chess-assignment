package chess.board.enums;

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
        return switch (Character.toUpperCase(c)) {
            case 'K' -> KING;
            case 'Q' -> QUEEN;
            case 'R' -> ROOK;
            case 'B' -> BISHOP;
            case 'N' -> KNIGHT;
            case 'P' -> PAWN;
            default -> throw new IllegalArgumentException("Invalid piece character: " + c);
        };
    }

}