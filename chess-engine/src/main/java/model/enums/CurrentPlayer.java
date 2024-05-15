package model.enums;

public enum CurrentPlayer {
    WHITE,
    BLACK;

    // Get opposite player
    public CurrentPlayer getOppositePlayer() {
        return this == WHITE ? BLACK : WHITE;
    }
}
