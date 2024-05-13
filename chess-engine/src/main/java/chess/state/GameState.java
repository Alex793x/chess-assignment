package chess.state;

import chess.ai_player.move_generation.KingMoveGenerator;
import chess.ai_player.move_generation.KnightMoveGenerator;
import chess.ai_player.move_generation.PawnMoveGenerator;
import chess.ai_player.move_generation.SlidingPieceMoveGenerator;
import chess.board.Bitboard;

import chess.board.Board;
import chess.board.GameStateData;
import chess.board.Move;
import chess.board.enums.PieceColor;
import chess.board.enums.PieceType;

import java.util.List;

public class GameState {
    private final Bitboard bitboard;
    private final PieceColor currentPlayer;
    private final int kingPosition;

    private final KingMoveGenerator kingMoveGenerator;

    // Constructor
    public GameState(GameStateData gameStateData) {
        this.bitboard = gameStateData.getBitboard();
        this.currentPlayer = gameStateData.getCurrentPlayer();
        this.kingPosition = gameStateData.getKingPosition();
        kingMoveGenerator = new KingMoveGenerator();
    }

    public boolean isKingInCheck() {
        if (kingPosition == -1) {
            //System.err.println("King not found for " + currentPlayer);
            return false;  // or handle appropriately
        }
        return isKingPositionUnderAttack(kingPosition, currentPlayer.opposite());
    }

    public boolean isKingInCheckmate() {
        if (!isKingInCheck()) {
            return false;
        }

        List<Move> possibleMoves = kingMoveGenerator.generateMovesForKing(bitboard, kingPosition, currentPlayer);

        // Check if there's any valid move where the king is not under attack
        for (Move move : possibleMoves) {
            if (!isKingPositionUnderAttack(move.getToSquare(), currentPlayer.opposite())) {
                return false;
            }
        }
        return true; // No valid moves where the king is not under attack
    }

    private boolean isKingPositionUnderAttack(int position, PieceColor enemyColor) {
        long threatBitboard = 0L;

        // Include all enemy piece moves that could potentially attack the given position
        threatBitboard |= getThreatBitboard(PieceType.PAWN, enemyColor);
        threatBitboard |= getThreatBitboard(PieceType.KNIGHT, enemyColor);
        threatBitboard |= getThreatBitboard(PieceType.BISHOP, enemyColor);
        threatBitboard |= getThreatBitboard(PieceType.ROOK, enemyColor);
        threatBitboard |= getThreatBitboard(PieceType.QUEEN, enemyColor);

        // Check if the position is under threat
        return (threatBitboard & (1L << position)) != 0;
    }

    private long getThreatBitboard(PieceType pieceType, PieceColor enemyColor) {
        long pieces = bitboard.getBitboardForPieceTypeAndColor(pieceType, enemyColor);
        long movesBitboard = 0L;

        while (pieces != 0) {
            int fromSquare = Long.numberOfTrailingZeros(pieces);
            pieces &= ~(1L << fromSquare); // Clear the bit to move to the next piece

            List<Move> moves = switch (pieceType) {
                case PAWN -> new PawnMoveGenerator(bitboard).generateMovesForPawn(fromSquare, enemyColor);
                case KNIGHT -> new KnightMoveGenerator(bitboard).generateMovesForKnight(fromSquare, enemyColor);
                default -> // For BISHOP, ROOK, QUEEN
                        new SlidingPieceMoveGenerator(bitboard).generateMovesForSlidingPiece(fromSquare, enemyColor, pieceType);
            };

            for (Move move : moves) {
                movesBitboard |= 1L << move.getToSquare();
            }
        }

        return movesBitboard;
    }


}
