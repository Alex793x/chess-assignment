package util.attack_vectors;

import model.enums.PieceColor;
import model.enums.PieceType;
import util.precompuation.PreComputedData;

import java.util.BitSet;

public class AttackVectorsHandler {

    public static BitSet calculateAttackVectorsForPiece(PieceType pieceType, int position, PieceColor color, BitSet allPieces) {
        switch (pieceType) {
            case PAWN:
                return calculatePawnAttacks(position, color);
            case KNIGHT:
                return calculateKnightAttacks(position);
            case BISHOP:
                return calculateSlidingPieceAttacks(position, new int[]{-9, -7, 7, 9}, allPieces);
            case ROOK:
                return calculateSlidingPieceAttacks(position, new int[]{-8, 8, -1, 1}, allPieces);
            case QUEEN:
                BitSet queenAttacks = calculateSlidingPieceAttacks(position, new int[]{-9, -7, 7, 9}, allPieces);
                queenAttacks.or(calculateSlidingPieceAttacks(position, new int[]{-8, 8, -1, 1}, allPieces));
                return queenAttacks;
            case KING:
                return (BitSet) PreComputedData.kingMoves[position].clone();
            default:
                return new BitSet(64);
        }
    }


    public static BitSet calculateMovesForPiece(PieceType pieceType, int position, PieceColor color, BitSet allPieces, BitSet emptySquares) {
        switch (pieceType) {
            case PAWN:
                return calculatePawnMoves(position, color, emptySquares, allPieces);
            case KNIGHT:
                return PreComputedData.knightMoves[position]; // Knights can jump, no need to check for empty squares
            case BISHOP:
                return calculateSlidingPieceMoves(position, new int[]{-9, -7, 7, 9}, allPieces, emptySquares);
            case ROOK:
                return calculateSlidingPieceMoves(position, new int[]{-8, 8, -1, 1}, allPieces, emptySquares);
            case QUEEN:
                // Queens combine rook and bishop movements
                BitSet queenMoves = calculateSlidingPieceMoves(position, new int[]{-9, -7, 7, 9}, allPieces, emptySquares);
                queenMoves.or(calculateSlidingPieceMoves(position, new int[]{-8, 8, -1, 1}, allPieces, emptySquares));
                return queenMoves;
            case KING:
                return PreComputedData.kingMoves[position]; // Kings can move one square any direction, no need to check for empty squares
            default:
                return new BitSet(64);
        }
    }

    private static BitSet calculateSlidingPieceAttacks(int position, int[] directions, BitSet allPieces) {
        BitSet attacks = new BitSet(64);
        int startRow = position / 8;
        int startCol = position % 8;
        for (int direction : directions) {
            int row = startRow;
            int col = startCol;
            while (true) {
                row += direction / 8;
                col += direction % 8;
                if (row < 0 || row > 7 || col < 0 || col > 7) break;
                int newIndex = row * 8 + col;
                attacks.set(newIndex);
                // Stop if a piece is encountered
                if (allPieces.get(newIndex)) break;
            }
        }
        return attacks;
    }

    private static BitSet calculatePawnAttacks(int position, PieceColor color) {
        BitSet attacks = new BitSet(64);
        int row = position / 8;
        int col = position % 8;
        int rowDirection = color == PieceColor.WHITE ? -1 : 1; // White pawns move up, Black pawns move down
        int[] cols = {col - 1, col + 1};

        for (int newCol : cols) {
            int newRow = row + rowDirection;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                int attackPosition = newRow * 8 + newCol;
                attacks.set(attackPosition);
            }
        }
        return attacks;
    }


    // Sliding moves account for both attacks and legal moves into empty squares
    private static BitSet calculateSlidingPieceMoves(int position, int[] directions, BitSet allPieces, BitSet emptySquares) {
        BitSet moves = new BitSet(64);
        int startRow = position / 8;
        int startCol = position % 8;
        for (int direction : directions) {
            int row = startRow;
            int col = startCol;
            while (true) {
                row += direction / 8;
                col += direction % 8;
                if (row < 0 || row > 7 || col < 0 || col > 7) break;
                int newIndex = row * 8 + col;
                moves.set(newIndex);
                // Stop expanding this direction if a piece is encountered
                if (allPieces.get(newIndex)) break;
            }
        }
        return moves;
    }

    // Specific to pawns, handles both capture and non-capture moves
    private static BitSet calculatePawnMoves(int position, PieceColor color, BitSet emptySquares, BitSet allPieces) {
        BitSet moves = new BitSet(64);
        int startRow = position / 8;
        int startCol = position % 8;
        int rowDirection = color == PieceColor.WHITE ? -1 : 1;

        // Standard single move forward
        int forwardOne = (startRow + rowDirection) * 8 + startCol;
        // Check if the forward move is within board boundaries before accessing BitSet
        if (forwardOne >= 0 && forwardOne < 64) {
            if (emptySquares.get(forwardOne)) {
                moves.set(forwardOne);
                // Double move from start position
                if ((color == PieceColor.WHITE && startRow == 6) || (color == PieceColor.BLACK && startRow == 1)) {
                    int forwardTwo = (startRow + 2 * rowDirection) * 8 + startCol;
                    if (emptySquares.get(forwardTwo)) {
                        moves.set(forwardTwo);
                    }
                }
            }
        }

        // Capture moves
        int[] cols = {startCol - 1, startCol + 1};
        for (int newCol : cols) {
            if (newCol >= 0 && newCol < 8) {
                int newRow = startRow + rowDirection;
                if (newRow >= 0 && newRow < 8) {
                    int capturePos = newRow * 8 + newCol;
                    if (allPieces.get(capturePos) && !emptySquares.get(capturePos)) {
                        moves.set(capturePos);
                    }
                }
            }
        }

        return moves;
    }


    private static BitSet calculateKnightAttacks(int position) {
        BitSet attacks = new BitSet(64);
        int row = position / 8;
        int col = position % 8;
        int[] rowOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] colOffsets = {1, 2, 2, 1, -1, -2, -2, -1};
        for (int i = 0; i < 8; i++) {
            int newRow = row + rowOffsets[i];
            int newCol = col + colOffsets[i];
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                attacks.set(newRow * 8 + newCol);
            }
        }
        return attacks;
    }
}
