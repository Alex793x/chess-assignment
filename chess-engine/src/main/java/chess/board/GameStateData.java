package chess.board;

import chess.board.enums.PieceColor;

public class GameStateData {
    private PieceColor currentPlayer;
    private Bitboard bitboard;
    private int kingPosition;

    public GameStateData(PieceColor currentPlayer, Bitboard bitboard, int kingPosition) {
        this.currentPlayer = currentPlayer;
        this.bitboard = bitboard;
        this.kingPosition = kingPosition;
    }

    // Getters for the fields
    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    public Bitboard getBitboard() {
        return bitboard;
    }

    public int getKingPosition() {
        return kingPosition;
    }
}
