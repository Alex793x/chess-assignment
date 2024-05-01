package chess.state;

import chess.ai_player.move_generation.KingMoveGenerator;
import chess.ai_player.move_generation.KnightMoveGenerator;
import chess.ai_player.move_generation.PawnMoveGenerator;
import chess.ai_player.move_generation.SlidingPieceMoveGenerator;
import chess.board.Board;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

import java.util.List;


public class GameState {
    private Board board;
    private PieceColor currentPlayer;

    // Constructor
    public GameState(Board board) {
        this.board = board;
        this.currentPlayer = PieceColor.WHITE;
    }

    public boolean isKingInCheck() {
        int kingPosition = board.getKingPositiion(currentPlayer);
        return isPositionUnderAttack(kingPosition, currentPlayer.opposite());
    }

    public boolean isKingInCheckmate() {
        if (!isKingInCheck()) {
            return false;
        }

        // Check if any move can remove the check
        return !canKingEscape();
    }


    /**
     * Checks if a given position on the board is under attack by any piece of the specified enemy color.
     *
     * @param position The index of the square to check for threats.
     * @param enemyColor The color of the potential attacking pieces.
     * @return true if the position is under attack, false otherwise.
     */
    private boolean isPositionUnderAttack(int position, PieceColor enemyColor) {
        boolean isUnderAttack = false;
        for (PieceType type : PieceType.values()) {
            boolean result = canBeAttackedBy(position, type, enemyColor);
            System.out.println("Checking attack by " + type + ": " + result);
            if (result) {
                isUnderAttack = true;
            }
        }
        return isUnderAttack;
    }

    private boolean canBeAttackedBy(int position, PieceType pieceType, PieceColor enemyColor) {
        // Create a move generator based on the piece type
        return switch (pieceType) {
            case PAWN -> new PawnMoveGenerator(board).generateThreatsForPawn(position, enemyColor);
            case KNIGHT -> new KnightMoveGenerator(board).generateThreatsForKnight(position, enemyColor);
            case BISHOP -> new SlidingPieceMoveGenerator(board).generateThreatsForBishop(position, enemyColor);
            case ROOK -> new SlidingPieceMoveGenerator(board).generateThreatsForRook(position, enemyColor);
            case QUEEN -> new SlidingPieceMoveGenerator(board).generateThreatsForQueen(position, enemyColor);
            case KING -> new KingMoveGenerator(board).generateThreatsForKing(position, enemyColor);
        };
    }

    private boolean canKingEscape() {
        int kingPosition = board.getKingPositiion(currentPlayer);
        List<Integer> possibleMoves = new KingMoveGenerator(board).generateMovesForKing(kingPosition, currentPlayer);

        System.out.println("Evaluating escape moves for king at " + kingPosition + ": " + possibleMoves);

        for (int newPosition : possibleMoves) {
            if (!isPositionUnderAttack(newPosition, currentPlayer.opposite())) {
                System.out.println("King can escape to: " + newPosition);
                return true;
            } else {
                System.out.println("Position " + newPosition + " is under attack.");
            }
        }
        return false;
    }

}
