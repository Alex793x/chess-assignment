package org.kea.chessbackend.chess.engine.move_validation.service;

import lombok.RequiredArgsConstructor;
import org.kea.chessbackend.chess.board.Board;
import org.kea.chessbackend.chess.board.enums.PieceColor;
import org.kea.chessbackend.chess.engine.move_validation.interfaces.PieceValidator;
import org.kea.chessbackend.chess.engine.move_validation.piece_validators.*;

@RequiredArgsConstructor
public final class MoveValidator implements PieceValidator {

    public static boolean validatePawnMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return PawnValidator.isValidPawnMove(board, fromSquare, toSquare, playerColor);
    }

    public static boolean validateKnightMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return KnightValidator.isValidKnightMove(board, fromSquare, toSquare, playerColor);
    }

    public static boolean validateBishopMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return BishopValidator.isValidBishopMove(board, fromSquare, toSquare, playerColor);
    }

    public static boolean validateRookMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return RookValidator.isValidRookMove(board, fromSquare, toSquare, playerColor);
    }

    public static boolean validateQueenMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return QueenValidator.isValidQueenMove(board, fromSquare, toSquare, playerColor);
    }

    public static boolean validateKingMoves(Board board, int fromSquare, int toSquare, PieceColor playerColor) {
        return KingValidator.isValidKingMove(board, fromSquare, toSquare, playerColor);
    }

}

