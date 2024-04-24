package org.kea.chessbackend.chess.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kea.chessbackend.chess.models.enums.PieceColor;
import org.kea.chessbackend.chess.models.enums.PieceType;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@EqualsAndHashCode
@Document(value = "board")
public class Board {

    private Bitboard bitboard;
    private PieceColor currentPlayer;
    private boolean check;
    private boolean checkmate;

    public Board() {
        this.bitboard = new Bitboard();
        this.currentPlayer = PieceColor.WHITE;
        this.check = false;
        this.checkmate = false;
        initializeBoard();
    }

    /**
     * The initialization of the boards ensures each position of
     */
    private void initializeBoard() {
        // Set up the initial positions of all pieces using the bitboards

        // White pieces
        bitboard.placePieceOnSquare(0, PieceType.ROOK, PieceColor.WHITE);
        bitboard.placePieceOnSquare(1, PieceType.KNIGHT, PieceColor.WHITE);
        bitboard.placePieceOnSquare(2, PieceType.BISHOP, PieceColor.WHITE);
        bitboard.placePieceOnSquare(3, PieceType.QUEEN, PieceColor.WHITE);
        bitboard.placePieceOnSquare(4, PieceType.KING, PieceColor.WHITE);
        bitboard.placePieceOnSquare(5, PieceType.BISHOP, PieceColor.WHITE);
        bitboard.placePieceOnSquare(6, PieceType.KNIGHT, PieceColor.WHITE);
        bitboard.placePieceOnSquare(7, PieceType.ROOK, PieceColor.WHITE);
        for (int i = 8; i < 16; i++) {
            bitboard.placePieceOnSquare(i, PieceType.PAWN, PieceColor.WHITE);
        }

        // Black pieces
        for (int i = 48; i < 56; i++) {
            bitboard.placePieceOnSquare(i, PieceType.PAWN, PieceColor.BLACK);
        }
        bitboard.placePieceOnSquare(56, PieceType.ROOK, PieceColor.BLACK);
        bitboard.placePieceOnSquare(57, PieceType.KNIGHT, PieceColor.BLACK);
        bitboard.placePieceOnSquare(58, PieceType.BISHOP, PieceColor.BLACK);
        bitboard.placePieceOnSquare(59, PieceType.QUEEN, PieceColor.BLACK);
        bitboard.placePieceOnSquare(60, PieceType.KING, PieceColor.BLACK);
        bitboard.placePieceOnSquare(61, PieceType.BISHOP, PieceColor.BLACK);
        bitboard.placePieceOnSquare(62, PieceType.KNIGHT, PieceColor.BLACK);
        bitboard.placePieceOnSquare(63, PieceType.ROOK, PieceColor.BLACK);
    }

    public void movePiece(int fromSquare, int toSquare) {
        PieceType pieceType = getPieceTypeAtSquare(fromSquare);
        PieceColor pieceColor = getPieceColorAtSquare(fromSquare);

        bitboard.removePieceFromSquare(fromSquare, pieceType, pieceColor);
        bitboard.placePieceOnSquare(toSquare, pieceType, pieceColor);
    }

    public PieceType getPieceTypeAtSquare(int square) {
        for (PieceType pieceType : PieceType.values()) {
            if (bitboard.isSquareOccupiedByPiece(square, pieceType, PieceColor.WHITE) ||
                    bitboard.isSquareOccupiedByPiece(square, pieceType, PieceColor.BLACK)) {
                return pieceType;
            }
        }
        return null;
    }

    public PieceColor getPieceColorAtSquare(int square) {
        for (PieceColor pieceColor : PieceColor.values()) {
            for (PieceType pieceType : PieceType.values()) {
                if (bitboard.isSquareOccupiedByPiece(square, pieceType, pieceColor)) {
                    return pieceColor;
                }
            }
        }
        return null;
    }

    public void updateGameState(PieceColor nextPlayer, boolean isCheck, boolean isCheckmate) {
        this.currentPlayer = nextPlayer;
        this.check = isCheck;
        this.checkmate = isCheckmate;
    }

    public boolean isWhite() {
        return currentPlayer == PieceColor.WHITE;
    }

}