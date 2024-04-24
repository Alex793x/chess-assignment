package org.kea.chessbackend.chess.engine.move_validation.interfaces;

public interface PieceValidator {

    static boolean isWithinBoardBounds(int square) {
        return square >= 0 && square < 64;
    }
}
