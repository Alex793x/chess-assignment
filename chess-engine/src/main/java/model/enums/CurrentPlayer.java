package model.enums;

public enum CurrentPlayer {
    WHITE,
    BLACK;

    // Get opposite player
    public CurrentPlayer getOppositePlayer() {
        return this == WHITE ? BLACK : WHITE;
    }

    public PieceColor getPieceColor() {
        return this == WHITE ? PieceColor.WHITE : PieceColor.BLACK;
    }
}
